package com.example.tp2

import android.app.Application
import com.example.tp2.di.AppContainer

class PatientsApplication : Application() {
    val container by lazy { AppContainer() }
}