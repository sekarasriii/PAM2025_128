package com.example.fespace.view.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import OrderEntity
import com.example.fespace.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class) // Perbaikan warning Experimental API
@Composable
fun AdminOrderDetailScreen(
    order: OrderEntity,
    adminViewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Info Client & Order
            Text("Info Pesanan", style = MaterialTheme.typography.titleLarge)
            Text("Lokasi: ${order.locationAddress}")
            Text("Budget: Rp ${order.budget}")
            Text("Status Saat Ini: ${order.status.uppercase()}", color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(16.dp))

            // Tombol Aksi Status
            Text("Update Status", fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    // PERBAIKAN: Mengirim objek order sesuai ekspektasi ViewModel
                    onClick = { adminViewModel.updateOrderStatus(order, "approved") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Hijau Sukses
                    modifier = Modifier.weight(1f)
                ) { Text("Approve") }

                Button(
                    // PERBAIKAN: Mengirim objek order sesuai ekspektasi ViewModel
                    onClick = { adminViewModel.updateOrderStatus(order, "rejected") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) { Text("Reject") }
            }

            Spacer(Modifier.height(16.dp))

            // Fitur WhatsApp
            Button(
                onClick = {
                    // Logika membuka WhatsApp (Asumsi id_client digunakan untuk mencari nomor WA di UserRepo)
                    // Untuk saat ini hanya placeholder intent
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://wa.me/") // Tambahkan nomor WA jika tersedia
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Phone, null)
                Spacer(Modifier.width(8.dp))
                Text("Hubungi Client via WA")
            }

            Spacer(Modifier.height(16.dp))
            Text("Dokumen & Desain", fontWeight = FontWeight.Bold)

            Button(
                onClick = { /* Implementasi File Picker untuk Upload Desain */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Upload, null)
                Spacer(Modifier.width(8.dp))
                Text("Upload Hasil Desain")
            }
        }
    }
}
