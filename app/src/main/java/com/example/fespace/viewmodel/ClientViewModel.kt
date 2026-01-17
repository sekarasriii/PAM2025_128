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
import com.example.fespace.data.local.entity.OrderEntity

class ClientViewModel(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val serviceRepository: ServiceRepository,
    private val portfolioRepository: PortfolioRepository,
    private val orderDocumentRepository: OrderDocumentRepository
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
    
    suspend fun getOrderById(orderId: Int): OrderEntity? {
        // Memanggil repository untuk mengambil data dari database secara asynchronous
        return orderRepository.getOrderById(orderId)
    }
    
    fun getOrderByIdFlow(orderId: Int): Flow<OrderEntity?> {
        return orderRepository.getOrderByIdFlow(orderId)
    }

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

    fun updateClientDocument(order: OrderEntity, documentPath: String, userId: Int) {
        viewModelScope.launch {
            // Update order entity with document path
            val updatedOrder = order.copy(
                clientDocumentPath = documentPath,
                updateAt = System.currentTimeMillis()
            )
            orderRepository.update(updatedOrder)
            
            // Also save to order_documents table as per SRS
            val fileName = documentPath.substringAfterLast("/")
            orderDocumentRepository.uploadDocument(
                orderId = order.idOrders,
                uploadedBy = userId,
                filePath = documentPath,
                fileName = fileName,
                docType = "client_document",
                description = "Dokumen dari client untuk order #${order.idOrders}"
            )
        }
    }
    
    // --- DOCUMENT MANAGEMENT ---
    
    /**
     * Get all documents for an order
     */
    fun getOrderDocuments(orderId: Int): Flow<List<OrderDocumentEntity>> {
        return orderDocumentRepository.getDocumentsByOrder(orderId)
    }
    
    /**
     * Upload a new document (client side)
     */
    fun uploadDocument(
        orderId: Int,
        uploadedBy: Int,
        filePath: String,
        fileName: String,
        docType: String,
        description: String? = null
    ) {
        viewModelScope.launch {
            orderDocumentRepository.uploadDocument(
                orderId = orderId,
                uploadedBy = uploadedBy,
                filePath = filePath,
                fileName = fileName,
                docType = docType,
                description = description
            )
        }
    }
}
