package com.example.fespace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fespace.repository.OrderRepository
import com.example.fespace.repository.PortfolioRepository
import com.example.fespace.repository.ServiceRepository
import com.example.fespace.repository.UserRepository
import com.example.fespace.repository.OrderDocumentRepository

class ViewModelFactory(
    private val userRepo: UserRepository,
    private val portfolioRepo: PortfolioRepository,
    private val serviceRepo: ServiceRepository,
    private val orderRepo: OrderRepository,
    private val orderDocumentRepo: OrderDocumentRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") // Menghilangkan warning unchecked cast
        return when {
            // 1. Kasus untuk AuthViewModel
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepo) as T
            }
            // 2. Kasus untuk AdminViewModel
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(
                    portfolioRepository = portfolioRepo,
                    serviceRepository = serviceRepo,
                    orderRepository = orderRepo,
                    userRepository = userRepo,
                    orderDocumentRepository = orderDocumentRepo
                ) as T
            }
            // 3. PERBAIKAN: Sesuaikan dengan parameter ClientViewModel
            modelClass.isAssignableFrom(ClientViewModel::class.java) -> {
                ClientViewModel(
                    orderRepository = orderRepo,
                    userRepository = userRepo,      // Tambahkan ini agar tidak type mismatch
                    serviceRepository = serviceRepo,
                    portfolioRepository = portfolioRepo,
                    orderDocumentRepository = orderDocumentRepo
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
