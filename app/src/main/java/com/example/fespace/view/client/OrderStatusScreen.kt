package com.example.fespace.view.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fespace.viewmodel.ClientViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.example.fespace.data.local.entity.OrderEntity // Ganti sesuai package Entity Anda
import androidx.compose.foundation.lazy.items // Pastikan ini terimport


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStatusScreen(
    clientViewModel: ClientViewModel,
    clientId: Int,
    onDetailClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val myOrders by clientViewModel.getMyOrders(clientId).collectAsState(emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pesanan Saya") },
                // TAMBAHKAN ICON BACK DI SINI
                navigationIcon = {
                    IconButton(onClick = onBackClick) { // GANTI onBack MENJADI onBackClick
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (myOrders.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Belum ada pesanan.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(myOrders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDetailClick(order.idOrders) } // FIX: Use snake_case
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "Proyek: ${order.locationAddress}", // FIX: Use snake_case
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Status: ${order.status.uppercase()}",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
