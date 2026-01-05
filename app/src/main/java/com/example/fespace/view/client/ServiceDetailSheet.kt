package com.example.fespace.view.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fespace.data.local.entity.ServiceEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailSheet(
    service: ServiceEntity,
    onDismiss: () -> Unit,
    onConfirmPesan: () -> Unit // Ke Form Order
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier
            .padding(24.dp)
            .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(service.nameServices, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Badge { Text(service.category) }

            Text("Deskripsi", fontWeight = FontWeight.Bold)
            Text(service.description)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Estimasi Pengerjaan", style = MaterialTheme.typography.labelMedium)
                    Text(service.durationEstimate, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Mulai Harga", style = MaterialTheme.typography.labelMedium)
                    Text("Rp ${service.priceStart}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Button(
                onClick = onConfirmPesan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Pesan Jasa Sekarang")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
