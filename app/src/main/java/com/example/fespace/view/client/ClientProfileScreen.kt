package com.example.fespace.view.client

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fespace.viewmodel.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(
    clientId: Int,
    clientViewModel: ClientViewModel,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // 1. Memuat profil user saat layar dibuka
    LaunchedEffect(clientId) {
        clientViewModel.loadUserProfile(clientId)
    }

    val user = clientViewModel.currentUser

    // 2. State lokal untuk form edit
    var name by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) } // Tambahkan variabel ini

    // 3. Sinkronisasi data dari database ke UI
    LaunchedEffect(user) {
        user?.let {
            name = it.nameUser
            whatsapp = it.whatsappNumber ?: ""
            email = it.email
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil & Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (isEditing) {
                            // Update data ke database saat klik simpan
                            user?.let {
                                val updatedUser = it.copy(
                                    nameUser = name,
                                    whatsappNumber = whatsapp,
                                    updateAt = System.currentTimeMillis()
                                )
                                clientViewModel.updateUserProfile(updatedUser)
                                Toast.makeText(context, "Profil Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                            }
                        }
                        isEditing = !isEditing
                    }) {
                        Text(if (isEditing) "Simpan" else "Edit", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()) // Tambahkan scroll agar tidak terpotong
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto Profil Placeholder
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Field Nama Lengkap
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                leadingIcon = { Icon(Icons.Default.Badge, null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Field WhatsApp
            OutlinedTextField(
                value = whatsapp,
                onValueChange = { if (it.all { char -> char.isDigit() }) whatsapp = it },
                label = { Text("Nomor WhatsApp") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                leadingIcon = { Icon(Icons.Default.Phone, null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Field Email (Read Only)
            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Email Akun") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                leadingIcon = { Icon(Icons.Default.Email, null) }
            )

            Spacer(modifier = Modifier.weight(1f)) // Menggunakan weight dengan benar

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}
