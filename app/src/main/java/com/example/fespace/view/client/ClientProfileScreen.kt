package com.example.fespace.view.client

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.fespace.ui.theme.*
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

    // Load user profile
    LaunchedEffect(clientId) {
        clientViewModel.loadUserProfile(clientId)
    }

    val user = clientViewModel.currentUser

    // Local state for form
    var name by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    // Local state for logout confirmation
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Sync data from database to UI
    LaunchedEffect(user) {
        user?.let {
            name = it.nameUser
            whatsapp = it.whatsappNumber ?: ""
            email = it.email
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar dari aplikasi?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Logout", color = Cream)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = Terracotta)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Profil & Pengaturan",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (isEditing) {
                            // Update to database
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
                        Text(
                            if (isEditing) "Simpan" else "Edit",
                            fontWeight = FontWeight.Bold,
                            color = Terracotta
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCharcoal,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = Terracotta
                )
            )
        },
        containerColor = DarkCharcoal
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.Large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
        ) {
            // Profile Photo Placeholder
            Surface(
                shape = CircleShape,
                color = DarkSurface,
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Terracotta
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.Small))

            // Profile Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Radius.Medium),
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = Elevation.Card
                )
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.Medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
                ) {
                    Text(
                        "Informasi Profil",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    // Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Badge,
                                null,
                                tint = if (isEditing) Terracotta else TextTertiary
                            ) 
                        },
                        shape = RoundedCornerShape(Radius.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = Gray700,
                            cursorColor = Terracotta,
                            focusedLabelColor = Terracotta,
                            unfocusedLabelColor = TextSecondary
                        )
                    )

                    // WhatsApp Field
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { newValue ->
                            // Only allow digits and + symbol
                            val cleaned = newValue.filter { it.isDigit() || it == '+' }
                            // Ensure it starts with +62 if user is typing
                            if (isEditing) {
                                whatsapp = when {
                                    cleaned.isEmpty() -> "+62"
                                    cleaned.startsWith("+62") -> cleaned
                                    cleaned.startsWith("62") -> "+$cleaned"
                                    cleaned.startsWith("0") -> "+62${cleaned.substring(1)}"
                                    cleaned.startsWith("8") -> "+62$cleaned"
                                    else -> "+62$cleaned"
                                }
                            }
                        },
                        label = { Text("Nomor WhatsApp") },
                        placeholder = { Text("+62 8XXX-XXXX-XXXX", color = TextTertiary.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Phone,
                                null,
                                tint = if (isEditing) Terracotta else TextTertiary
                            ) 
                        },
                        visualTransformation = com.example.fespace.utils.WhatsAppVisualTransformation(),
                        shape = RoundedCornerShape(Radius.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = Gray700,
                            cursorColor = Terracotta,
                            focusedLabelColor = Terracotta,
                            unfocusedLabelColor = TextSecondary
                        )
                    )

                    // Email Field (Read Only)
                    OutlinedTextField(
                        value = email,
                        onValueChange = {},
                        label = { Text("Email Akun") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Email,
                                null,
                                tint = TextTertiary
                            ) 
                        },
                        shape = RoundedCornerShape(Radius.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = TextSecondary,
                            disabledBorderColor = Gray700,
                            disabledLabelColor = TextDisabled,
                            disabledLeadingIconColor = TextDisabled
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentRed,
                    contentColor = Cream
                ),
                shape = RoundedCornerShape(Radius.Medium)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(Modifier.width(Spacing.Small))
                Text(
                    "Logout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
