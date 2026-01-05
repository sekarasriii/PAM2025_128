package com.example.fespace.view.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fespace.viewmodel.ClientViewModel

@Composable
fun ClientPortfolioScreen(
    clientViewModel: ClientViewModel
) {
    val portfolios =
        clientViewModel.portfoliosByCategory.collectAsStateWithLifecycle().value

    LaunchedEffect(portfolios) {
        println("PORTFOLIO CLIENT: ${portfolios.size}")
        portfolios.forEach {
            println("➡ ${it.title} | ${it.category}")
        }
    }

    if (portfolios.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Belum ada portfolio pada kategori ini")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(portfolios) { portfolio ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            portfolio.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            portfolio.description ?: "-",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
