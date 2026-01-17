package com.example.fespace.view.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fespace.ui.components.LocalImage
import com.example.fespace.ui.theme.*
import com.example.fespace.viewmodel.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientPortfolioScreen(
    clientViewModel: ClientViewModel,
    onBack: () -> Unit
) {
    val portfolios =
        clientViewModel.allPortfolios.collectAsStateWithLifecycle(initialValue = emptyList()).value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Inspirasi Portfolio",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
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
        if (portfolios.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextTertiary
                    )
                    Text(
                        "Belum ada portfolio",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(Spacing.Medium),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
                verticalArrangement = Arrangement.spacedBy(Spacing.Small)
            ) {
                items(portfolios) { portfolio ->
                    PortfolioGridCard(portfolio)
                }
            }
        }
    }
}

@Composable
fun PortfolioGridCard(portfolio: com.example.fespace.data.local.entity.PortfolioEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(Radius.Medium),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.Card)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image
            LocalImage(
                imagePath = portfolio.imagePath,
                contentDescription = portfolio.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                showPlaceholder = true
            )
            
            // Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, DarkCharcoal.copy(alpha = 0.9f))
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Spacing.Small)
            ) {
                Text(
                    portfolio.title,
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    portfolio.category,
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
                Text(
                    "Tahun ${portfolio.year}",
                    color = TextTertiary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
