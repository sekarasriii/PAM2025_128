package com.example.fespace.view.client

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.fespace.viewmodel.ClientViewModel

@Composable
fun ClientRoute(
    navController: NavHostController, // Ini adalah NavController utama dari AppNavigation
    clientId: Int,
    clientViewModel: ClientViewModel
) {
    // Gunakan rute flat (langsung) agar navigasi antar screen client lebih stabil
    NavHost(
        navController = navController,
        startDestination = "client_home"
    ) {
        // 1. HOME SCREEN
        composable("client_home") {
            ClientHomeScreen(
                clientViewModel = clientViewModel,
                onOrderClick = { serviceId ->
                    navController.navigate("order_service/$serviceId")
                },
                onViewOrdersClick = {
                    navController.navigate("order_status")
                },
                onProfileClick = {
                    navController.navigate("client_profile")
                }
            )
        }

        // 2. ORDER FORM
        composable(
            route = "order_service/{serviceId}",
            arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: 0
            OrderServiceScreen(
                // PERBAIKAN: Hapus baris 'navController = navController,' karena parameternya sudah tidak ada
                clientViewModel = clientViewModel,
                clientId = clientId,
                serviceId = serviceId,
                onOrderSuccess = {
                    navController.navigate("order_status") {
                        popUpTo("client_home") { saveState = true }
                    }
                }
            )
        }

        // 3. ORDER STATUS (Pesanan Saya)
        composable("order_status") {
            OrderStatusScreen(
                clientViewModel = clientViewModel,
                clientId = clientId,
                onDetailClick = { orderId ->
                    navController.navigate("order_detail/$orderId")
                },
                // ADD THIS LINE:
                onBackClick = {
                    navController.navigate("client_home") {
                        popUpTo("client_home") { inclusive = true }
                    }
                }
            )
        }


        // 4. ORDER DETAIL
        composable(
            route = "order_detail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            OrderDetailScreen(
                orderId = orderId,
                clientViewModel = clientViewModel
            )
        }


        // 5. PROFILE SCREEN - PASTIKAN INI ADA
        // Contoh pemanggilan di ClientRoute
        composable("client_profile") {
            ClientProfileScreen(
                clientId = clientId,
                clientViewModel = clientViewModel,
                onLogout = { /* alur logout */ },
                onBack = { navController.popBackStack() })
        }
    }
}
