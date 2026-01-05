package com.example.fespace.view.auth

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fespace.viewmodel.AuthViewModel
import com.example.fespace.view.auth.RegisterScreen

@Composable
fun AuthRoute(navController: NavHostController) {

    // 1️⃣ Buat ViewModel DI SINI
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // ======================
        // LOGIN
        // ======================
        composable("login") {
            LoginScreen(
                navController = navController, // <--- ADD THIS LINE
                viewModel = authViewModel,
                onLoginSuccess = { role, userId -> // Added userId parameter here
                    if (role == "ADMIN") {
                        navController.navigate("admin_route") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        // You might want to pass the userId to the client route if needed
                        navController.navigate("client_route") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ======================
        // REGISTER
        // ======================
        composable("register") {
            RegisterScreen(
                navController = navController,
                viewModel = authViewModel,
                onBack = {
                    navController.popBackStack()
                } // Add this parameter
            )
        }
    }
}