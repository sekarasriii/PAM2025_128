package com.example.fespace.view.client

import java.text.DecimalFormat
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.fespace.ui.components.LocalImage
import com.example.fespace.ui.theme.*
import com.example.fespace.viewmodel.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderServiceScreen(
    clientViewModel: ClientViewModel,
    clientId: Int,
    serviceId: Int,
    onOrderSuccess: () -> Unit,
    onBackClick: () -> Unit
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
            TopAppBar(
                title = { Text("Pesan Jasa Desain", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
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
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            if (portfolios.isEmpty()) {
                Text(
                    "Belum ada portfolio tersedia",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
                    contentPadding = PaddingValues(vertical = Spacing.Small)
                ) {
                    items(portfolios) { item ->
                        PortfolioItemCard(item)
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = Gray700)

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
                        .fillMaxWidth(),
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

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = DarkSurface
                ) {
                    services.forEach { service ->
                        DropdownMenuItem(
                            text = { Text(service.nameServices, color = TextPrimary) },
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
                    containerColor = SurfaceContainerHigh,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(Radius.Small)
            ) {
                Icon(Icons.Default.CloudUpload, null, tint = Terracotta)
                Spacer(Modifier.width(Spacing.Small))
                Text(if (locationPhotoUri == null) "Upload Foto Lokasi" else "Foto Terpilih ✅")
            }

            // 3. Input Alamat
            var addressError by remember { mutableStateOf("") }
            OutlinedTextField(
                value = address,
                onValueChange = { 
                    address = it
                    addressError = when {
                        it.isBlank() -> "Alamat tidak boleh kosong"
                        it.all { char -> char.isDigit() || char.isWhitespace() || !char.isLetterOrDigit() } -> 
                            "Alamat tidak boleh hanya berisi angka/simbol"
                        !it.any { char -> char.isLetter() } -> "Alamat harus mengandung huruf"
                        else -> ""
                    }
                },
                label = { Text("Alamat Lokasi Proyek", color = TextSecondary) },
                placeholder = { Text("Contoh: Jl. Merdeka No. 10, Jakarta", color = TextTertiary) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = Terracotta,
                    unfocusedBorderColor = Gray700,
                    cursorColor = Terracotta,
                    focusedLabelColor = Terracotta,
                    unfocusedLabelColor = TextSecondary
                ),
                isError = addressError.isNotEmpty() && address.isNotEmpty(),
                supportingText = {
                    if (addressError.isNotEmpty() && address.isNotEmpty()) {
                        Text(addressError, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // 4. Input Budget
            var displayBudget by remember { mutableStateOf("") }
            OutlinedTextField(
                value = displayBudget,
                onValueChange = { input ->
                    val cleanInput = input.replace(".", "").replace(",", "")
                    if (cleanInput.all { it.isDigit() }) {
                        budget = cleanInput
                        displayBudget = if (cleanInput.isEmpty()) "" else {
                            val formatter = java.text.DecimalFormat("#,###")
                            formatter.format(cleanInput.toLong()).replace(",", ".")
                        }
                    }
                },
                label = { Text("Rencana Anggaran (Budget)", color = TextSecondary) },
                prefix = { Text("Rp ", color = Terracotta) },
                placeholder = { Text("Contoh: 50.000.000", color = TextTertiary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = Terracotta,
                    unfocusedBorderColor = Gray700,
                    cursorColor = Terracotta,
                    focusedLabelColor = Terracotta,
                    unfocusedLabelColor = TextSecondary
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )

            val isFormValid = selectedServiceId != null && 
                             address.isNotBlank() && 
                             addressError.isEmpty() && 
                             budget.isNotBlank() && 
                             locationPhotoUri != null

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Tombol Kirim (Simpan ke DB sesuai Flowchart)
            Button(
                onClick = {
                    if (isFormValid) {
                        clientViewModel.placeOrder(
                            clientId = clientId,
                            serviceId = selectedServiceId!!,
                            address = address,
                            budget = budget.toDoubleOrNull() ?: 0.0
                        )
                        onOrderSuccess()
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Terracotta,
                    contentColor = Cream,
                    disabledContainerColor = Gray800,
                    disabledContentColor = TextDisabled
                )
            ) {
                Text("Kirim Pesanan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
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
        shape = RoundedCornerShape(Radius.Medium),
        elevation = CardDefaults.cardElevation(Elevation.Card),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column {
            // Display portfolio image
            LocalImage(
                imagePath = portfolio.imagePath,
                contentDescription = portfolio.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
                showPlaceholder = true
            )

            Column(modifier = Modifier.padding(Spacing.Medium)) {
                Text(
                    text = portfolio.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(Spacing.ExtraSmall))
                Text(
                    text = portfolio.description ?: "Tidak ada deskripsi proyek.",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    color = TextSecondary
                )
            }
        }
    }
}
