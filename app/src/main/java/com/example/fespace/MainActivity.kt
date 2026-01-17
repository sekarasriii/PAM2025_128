package com.example.fespace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fespace.ui.theme.FeSpaceTheme
import com.example.fespace.view.navigation.AppNavigation
import com.example.fespace.view.navigation.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Seed database with admin account on first run
        val database = com.example.fespace.data.local.database.FeSpaceDatabase.getInstance(this)
        val userRepository = com.example.fespace.repository.UserRepository(database.userDao())
        com.example.fespace.data.local.database.DatabaseSeeder.seedDatabase(this, userRepository)
        
        setContent {
            FeSpaceTheme {
                // PASTIKAN memanggil AppNavigation dengan rute yang ada di Screen.kt
                AppNavigation(startDestination = Screen.Welcome.route)
            }
        }
    }
}