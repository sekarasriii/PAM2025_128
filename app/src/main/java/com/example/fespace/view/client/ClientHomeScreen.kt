package com.example.fespace.view.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fespace.data.local.entity.PortfolioEntity
import com.example.fespace.data.local.entity.ServiceEntity
import com.example.fespace.viewmodel.ClientViewModel
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    clientViewModel: ClientViewModel,
    onOrderClick: (Int) -> Unit,
    onViewOrdersClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // 1. Ambil data asli dari database (Flow)
    val services: List<ServiceEntity> by clientViewModel.availableServices.collectAsStateWithLifecycle(initialValue = emptyList())
    LaunchedEffect(services) {
        println("DEBUG_LOG: Total Layanan di DB = ${services.size}")
        services.forEach {
            println("DEBUG_LOG: Nama: ${it.nameServices}, Kategori: '${it.category}'")
        }
    }

    val portfolios: List<PortfolioEntity> by clientViewModel.allPortfolios.collectAsStateWithLifecycle(initialValue = emptyList())

    // State UI
    var selectedCategory by remember { mutableStateOf("Residential") }
    var selectedServiceForDetail by remember { mutableStateOf<ServiceEntity?>(null) }
    var showSheet by remember { mutableStateOf(false) }

    val categories = listOf("Residential", "Commercial", "Interior", "Renovation")

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("FeSpace Katalog", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onViewOrdersClick) {
                        // MENGGUNAKAN VERSI AUTOMIRRORED
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = "Pesanan Saya"
                        )
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- SECTION 1: PILIH KATEGORI ---
            item {
                Text("Pilih Kategori Desain", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(categories) { category ->
                val isSelected = selectedCategory == category
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { selectedCategory = category },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    if (isSelected)
                                        listOf(Color(0xFF1A237E), Color(0xFF3949AB))
                                    else
                                        listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))
                                )
                            )
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else Color.Black,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- SECTION 2: PORTFOLIO (INSPIRASI PROYEK) ---
            item {
                Text("Inspirasi Proyek $selectedCategory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            // Filter Portfolio (Sesuai seleksi Anda)
            val filteredPortfolios = portfolios.filter { it.category.trim().equals(selectedCategory.trim(), ignoreCase = true) }

            if (filteredPortfolios.isEmpty()) {
                item { Text("Belum ada inspirasi proyek untuk kategori ini.", color = Color.Gray, style = MaterialTheme.typography.bodySmall) }
            } else {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filteredPortfolios) { portfolio ->
                            PortfolioHomeCard(portfolio)
                        }
                    }
                }
            }

            // --- SECTION 3: LAYANAN TERSEDIA ---
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Layanan Pesan Jasa $selectedCategory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            // Filter Services (PASTIKAN BAGIAN INI SAMA DENGAN PORTFOLIO)
            val filteredServices = services.filter {
                it.category.trim().equals(selectedCategory.trim(), ignoreCase = true)
            }

            if (filteredServices.isEmpty()) {
                item { Text("Layanan belum tersedia untuk kategori ini.", color = Color.Gray, style = MaterialTheme.typography.bodySmall) }
            } else {
                items(filteredServices) { service ->
                    ServiceKatalogItem(service, onClick = {
                        selectedServiceForDetail = service
                        showSheet = true
                    })
                }
            }

            item { Spacer(modifier = Modifier.height(50.dp)) }
        }

        // --- BOTTOM SHEET DETAIL JASA ---
        if (showSheet && selectedServiceForDetail != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false }
            ) {
                ServiceDetailContent(
                    service = selectedServiceForDetail!!,
                    onConfirmPesan = {
                        showSheet = false
                        onOrderClick(selectedServiceForDetail!!.idServices)
                    }
                )
            }
        }
    }
}

@Composable
fun ServiceDetailContent(service: ServiceEntity, onConfirmPesan: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(service.nameServices, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(service.description)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Estimasi: ${service.durationEstimate}")
            Text("Rp ${service.priceStart}", fontWeight = FontWeight.Bold, color = Color.Blue)
        }
        Button(onClick = onConfirmPesan, modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)) {
            Text("Pesan Jasa Sekarang")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ServiceKatalogItem(service: ServiceEntity, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(service.nameServices, fontWeight = FontWeight.Bold)
                Text("Estimasi: ${service.durationEstimate}", style = MaterialTheme.typography.bodySmall)
            }
            Text("Rp ${service.priceStart}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PortfolioHomeCard(portfolio: PortfolioEntity) {
    Card(
        modifier = Modifier
            .width(240.dp) // Diperlebar agar deskripsi terbaca
            .height(160.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)) {
            // Overlay gelap agar teks putih terbaca
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.8f)))))

            Column(Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)) {
                Text(portfolio.title, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)

                // Menampilkan Deskripsi Baru
                Text(
                    text = portfolio.description ?: "",
                    color = Color.White.copy(0.8f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${portfolio.category} • ${portfolio.year}",
                    color = Color.Cyan,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}