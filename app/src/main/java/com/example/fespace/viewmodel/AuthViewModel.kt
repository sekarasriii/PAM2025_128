package com.example.fespace.viewmodel

import android.util.Log
// HAPUS BARIS INI: import androidx.compose.ui.semantics.role
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fespace.data.local.entity.UserEntity
import com.example.fespace.repository.UserRepository
import com.example.fespace.repository.OrderRepository
import com.example.fespace.repository.PortfolioRepository
import com.example.fespace.repository.ServiceRepository
import com.example.fespace.viewmodel.AdminViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isLoginValid(email: String, pass: String): Boolean {
        return email.isNotEmpty() && pass.isNotEmpty() && isValidEmail(email)
    }

    fun isRegisterValid(name: String, email: String, pass: String, whatsapp: String): Boolean {
        return name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() &&
                whatsapp.isNotEmpty() && isValidEmail(email)
    }

    fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        whatsappNumber: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.insertUser(
                    UserEntity(
                        nameUser = name,
                        email = email,
                        password = password,
                        role = role,
                        whatsappNumber = whatsappNumber
                    )
                )
                onSuccess()
            } catch (e: Exception) {
                Log.e("RegisterError", e.message ?: "Unknown error")
            }
        }
    }

    fun login(email: String, pass: String, onResult: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUser(email, pass) // This returns a UserEntity?
            withContext(Dispatchers.Main) {
                if (user != null) {
                    // Pass the 'role' property (a String) instead of the whole 'user' object
                    onResult(user.role)
                } else {
                    onResult(null)
                }
            }
        }
    }
    // Factory (Tetap di bawah)
    class ViewModelFactory(
        private val userRepo: UserRepository,private val portfolioRepo: PortfolioRepository,
        private val serviceRepo: ServiceRepository,
        private val orderRepo: OrderRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return when {
                modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                    AuthViewModel(userRepo) as T

                modelClass.isAssignableFrom(AdminViewModel::class.java) ->
                    AdminViewModel(
                        portfolioRepository = portfolioRepo,
                        serviceRepository = serviceRepo,
                        orderRepository = orderRepo,
                        userRepository = userRepo
                    ) as T

                modelClass.isAssignableFrom(ClientViewModel::class.java) ->
                    ClientViewModel(
                        orderRepository = orderRepo,
                        userRepository = userRepo,      // Tambahkan ini jika dibutuhkan
                        serviceRepository = serviceRepo,
                        portfolioRepository = portfolioRepo
                    ) as T

                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }

    }
}
