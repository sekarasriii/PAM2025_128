package com.example.fespace.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fespace.data.local.database.FeSpaceDatabase
import com.example.fespace.repository.OrderRepository
import com.example.fespace.repository.PortfolioRepository
import com.example.fespace.repository.ServiceRepository
import com.example.fespace.repository.UserRepository
import com.example.fespace.view.admin.AdminDashboardScreen
import com.example.fespace.view.auth.LoginScreen
import com.example.fespace.view.auth.RegisterScreen
import com.example.fespace.view.client.ClientHomeScreen
import com.example.fespace.view.client.OrderDetailScreen
import com.example.fespace.view.client.OrderServiceScreen
import com.example.fespace.view.client.OrderStatusScreen
import com.example.fespace.view.common.WelcomeScreen
import com.example.fespace.viewmodel.AdminViewModel
import com.example.fespace.viewmodel.AuthViewModel
import com.example.fespace.viewmodel.ClientViewModel
import com.example.fespace.viewmodel.ViewModelFactory
import com.example.fespace.view.client.ClientProfileScreen
import com.example.fespace.view.client.ClientRoute

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // 1. Initialize Database
    val database = FeSpaceDatabase.getInstance(context)

    // 2. Initialize Daos
    val userDao = database.userDao()
    val orderDao = database.orderDao()
    val portfolioDao = database.portfolioDao()
    val serviceDao = database.serviceDao()

    // 3. Initialize Repositories
    val userRepository = UserRepository(userDao)
    val orderRepository = OrderRepository(orderDao)
    val portfolioRepository = PortfolioRepository(portfolioDao)
    val serviceRepository = ServiceRepository(serviceDao)

    // 4. Initialize ViewModel Factory
    val viewModelFactory = ViewModelFactory(
        userRepo = userRepository,
        portfolioRepo = portfolioRepository,
        serviceRepo = serviceRepository,
        orderRepo = orderRepository
    )

    // 5. Initialize ViewModels
    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
    val adminViewModel: AdminViewModel = viewModel(factory = viewModelFactory)
    val clientViewModel: ClientViewModel = viewModel(factory = viewModelFactory)

    var currentUserId by remember { mutableIntStateOf(-1) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // WELCOME SCREEN
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        // LOGIN SCREEN
        // LOGIN SCREEN
        composable(Screen.Login.route) {
            LoginScreen(
                // TAMBAHKAN DUA PARAMETER INI:
                navController = navController,
                viewModel = authViewModel,

                onLoginSuccess = { role, userId ->
                    // SIMPAN ID USER YANG BERHASIL LOGIN DISINI
                    currentUserId = userId

                    if (role.equals("admin", ignoreCase = true)) {
                        navController.navigate("admin_route") {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate("client_home") {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onBack = { navController.popBackStack() }
            )
        }

        // REGISTER SCREEN
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                viewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("client_route") {
            ClientRoute(
                navController = navController,
                clientId = currentUserId, // Pastikan ID ini dikirim dari session/state login
                clientViewModel = clientViewModel
            )
        }

        // --- SECTION CLIENT ---

        // CLIENT HOME
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

        // CLIENT PROFILE
        composable("client_profile") {
            ClientProfileScreen(
                clientId = currentUserId, // SEKARANG MENGIRIM ID YANG DINAMIS
                clientViewModel = clientViewModel,
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "order_service/{serviceId}",
            arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: 0
            OrderServiceScreen(
                clientViewModel = clientViewModel,
                clientId = currentUserId,
                serviceId = serviceId,
                onOrderSuccess = {
                    // PERBAIKAN NAVIGASI:
                    navController.navigate("order_status") {
                        // Hapus form order dari tumpukan (backstack)
                        // agar saat di-back dari halaman 'Pesanan Saya' langsung ke Home.
                        popUpTo("client_home") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable("order_status") {
            OrderStatusScreen(
                clientViewModel = clientViewModel,
                clientId = currentUserId,
                onDetailClick = { orderId ->
                    navController.navigate("order_detail/$orderId")
                },
                onBackClick = { navController.popBackStack() } // Pastikan ini ada
            )
        }

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

        // --- SECTION ADMIN ---

        composable("admin_route") {
            AdminDashboardScreen(
                adminViewModel = adminViewModel,
                navController = navController
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                adminViewModel = adminViewModel,
                navController = navController
            )
        }
    }
}
