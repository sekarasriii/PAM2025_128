package com.example.fespace.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.fespace.data.local.entity.UserEntity
import com.example.fespace.data.local.entity.ServiceEntity

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["idUser"], childColumns = ["idClient"]),
        ForeignKey(entity = ServiceEntity::class, parentColumns = ["idServices"], childColumns = ["idServices"])
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val idOrders: Int = 0,
    val idClient: Int,
    val idServices: Int,
    val locationAddress: String,
    val budget: Double,
    val status: String = "pending",
    // Gunakan ColumnInfo agar konsisten dengan query DAO
    @androidx.room.ColumnInfo(name = "createAt")
    val createAt: Long = System.currentTimeMillis(),
    @androidx.room.ColumnInfo(name = "updateAt")
    val updateAt: Long = System.currentTimeMillis()
)
