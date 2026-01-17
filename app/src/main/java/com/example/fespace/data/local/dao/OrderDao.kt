package com.example.fespace.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.fespace.data.local.entity.OrderEntity


@Dao
interface OrderDao {

    // ========================
    // CREATE
    // ========================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity)

    // ========================
    // UPDATE
    // ========================
    @Update
    suspend fun update(order: OrderEntity)

    // ========================
    // DELETE
    // ========================
    @Delete
    suspend fun delete(order: OrderEntity)

    // ========================
    // READ (FLOW → UI)
    // ========================
    @Query("SELECT * FROM orders WHERE idClient = :clientId ORDER BY idOrders DESC")
    fun getOrdersByClient(clientId: Int): Flow<List<OrderEntity>>

    //@Query("SELECT * FROM orders WHERE idAdmin = :adminId")
    //fun getOrdersByAdmin(adminId: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY createAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    // ========================
    // READ (ONE DATA)
    // ========================
    @Query("SELECT * FROM orders WHERE idOrders = :id")
    suspend fun getOrderById(id: Int): OrderEntity?

    @Query("SELECT * FROM orders WHERE idOrders = :id")
    fun getOrderByIdFlow(id: Int): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE status = :status")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("""
    SELECT orders.* FROM orders 
    INNER JOIN users ON orders.idClient = users.idUser 
    WHERE (:status IS NULL OR orders.status = :status)    AND (:clientName IS NULL OR users.nameUser LIKE '%' || :clientName || '%')
    ORDER BY orders.createAt DESC
""")
    fun getFilteredOrders(status: String?, clientName: String?): Flow<List<OrderEntity>>


}