package com.example.fespace.repository

import kotlinx.coroutines.flow.Flow
import com.example.fespace.data.local.dao.OrderDao
import com.example.fespace.data.local.entity.OrderEntity

class OrderRepository(
    private val orderDao: OrderDao
) {
    fun getOrdersByClient(clientId: Int): Flow<List<OrderEntity>> {
        return orderDao.getOrdersByClient(clientId)
    }

    suspend fun insert(order: OrderEntity) {
        // Ganti 'insertOrder' menjadi 'insert' agar sesuai dengan DAO
        orderDao.insert(order)
    }

    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()

    fun getFilteredOrders(status: String?, clientName: String?): Flow<List<OrderEntity>> {
        return orderDao.getFilteredOrders(status, clientName)
    }

    suspend fun update(order: OrderEntity) {
        orderDao.update(order)
    }

    suspend fun delete(order: OrderEntity) {
        orderDao.delete(order)
    }

    suspend fun getOrderById(id: Int): OrderEntity? {
        return orderDao.getOrderById(id)
    }

    fun getOrderByIdFlow(id: Int): Flow<OrderEntity?> {
        return orderDao.getOrderByIdFlow(id)
    }
} // Pastikan kurung kurawal penutup kelas hanya ada satu di paling bawah
