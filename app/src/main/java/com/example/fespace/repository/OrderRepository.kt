package com.example.fespace.repository

import com.example.fespace.data.local.dao.OrderDao
import kotlinx.coroutines.flow.Flow
import OrderEntity

class OrderRepository(
    private val orderDao: OrderDao
) {

    fun getOrdersByClient(clientId: Int): Flow<List<OrderEntity>> {
        return orderDao.getOrdersByClient(clientId)
    }

    //fun getOrdersByAdmin(adminId: Int): Flow<List<OrderEntity>> {
        //return orderDao.getOrdersByAdmin(adminId)
    //}

    fun getAllOrders(): Flow<List<OrderEntity>> {
        return orderDao.getAllOrders()
    }

    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>> {
        return orderDao.getOrdersByStatus(status)
    }

    suspend fun getOrderById(id: Int): OrderEntity? { // Lengkapi menjadi getOrderById
        return orderDao.getOrderById(id)
    }

    suspend fun insert(order: OrderEntity) {
        orderDao.insert(order)
    }

    suspend fun update(order: OrderEntity) {
        orderDao.update(order)
    }

    suspend fun delete(order: OrderEntity) {
        orderDao.delete(order)
    }
}
