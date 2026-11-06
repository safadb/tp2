package com.example.tp2.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Consultation(
    val id: String,
    val patient: PatientInfo,
    val date: String,
    val motif: String,
    val traitement: String? = null,
    val medecin: String,
    val notes: String? = null,
    val statut: String = "En attente"
)

@Serializable
data class PatientInfo(
    val id: String,
    val nom: String,
    val prenom: String
)