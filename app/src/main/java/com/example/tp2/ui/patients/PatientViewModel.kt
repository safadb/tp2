package com.example.tp2.ui.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

import com.example.tp2.PatientsApplication
import com.example.tp2.data.model.Patient
import com.example.tp2.data.model.PatientRequest
import com.example.tp2.data.repository.PatientRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PatientViewModel(
    private val repository: PatientRepository
) : ViewModel() {

    // État de l'écran
    private val _uiState =
        MutableStateFlow<PatientsUiState>(PatientsUiState.Loading)

    val uiState: StateFlow<PatientsUiState> = _uiState.asStateFlow()

    // Messages ponctuels (Snackbar)
    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    // Filtre par statut
    private val _selectedStatut = MutableStateFlow<String?>(null)
    val selectedStatut: StateFlow<String?> = _selectedStatut.asStateFlow()

    init {
        loadPatients()
    }

    // Charger les patients selon le statut choisi
    fun loadPatients() {
        _uiState.value = PatientsUiState.Loading

        viewModelScope.launch {
            repository.getPatients(_selectedStatut.value)
                .onSuccess { patients ->
                    _uiState.value = PatientsUiState.Success(patients)
                }
                .onFailure { e ->
                    _uiState.value =
                        PatientsUiState.Error(e.message ?: "Erreur inconnue")
                }
        }
    }

    // Changer le filtre
    fun filterByStatut(statut: String?) {
        _selectedStatut.value = statut
        loadPatients()
    }

    // Ajouter un patient
    fun addPatient(request: PatientRequest) {
        viewModelScope.launch {
            repository.createPatient(request)
                .onSuccess { patient ->
                    updateList { it + patient }
                    _messages.emit(
                        "${patient.prenom} ${patient.nom} ajouté"
                    )
                }
                .onFailure { e ->
                    _messages.emit(e.message ?: "Ajout impossible")
                }
        }
    }

    // Modifier un patient (PUT)
    fun updatePatient(patient: Patient, request: PatientRequest) {
        viewModelScope.launch {
            repository.updatePatient(patient.id, request)
                .onSuccess { updatedPatient ->
                    updateList { list ->
                        list.map {
                            if (it.id == updatedPatient.id) updatedPatient
                            else it
                        }
                    }

                    _messages.emit(
                        "${updatedPatient.prenom} ${updatedPatient.nom} modifié"
                    )
                }
                .onFailure { e ->
                    _messages.emit(
                        e.message ?: "Modification impossible"
                    )
                }
        }
    }

    // Supprimer un patient (DELETE)
    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            repository.deletePatient(patient.id)
                .onSuccess {
                    updateList { list ->
                        list.filterNot { it.id == patient.id }
                    }

                    _messages.emit(
                        "${patient.prenom} ${patient.nom} supprimé"
                    )
                }
                .onFailure { e ->
                    _messages.emit(
                        e.message ?: "Suppression impossible"
                    )
                }
        }
    }

    // Modifier la liste affichée sans recharger depuis le serveur
    private fun updateList(transform: (List<Patient>) -> List<Patient>) {
        _uiState.update { state ->
            if (state is PatientsUiState.Success)
                state.copy(patients = transform(state.patients))
            else
                state
        }
    }

    // Factory : construction du ViewModel
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[
                    ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as PatientsApplication

                PatientViewModel(app.container.patientRepository)
            }
        }
    }
}