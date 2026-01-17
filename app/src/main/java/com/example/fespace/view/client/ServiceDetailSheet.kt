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
import androidx.compose.material3.BottomSheetDefaults
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
import com.example.fespace.ui.theme.*
import androidx.compose.material3.ButtonDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailSheet(
    service: ServiceEntity,
    onDismiss: () -> Unit,
    onConfirmPesan: () -> Unit // Ke Form Order
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface, // Dark surface for bottom sheet
        dragHandle = { BottomSheetDefaults.DragHandle(color = Gray700) }
    ) {
        Column(Modifier
            .padding(Spacing.Large)
            .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
            Text(
                service.nameServices,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Badge(
                containerColor = AccentGold,
                contentColor = DarkCharcoal
            ) { 
                Text(service.category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall) 
            }

            Spacer(modifier = Modifier.height(Spacing.ExtraSmall))

            Text("Deskripsi", fontWeight = FontWeight.Bold, color = AccentGold, style = MaterialTheme.typography.titleSmall)
            Text(service.description, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(Spacing.Small))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Estimasi Pengerjaan", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Text(service.durationEstimate, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Mulai Harga", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Text("Rp ${service.priceStart}", fontWeight = FontWeight.Bold, color = AccentGold)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.Medium))

            Button(
                onClick = onConfirmPesan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(Radius.Medium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Terracotta,
                    contentColor = Cream
                )
            ) {
                Text("Pesan Jasa Sekarang", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(Spacing.Large))
        }
    }
}
