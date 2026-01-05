package com.example.fespace.data.local.dao

import androidx.room.*
import com.example.fespace.data.local.entity.PortfolioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolios ORDER BY createAt DESC")
    fun getAllPortfolios(): Flow<List<PortfolioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolio(portfolio: PortfolioEntity)

    @Update
    suspend fun updatePortfolio(portfolio: PortfolioEntity)

    @Delete
    suspend fun deletePortfolio(portfolio: PortfolioEntity)

    @Query("""
        SELECT * FROM portfolios 
        WHERE LOWER(category) = LOWER(:category)
        ORDER BY createAt DESC
    """)
    fun getPortfoliosByCategory(category: String): Flow<List<PortfolioEntity>>

}
