package com.example.fespace.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.fespace.repository.OrderDocumentRepository
import com.example.fespace.utils.SessionManager
import com.example.fespace.view.admin.AdminDashboardScreen
import com.example.fespace.view.auth.LoginScreen
import com.example.fespace.view.auth.RegisterScreen
import com.example.fespace.view.client.ClientHomeScreen
import com.example.fespace.view.client.ClientPortfolioScreen
import com.example.fespace.view.client.OrderDetailScreen
import com.example.fespace.view.client.OrderServiceScreen
import com.example.fespace.view.client.OrderStatusScreen
import com.example.fespace.view.common.WelcomeScreen
import com.example.fespace.viewmodel.AdminViewModel
import com.example.fespace.viewmodel.AuthViewModel
import com.example.fespace.viewmodel.ClientViewModel
import com.example.fespace.viewmodel.ViewModelFactory
import com.example.fespace.view.client.ClientProfileScreen
import com.example.fespace.view.admin.AdminOrderDetailScreen

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Initialize SessionManager
    val sessionManager = remember { SessionManager(context) }

    // Coroutine scope for Composable
    val scope = rememberCoroutineScope()

    // 1. Initialize Database
    val database = FeSpaceDatabase.getInstance(context)

    // 2. Initialize Daos
    val userDao = database.userDao()
    val orderDao = database.orderDao()
    val portfolioDao = database.portfolioDao()
    val serviceDao = database.serviceDao()
    val orderDocumentDao = database.orderDocumentDao()

    // 3. Initialize Repositories
    val userRepository = UserRepository(userDao)
    val orderRepository = OrderRepository(orderDao)
    val portfolioRepository = PortfolioRepository(portfolioDao)
    val serviceRepository = ServiceRepository(serviceDao)
    val orderDocumentRepository = OrderDocumentRepository(orderDocumentDao)

    // 4. Initialize ViewModel Factory
    val viewModelFactory = ViewModelFactory(
        userRepo = userRepository,
        portfolioRepo = portfolioRepository,
        serviceRepo = serviceRepository,
        orderRepo = orderRepository,
        orderDocumentRepo = orderDocumentRepository
    )

    // 5. Initialize ViewModels
    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
    val adminViewModel: AdminViewModel = viewModel(factory = viewModelFactory)
    val clientViewModel: ClientViewModel = viewModel(factory = viewModelFactory)

    // 6. Session-based user ID
    var currentUserId by remember { mutableIntStateOf(sessionManager.getUserId()) }

    // 7. Determine start destination based on session
    val actualStartDestination = remember {
        if (sessionManager.isLoggedIn()) {
            val role = sessionManager.getUserRole()
            if (role.equals("admin", ignoreCase = true)) {
                "admin_route"
            } else {
                "client_home"
            }
        } else {
            startDestination
        }
    }

    NavHost(
        navController = navController,
        startDestination = actualStartDestination
    ) {
        // WELCOME SCREEN
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        // LOGIN SCREEN
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,viewModel = authViewModel,
                onLoginSuccess = { role, userId ->
                    currentUserId = userId // Update ID User yang sedang login
                    if (role.equals("admin", ignoreCase = true)) {
                        navController.navigate("admin_route") { popUpTo(0) { inclusive = true } }
                    } else {
                        navController.navigate("client_home") { popUpTo(0) { inclusive = true } }
                    }
                },
                onRegisterClick = { navController.navigate("register") },
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
                },
                onSeeAllPortfolioClick = {
                    navController.navigate("client_portfolio")
                }
            )
        }

        // CLIENT PORTFOLIO
        composable("client_portfolio") {
            ClientPortfolioScreen(
                clientViewModel = clientViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // CLIENT PROFILE
        composable("client_profile") {
            ClientProfileScreen(
                clientId = currentUserId,
                clientViewModel = clientViewModel,
                onLogout = {
                    sessionManager.clearSession()
                    currentUserId = -1
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
                    navController.navigate("order_status") {
                        popUpTo("client_home") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
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
                clientViewModel = clientViewModel,
                onBack = { navController.popBackStack() } // Menambahkan parameter onBack
            )
        }


        // --- SECTION ADMIN ---

        composable("admin_route") {
            AdminDashboardScreen(
                adminViewModel = adminViewModel,
                navController = navController
            )
        }

        composable(
            route = "admin_dashboard?menu={menu}",
            arguments = listOf(
                navArgument("menu") {
                    type = NavType.StringType
                    defaultValue = "Dashboard"
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val initialMenu = backStackEntry.arguments?.getString("menu") ?: "Dashboard"
            AdminDashboardScreen(
                adminViewModel = adminViewModel,
                navController = navController,
                initialSelectedMenu = initialMenu
            )
        }

        // ✅ TAMBAHKAN INI (SETELAH COMPOSABLE ADMIN):
        composable(
            route = Screen.AdminOrderDetail.routeWithArgs,
            arguments = listOf(
                navArgument("orderId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            AdminOrderDetailScreen(
                orderId = orderId,
                adminViewModel = adminViewModel,
                onBack = {
                    // Navigate back to admin dashboard with Orders tab selected
                    navController.navigate("admin_dashboard?menu=Orders") {
                        popUpTo("admin_dashboard") { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminClients.route) {
            com.example.fespace.view.admin.AdminClientsScreen(
                adminViewModel = adminViewModel,
                onBack = { navController.popBackStack() },
                onClientClick = { clientId ->
                    // Optional: navigate to client details or filter orders by client
                    navController.navigate("admin_dashboard?menu=Orders")
                }
            )
        }
    }
}
