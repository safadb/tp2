package com.example.tp2.data.repository

import com.example.tp2.data.model.Consultation
import com.example.tp2.data.model.ApiError
import com.example.tp2.data.remote.ConsultationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

class ConsultationRepository(
    private val api: ConsultationApi,
    private val json: Json
) {

    suspend fun getConsultations(
        patientId: String
    ): Result<List<Consultation>> =
        safeCall {
            api.getConsultations(patientId)
        }

    private suspend fun <T> safeCall(
        call: suspend () -> T
    ): Result<T> =
        try {
            Result.success(call())

        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string()

            val apiError = body?.let {
                runCatching {
                    json.decodeFromString(
                        ApiError.serializer(),
                        it
                    )
                }.getOrNull()
            }

            Result.failure(
                Exception(
                    apiError?.message ?: "Erreur ${e.code()}"
                )
            )

        } catch (e: IOException) {
            Result.failure(
                Exception("Serveur injoignable.")
            )

        } catch (e: SerializationException) {
            Result.failure(
                Exception("Réponse inattendue du serveur.")
            )
        }
}