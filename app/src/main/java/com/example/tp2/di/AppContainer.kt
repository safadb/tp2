package com.example.tp2.di

import com.example.tp2.data.remote.PatientApi
import com.example.tp2.data.remote.ConsultationApi

import com.example.tp2.data.repository.PatientRepository
import com.example.tp2.data.repository.ConsultationRepository

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://10.0.2.2:5000/api/"

class AppContainer {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(
            json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
        .build()

    private val patientApi =
        retrofit.create(PatientApi::class.java)

    private val consultationApi =
        retrofit.create(ConsultationApi::class.java)

    val patientRepository =
        PatientRepository(patientApi, json)

    val consultationRepository =
        ConsultationRepository(consultationApi, json)
}