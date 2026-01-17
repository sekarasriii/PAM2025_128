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
import com.example.fespace.ui.components.CategoryChip
import com.example.fespace.ui.components.PrimaryButton
import com.example.fespace.ui.theme.*
import com.example.fespace.ui.components.LocalImage
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    clientViewModel: ClientViewModel,
    onOrderClick: (Int) -> Unit,
    onViewOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSeeAllPortfolioClick: () -> Unit
) {
    // Data from database
    val services: List<ServiceEntity> by clientViewModel.availableServices.collectAsStateWithLifecycle(initialValue = emptyList())
    val portfolios: List<PortfolioEntity> by clientViewModel.allPortfolios.collectAsStateWithLifecycle(initialValue = emptyList())

    // UI State
    var selectedCategory by remember { mutableStateOf("Residential") }
    var selectedServiceForDetail by remember { mutableStateOf<ServiceEntity?>(null) }
    var showSheet by remember { mutableStateOf(false) }

    val categories = listOf("Residential", "Commercial", "Interior", "Renovation")

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        "FeSpace",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ) 
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = DarkCharcoal,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = Terracotta
                ),
                actions = {
                    IconButton(onClick = onViewOrdersClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = "Pesanan Saya"
                        )
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        },
        containerColor = DarkCharcoal
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {
            // Welcome Section
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.Small)
                ) {
                    Text(
                        "Wujudkan Hunian Impian",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Terracotta
                    )
                    Text(
                        "Pilih kategori dan temukan inspirasi proyek Anda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            // Category Selection - Horizontal Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
                    Text(
                        "Kategori Desain",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = DarkSurface,
                                    selectedContainerColor = Terracotta,
                                    labelColor = TextSecondary,
                                    selectedLabelColor = DarkCharcoal
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = Gray700,
                                    selectedBorderColor = Terracotta,
                                    borderWidth = 1.5.dp
                                )
                            )
                        }
                    }
                }
            }

            // Portfolio Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Inspirasi Proyek",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    TextButton(onClick = onSeeAllPortfolioClick) {
                        Text("Lihat Semua", color = Terracotta)
                    }
                }
            }

            // Portfolio Cards
            val filteredPortfolios = portfolios.filter { 
                it.category.trim().equals(selectedCategory.trim(), ignoreCase = true) 
            }

            if (filteredPortfolios.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkSurface
                        ),
                        shape = RoundedCornerShape(Radius.Medium)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.ExtraLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Spacing.Small)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = TextTertiary
                                )
                                Text(
                                    "Belum ada inspirasi proyek",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)
                    ) {
                        items(filteredPortfolios) { portfolio ->
                            PortfolioHomeCard(portfolio)
                        }
                    }
                }
            }

            // Services Section
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Spacing.Small),
                    color = Gray700
                )
                Text(
                    "Layanan Tersedia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            // Service Cards
            val filteredServices = services.filter {
                it.category.trim().equals(selectedCategory.trim(), ignoreCase = true)
            }

            if (filteredServices.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkSurface
                        ),
                        shape = RoundedCornerShape(Radius.Medium)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.ExtraLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Spacing.Small)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = TextTertiary
                                )
                                Text(
                                    "Layanan belum tersedia",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredServices) { service ->
                    ServiceKatalogItem(
                        service = service,
                        onClick = {
                            selectedServiceForDetail = service
                            showSheet = true
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(Spacing.ExtraLarge)) }
        }

        // Bottom Sheet for Service Detail
        if (showSheet && selectedServiceForDetail != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                containerColor = DarkSurface
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
            .padding(Spacing.Large)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
    ) {
        // Service Name
        Text(
            service.nameServices,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        // Category Chip
        CategoryChip(
            text = service.category,
            backgroundColor = TerracottaAlpha,
            textColor = Terracotta
        )
        
        // Description
        Text(
            service.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        // Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = DarkCharcoal
            ),
            shape = RoundedCornerShape(Radius.Medium)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.Medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.Small)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Estimasi Waktu",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextTertiary
                        )
                        Text(
                            service.durationEstimate,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Mulai Dari",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextTertiary
                        )
                        Text(
                            com.example.fespace.utils.RupiahFormatter.formatToRupiah(service.priceStart),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    }
                }
            }
        }
        
        // Order Button
        PrimaryButton(
            text = "Pesan Jasa Sekarang",
            onClick = onConfirmPesan
        )
        
        Spacer(modifier = Modifier.height(Spacing.Medium))
    }
}

@Composable
fun ServiceKatalogItem(service: ServiceEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(Radius.Medium),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Service Icon
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(Radius.Small),
                color = DarkCharcoal
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Architecture,
                        contentDescription = null,
                        tint = BrownWarm
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(Spacing.Medium))
            
            // Service Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
            ) {
                Text(
                    service.nameServices,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "Estimasi: ${service.durationEstimate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            // Price
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Mulai dari",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                Text(
                    com.example.fespace.utils.RupiahFormatter.formatToRupiah(service.priceStart),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                )
            }
        }
    }
}

@Composable
fun PortfolioHomeCard(portfolio: PortfolioEntity) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp),
        shape = RoundedCornerShape(Radius.Medium),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Elevation.Card
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image or Gradient
            if (portfolio.imagePath != null) {
                LocalImage(
                    imagePath = portfolio.imagePath,
                    contentDescription = portfolio.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    showPlaceholder = false
                )
            } else {
                // Fallback gradient if no image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    BrownDark,
                                    BrownWarm
                                )
                            )
                        )
                )
            }
            
            // Dark overlay for text readability
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(0.7f)
                            )
                        )
                    )
            )

            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(Spacing.Medium)
            ) {
                // Category Chip
                Surface(
                    shape = RoundedCornerShape(Radius.Small),
                    color = AccentGold.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = portfolio.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.Small))
                
                // Title
                Text(
                    portfolio.title,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Description
                Text(
                    text = portfolio.description ?: "",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.ExtraSmall))

                // Year
                Text(
                    text = "Tahun ${portfolio.year}",
                    color = TextTertiary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}