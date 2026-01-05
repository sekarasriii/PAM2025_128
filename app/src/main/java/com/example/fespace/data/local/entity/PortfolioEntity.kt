package com.example.fespace.data.local.entity

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "portfolios")
data class PortfolioEntity(
    @PrimaryKey(autoGenerate = true)
    val idPortfolios: Int = 0,
    val idAdmin: Int,
    val title: String,
    val description: String,
    val imagePath: String?,
    val category: String, // 'residential', 'commercial', 'renovation', 'interior'
    val year: Int,
    val createAt: Long = System.currentTimeMillis(),
    val updateAt: Long = System.currentTimeMillis()
)

