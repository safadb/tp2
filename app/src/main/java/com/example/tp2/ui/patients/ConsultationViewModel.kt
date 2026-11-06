package com.example.tp2.ui.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

import com.example.tp2.PatientsApplication
import com.example.tp2.data.model.Consultation
import com.example.tp2.data.repository.ConsultationRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultationViewModel(
    private val repository: ConsultationRepository,
    private val patientId: String
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ConsultationUiState>(
            ConsultationUiState.Loading
        )

    val uiState: StateFlow<ConsultationUiState> =
        _uiState.asStateFlow()

    init {
        loadConsultations()
    }

    fun loadConsultations() {
        _uiState.value = ConsultationUiState.Loading

        viewModelScope.launch {
            repository.getConsultations(patientId)
                .onSuccess { consultations ->
                    _uiState.value =
                        ConsultationUiState.Success(consultations)
                }
                .onFailure { e ->
                    _uiState.value =
                        ConsultationUiState.Error(
                            e.message ?: "Erreur inconnue"
                        )
                }
        }
    }

    companion object {
        fun Factory(
            patientId: String
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[
                    ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
                ] as PatientsApplication

                ConsultationViewModel(
                    app.container.consultationRepository,
                    patientId
                )
            }
        }
    }
}