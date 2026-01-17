package com.example.fespace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val idServices: Int = 0,
    val idAdmin: Int,
    val nameServices: String,
    val category: String,
    val description: String,
    val priceStart: Double,
    val durationEstimate: String,
    val features: String?,
    val imagePath: String? = null, // Legacy: single image path (kept for backward compatibility)
    val imagePaths: String? = null, // New: multiple image paths (comma-separated)
    val createAt: Long = System.currentTimeMillis(),
    val updateAt: Long = System.currentTimeMillis() // Sesuaikan dengan 'updateAt' Anda
)