package com.example.fespace.view.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fespace.viewmodel.AdminViewModel
import OrderEntity

@Composable
fun AdminRoute(
    navController: NavHostController,
    adminViewModel: AdminViewModel
) {
    // PERBAIKAN 2: Sebutkan tipe data secara eksplisit List<OrderEntity> agar 'find' dan 'idOrders' dikenali
    val orders: List<OrderEntity> by adminViewModel.orders.collectAsState(initial = emptyList())

    NavHost(
        navController = navController,
        startDestination = "admin_dashboard"
    ) {
        // RUTE UTAMA ADMIN
        composable("admin_dashboard") {
            AdminDashboardScreen(
                adminViewModel = adminViewModel,
                navController = navController
            )
        }

        // PERBAIKAN 3: HAPUS rute "admin_route" di sini!
        // Anda tidak boleh memanggil AdminRoute di dalam AdminRoute (Recursive Loop).
        // Rute "admin_route" seharusnya hanya ada di AppNavigation.kt.

        // RUTE DETAIL ORDER
        composable(
            route = "admin_order_detail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0

            // Sekarang 'it' otomatis terdeteksi sebagai OrderEntity karena perbaikan 2
            val order = orders.find { it.idOrders == orderId }

            if (order != null) {
                AdminOrderDetailScreen(
                    order = order,
                    adminViewModel = adminViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // RUTE MANAJEMEN LAINNYA
        composable("manage_portfolio") {
            ManagePortfolioScreen()
        }
        composable("manage_service") {
            ManageServiceScreen()
        }
        composable("manage_order") {
            ManageOrderScreen()
        }
    }
}
