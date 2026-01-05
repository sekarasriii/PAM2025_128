package com.example.fespace.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fespace.data.local.entity.*
import com.example.fespace.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ClientViewModel(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val serviceRepository: ServiceRepository,
    private val portfolioRepository: PortfolioRepository,
) : ViewModel() {

    // ===================== PORTFOLIO =====================
    private val selectedCategory = MutableStateFlow<String?>(null)

    val portfoliosByCategory: StateFlow<List<PortfolioEntity>> =
        selectedCategory
            .filterNotNull()
            .flatMapLatest { category ->
                portfolioRepository.getPortfolioByCategory(category)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    val allPortfolios: StateFlow<List<PortfolioEntity>> = portfolioRepository.getAllPortfolio()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun selectCategory(category: String) {
        selectedCategory.value = category
    }

    // ===================== SERVICES =====================
    val availableServices: StateFlow<List<ServiceEntity>> =
        serviceRepository.getAllServices()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // ===================== USER =====================
    var currentUser by mutableStateOf<UserEntity?>(null)
        private set

    fun loadUserProfile(userId: Int) {
        viewModelScope.launch {
            currentUser = userRepository.getUserById(userId)
        }
    }

    fun updateUserProfile(user: UserEntity) {
        viewModelScope.launch {
            userRepository.update(user)
            currentUser = user
        }
    }

    // ===================== ORDER =====================
    fun getMyOrders(clientId: Int): Flow<List<OrderEntity>> =
        orderRepository.getOrdersByClient(clientId)

    fun placeOrder(clientId: Int, serviceId: Int, address: String, budget: Double) {
        viewModelScope.launch {
            try {
                val newOrder = OrderEntity(
                    idClient = clientId,      // Harus sama dengan di OrderEntity.kt
                    idServices = serviceId,   // Harus sama dengan di OrderEntity.kt
                    locationAddress = address, // Harus sama dengan di OrderEntity.kt
                    budget = budget,
                    status = "pending"
                )
                orderRepository.insert(newOrder)
            } catch (e: Exception) {
                // Log jika terjadi error agar aplikasi tidak langsung mati
                println("DEBUG_ERROR: Gagal simpan order: ${e.message}")
            }
        }
    }
}
