package com.example.fespace.view.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fespace.data.local.database.FeSpaceDatabase
import com.example.fespace.repository.UserRepository
import com.example.fespace.repository.OrderRepository
import com.example.fespace.repository.PortfolioRepository
import com.example.fespace.repository.ServiceRepository
import com.example.fespace.viewmodel.AuthViewModel
import com.example.fespace.viewmodel.ViewModelFactory

@Composable
fun AuthRoute(navController: NavHostController) {
    val context = LocalContext.current

    // 1️⃣ Inisialisasi Database & Repository (PENTING!)
    val database = FeSpaceDatabase.getInstance(context)
    val userRepository = UserRepository(database.userDao())

    // Sesuaikan repo lain jika dibutuhkan oleh factory Anda
    val orderRepository = OrderRepository(database.orderDao())
    val portfolioRepository = PortfolioRepository(database.portfolioDao())
    val serviceRepository = ServiceRepository(database.serviceDao())
    val orderDocumentRepository = com.example.fespace.repository.OrderDocumentRepository(database.orderDocumentDao())

    // 2️⃣ Gunakan ViewModelFactory agar Repository Terinjeksi
    val factory = ViewModelFactory(
        userRepo = userRepository,
        orderRepo = orderRepository,
        portfolioRepo = portfolioRepository,
        serviceRepo = serviceRepository,
        orderDocumentRepo = orderDocumentRepository
    )

    val authViewModel: AuthViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // LOGIN
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel,
                onLoginSuccess = { role, userId ->
                    // PERBAIKAN: Gunakan rute yang sama dengan AppNavigation
                    if (role.equals("admin", ignoreCase = true)) {
                        navController.navigate("admin_route") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("client_home") { // Ganti ke client_home
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

        // REGISTER
        composable("register") {
            RegisterScreen(
                navController = navController,
                viewModel = authViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
