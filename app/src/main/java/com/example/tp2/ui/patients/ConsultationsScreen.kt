package com.example.tp2.ui.patients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp2.data.model.Consultation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationsScreen(
    patientId: String,
    patientName: String,
    onBack: () -> Unit,
    viewModel: ConsultationViewModel = viewModel(
        key = "consultations_$patientId",
        factory = ConsultationViewModel.Factory(patientId)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Consultations - $patientName")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.loadConsultations() }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Actualiser"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {

                is ConsultationUiState.Loading ->
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )

                is ConsultationUiState.Error ->
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.loadConsultations()
                            }
                        ) {
                            Text("Réessayer")
                        }
                    }

                is ConsultationUiState.Success ->
                    ConsultationList(
                        consultations = state.consultations
                    )
            }
        }
    }
}

@Composable
fun ConsultationList(
    consultations: List<Consultation>
) {
    if (consultations.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Aucune consultation")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(consultations, key = { it.id }) { consultation ->
                ConsultationCard(consultation)
            }
        }
    }
}

@Composable
fun ConsultationCard(
    consultation: Consultation
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = consultation.motif,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Date : ${consultation.date}")

            Text("Médecin : ${consultation.medecin}")

            consultation.traitement?.let {
                Text("Traitement : $it")
            }

            consultation.notes?.let {
                Text("Notes : $it")
            }

            Text(
                text = "Statut : ${consultation.statut}",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}