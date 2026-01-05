package com.example.fespace.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fespace.data.local.dao.ServiceDao
import com.example.fespace.data.local.dao.UserDao
import com.example.fespace.data.local.entity.UserEntity
import com.example.fespace.data.local.dao.PortfolioDao
import com.example.fespace.data.local.dao.OrderDao
import com.example.fespace.data.local.entity.ServiceEntity
import com.example.fespace.data.local.entity.PortfolioEntity

@Database(entities =
    [
        UserEntity::class,
        PortfolioEntity::class,
        ServiceEntity::class,
        OrderEntity::class],
    version = 1, exportSchema = false)
abstract class FeSpaceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun serviceDao(): ServiceDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: FeSpaceDatabase? = null

        fun getInstance(context: Context): FeSpaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FeSpaceDatabase::class.java,
                    "fespace_database"
                )
                    .fallbackToDestructiveMigration() // <--- INI WAJIB ADA
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
