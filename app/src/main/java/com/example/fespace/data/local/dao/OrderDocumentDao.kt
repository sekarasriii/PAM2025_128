package com.example.fespace.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fespace.data.local.entity.OrderDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDocumentDao {

    // ========================
    // CREATE
    // ========================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: OrderDocumentEntity)

    // ========================
    // DELETE
    // ========================
    @Delete
    suspend fun delete(document: OrderDocumentEntity)

    // ========================
    // READ
    // ========================
    @Query("""
        SELECT * FROM order_documents 
        WHERE id_orders = :orderId
        ORDER BY uploaded_at DESC
    """)
    fun getDocumentsByOrder(orderId: Int): Flow<List<OrderDocumentEntity>>
    
    @Query("""
        SELECT * FROM order_documents 
        WHERE id_orders = :orderId AND docType = :docType
        ORDER BY uploaded_at DESC
    """)
    fun getDocumentsByOrderAndType(orderId: Int, docType: String): Flow<List<OrderDocumentEntity>>
}