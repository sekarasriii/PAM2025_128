package com.example.fespace.view.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fespace.data.local.entity.UserEntity
import com.example.fespace.ui.theme.*
import com.example.fespace.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminClientsScreen(
    adminViewModel: AdminViewModel,
    onBack: () -> Unit,
    onClientClick: (Int) -> Unit = {}
) {
    val clients by adminViewModel.clients.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredClients = remember(clients, searchQuery) {
        if (searchQuery.isBlank()) clients
        else clients.filter { 
            it.nameUser.contains(searchQuery, ignoreCase = true) || 
            it.email.contains(searchQuery, ignoreCase = true) 
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Daftar Klien", 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCharcoal,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = DarkCharcoal
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.Large)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.Medium),
                placeholder = { Text("Cari nama atau email...", color = TextSecondary.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentGold) },
                shape = RoundedCornerShape(Radius.Medium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = Terracotta,
                    unfocusedBorderColor = AccentGold.copy(alpha = 0.3f),
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                singleLine = true
            )
            
            if (filteredClients.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Person, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp), 
                            tint = TextTertiary.copy(alpha = 0.3f)
                        )
                        Spacer(Modifier.height(Spacing.Medium))
                        Text(
                            if (searchQuery.isBlank()) "Belum ada klien terdaftar" else "Klien tidak ditemukan",
                            color = TextTertiary
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.Medium),
                    contentPadding = PaddingValues(bottom = Spacing.ExtraLarge)
                ) {
                    items(filteredClients) { client ->
                        ClientCard(client = client)
                    }
                }
            }
        }
    }
}

@Composable
fun ClientCard(
    client: UserEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.Large),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {
            // Avatar Placeholder
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = TerracottaAlpha
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        client.nameUser.take(1).uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Terracotta
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    client.nameUser,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Spacer(Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Email, 
                        contentDescription = null, 
                        tint = AccentGold, 
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        client.email,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                
                if (!client.whatsappNumber.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Phone, 
                            contentDescription = null, 
                            tint = SageGreen, 
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            client.whatsappNumber,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
