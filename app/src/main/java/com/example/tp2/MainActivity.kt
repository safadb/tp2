package com.example.tp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.tp2.ui.patients.PatientsScreen
import com.example.tp2.ui.patients.ConsultationsScreen
import com.example.tp2.ui.theme.Tp2Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            Tp2Theme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "patients"
                ) {

                    // Écran des patients
                    composable("patients") {

                        PatientsScreen(
                            onConsultations = { patientId, patientName ->

                                navController.navigate(
                                    "consultations/$patientId/${android.net.Uri.encode(patientName)}"
                                )
                            }
                        )
                    }

                    // Écran des consultations
                    composable(
                        route = "consultations/{patientId}/{patientName}",
                        arguments = listOf(
                            navArgument("patientId") {
                                type = NavType.StringType
                            },
                            navArgument("patientName") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->

                        val patientId =
                            backStackEntry.arguments?.getString("patientId") ?: ""

                        val patientName =
                            backStackEntry.arguments?.getString("patientName") ?: ""

                        ConsultationsScreen(
                            patientId = patientId,
                            patientName = patientName,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}