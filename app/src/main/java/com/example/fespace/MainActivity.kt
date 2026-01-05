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
        setContent {
            FeSpaceTheme {
                // PASTIKAN memanggil AppNavigation dengan rute yang ada di Screen.kt
                AppNavigation(startDestination = Screen.Welcome.route)
            }
        }
    }
}