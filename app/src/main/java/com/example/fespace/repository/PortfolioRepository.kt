package com.example.fespace.repository

import com.example.fespace.data.local.dao.PortfolioDao
import com.example.fespace.data.local.entity.PortfolioEntity
import kotlinx.coroutines.flow.Flow

class PortfolioRepository(
    private val portfolioDao: PortfolioDao
) {

    fun getAllPortfolio(): Flow<List<PortfolioEntity>> =
        portfolioDao.getAllPortfolios()

    fun getPortfolioByCategory(category: String): Flow<List<PortfolioEntity>> =
        portfolioDao.getPortfoliosByCategory(category)

    suspend fun insert(portfolio: PortfolioEntity) =
        portfolioDao.insertPortfolio(portfolio)

    suspend fun update(portfolio: PortfolioEntity) =
        portfolioDao.updatePortfolio(portfolio)

    suspend fun delete(portfolio: PortfolioEntity) =
        portfolioDao.deletePortfolio(portfolio)
}
