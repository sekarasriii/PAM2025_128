package com.example.fespace.view.client

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fespace.data.local.entity.PortfolioEntity
import com.example.fespace.viewmodel.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderServiceScreen(
    // navController dihapus karena tidak digunakan
    clientViewModel: ClientViewModel,
    clientId: Int,
    serviceId: Int,
    onOrderSuccess: () -> Unit
) {
    // Data dari ViewModel
    val services by clientViewModel.availableServices.collectAsStateWithLifecycle()
    val portfolios by clientViewModel.allPortfolios.collectAsStateWithLifecycle()

    // State Input
    var address by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedServiceId by remember { mutableStateOf<Int?>(serviceId) }
    var locationPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher untuk mengambil gambar dari galeri (Sesuai Flowchart: Upload Foto Lokasi)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        locationPhotoUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pesan Jasa Desain", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // SECTION 1: PORTFOLIO (INSPIRASI DESAIN)
            // ==========================================
            Text(
                "Inspirasi Desain Sebelumnya",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (portfolios.isEmpty()) {
                Text(
                    "Belum ada portfolio tersedia",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(portfolios) { item ->
                        PortfolioItemCard(item)
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))

            // ==========================================
            // SECTION 2: FORMULIR PEMESANAN
            // ==========================================
            Text(
                "Formulir Pemesanan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // 1. Dropdown Pilih Jasa
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = services.find { it.idServices == selectedServiceId }?.nameServices ?: "Pilih Layanan",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Layanan Terpilih") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    services.forEach { service ->
                        DropdownMenuItem(
                            text = { Text(service.nameServices) },
                            onClick = {
                                selectedServiceId = service.idServices
                                expanded = false
                            }
                        )
                    }
                }
            }

            // 2. FITUR UPLOAD FOTO LOKASI (Sesuai Flowchart)
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CloudUpload, null)
                Spacer(Modifier.width(8.dp))
                Text(if (locationPhotoUri == null) "Upload Foto Lokasi" else "Foto Terpilih ✅")
            }

            // 3. Input Alamat
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat Lokasi Proyek") },
                placeholder = { Text("Contoh: Jl. Merdeka No. 10, Jakarta") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // 4. Input Budget
            OutlinedTextField(
                value = budget,
                onValueChange = { if (it.all { c -> c.isDigit() }) budget = it },
                label = { Text("Rencana Anggaran (Budget)") },
                prefix = { Text("Rp ") },
                placeholder = { Text("Contoh: 50000000") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Tombol Kirim (Simpan ke DB sesuai Flowchart)
            Button(
                onClick = {
                    if (selectedServiceId != null) {
                        clientViewModel.placeOrder(
                            clientId = clientId,
                            serviceId = selectedServiceId!!,
                            address = address,
                            budget = budget.toDoubleOrNull() ?: 0.0
                        )
                        onOrderSuccess()
                    }
                },
                enabled = selectedServiceId != null && address.isNotBlank() && budget.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Kirim Pesanan", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PortfolioItemCard(portfolio: PortfolioEntity) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = portfolio.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = portfolio.description ?: "Tidak ada deskripsi proyek.",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
        }
    }
}
