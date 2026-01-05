package com.example.fespace.view.client

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fespace.viewmodel.ClientViewModel
import androidx.compose.material.icons.Icons
import  androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Download
import  androidx.compose.material.icons.filled.UploadFile

@Composable
fun OrderDetailScreen(
    orderId: Int,
    clientViewModel: ClientViewModel
) {// Di sini nanti ambil data detail order dan dokumen dari ViewModel
    Column(Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        Text("Detail Pesanan #$orderId", style = MaterialTheme.typography.headlineSmall)

        Card(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Status Saat Ini: DESIGN PROGRESS", color = Color.Blue)
                Text("Arsitek: Admin FeSpace")
                Button(onClick = { /* Logika Chat/WhatsApp Admin */ }) {
                    Icon(Icons.Default.Chat, null)
                    Text("Hubungi Arsitek")
                }
            }
        }

        Text("Dokumen dari Arsitek", fontWeight = FontWeight.Bold)
        // Item Dokumen yang bisa didownload
        OutlinedCard(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Draft_Desain_Final.pdf", Modifier.weight(1f))
                IconButton(onClick = { /* Logika Download */ }) {
                    Icon(Icons.Default.Download, null)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Upload Dokumen Tambahan (Opsional)", fontWeight = FontWeight.Bold)
        Button(onClick = { /* Logika Pick File & Upload */ }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.UploadFile, null)
            Text("Pilih File")
        }
    }
}
