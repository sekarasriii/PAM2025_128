package com.example.fespace.view.client

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fespace.ui.components.LocalImage
import com.example.fespace.ui.theme.*
import com.example.fespace.utils.FileUtils
import com.example.fespace.viewmodel.ClientViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    clientViewModel: ClientViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { com.example.fespace.utils.SessionManager(context) }
    val currentUserId = sessionManager.getUserId()
    
    // Reactive state - automatically updates when database changes
    val order by clientViewModel.getOrderByIdFlow(orderId).collectAsStateWithLifecycle(initialValue = null)
    
    // File picker launcher for client document upload (JPG, PNG, PDF)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Check file size
            val fileSizeMB = FileUtils.getFileSizeMB(context, it)
            if (fileSizeMB > 10) {
                Toast.makeText(context, "File terlalu besar! Maksimal 10MB", Toast.LENGTH_LONG).show()
                return@rememberLauncherForActivityResult
            }
            
            // Save file
            val savedPath = FileUtils.saveFileToInternalStorage(context, it)
            if (savedPath != null) {
                order?.let { currentOrder ->
                    clientViewModel.updateClientDocument(currentOrder, savedPath, currentUserId)
                }
                Toast.makeText(context, "Dokumen berhasil diupload", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal upload. Pastikan file JPG/PNG/PDF dan < 10MB", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Detail Pesanan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Cream
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Terracotta
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCharcoal,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = DarkCharcoal
    ) { padding ->
        if (order == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Terracotta)
            }
        } else {
            val currentOrder = order!!
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // === HERO SECTION ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    DarkSurface,
                                    DarkCharcoal
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.Large),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Order ID Badge
                        Surface(
                            shape = RoundedCornerShape(Radius.Small),
                            color = TerracottaAlpha
                        ) {
                            Text(
                                "Order #$orderId",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = Terracotta,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        Spacer(Modifier.weight(1f))
                        
                        // Project Title
                        Text(
                            "Service ID: ${currentOrder.idServices}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Cream,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(Modifier.height(Spacing.Small))
                        
                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(Radius.Medium),
                            color = when (currentOrder.status.lowercase()) {
                                "pending" -> StatusPending.copy(alpha = 0.2f)
                                "in progress", "in_progress" -> StatusInProgress.copy(alpha = 0.2f)
                                "approved", "completed" -> StatusApproved.copy(alpha = 0.2f)
                                "in design", "in_design" -> StatusInDesign.copy(alpha = 0.2f)
                                "delivered" -> StatusDelivered.copy(alpha = 0.2f)
                                "cancelled", "rejected" -> StatusCancelled.copy(alpha = 0.2f)
                                else -> Gray600.copy(alpha = 0.2f)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val statusColor = when (currentOrder.status.lowercase()) {
                                    "pending" -> StatusPending
                                    "in progress", "in_progress" -> StatusInProgress
                                    "approved", "completed" -> StatusApproved
                                    "in design", "in_design" -> StatusInDesign
                                    "delivered" -> StatusDelivered
                                    "cancelled", "rejected" -> StatusCancelled
                                    else -> TextSecondary
                                }
                                val statusLabel = when (currentOrder.status.lowercase()) {
                                    "pending" -> "MENUNGGU"
                                    "in progress", "in_progress" -> "PROSES"
                                    "approved", "completed" -> "SELESAI"
                                    "in design", "in_design" -> "DESAIN"
                                    "delivered" -> "TERKIRIM"
                                    "cancelled", "rejected" -> "DIBATALKAN"
                                    else -> currentOrder.status.uppercase()
                                }
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = statusColor
                                )
                                Text(
                                    statusLabel,
                                    color = statusColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                
                // === CONTENT SECTION ===
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.Large),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Large)
                ) {
                    // Contact Architect Button
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW)
                                val adminPhone = "6281234567890"
                                val message = "Halo Admin, saya ingin bertanya tentang pesanan #$orderId"
                                val url = "https://wa.me/$adminPhone?text=${Uri.encode(message)}"
                                intent.data = Uri.parse(url)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen,
                            contentColor = DarkCharcoal
                        ),
                        shape = RoundedCornerShape(Radius.Medium)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Hubungi Arsitek", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    // Project Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(Radius.Large),
                        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.Card)
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.Large),
                            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
                        ) {
                            Text(
                                "Detail Proyek",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            
                            HorizontalDivider(color = Gray700, thickness = 1.dp)
                            
                            // Location
                            DetailRow(
                                icon = Icons.Default.LocationOn,
                                label = "Lokasi",
                                value = currentOrder.locationAddress
                            )
                            
                            // Budget
                            DetailRow(
                                icon = Icons.Default.AttachMoney,
                                label = "Budget",
                                value = com.example.fespace.utils.RupiahFormatter.formatToRupiah(currentOrder.budget),
                                valueColor = AccentGold
                            )
                            
                            // Date
                            DetailRow(
                                icon = Icons.Default.DateRange,
                                label = "Tanggal Order",
                                value = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID")).format(java.util.Date(currentOrder.createAt))
                            )
                        }
                    }
                    
                    // Design from Architect
                    if (currentOrder.designPath != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(Radius.Large),
                            elevation = CardDefaults.cardElevation(defaultElevation = Elevation.Card)
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.Large),
                                verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                                ) {
                                    Icon(
                                        Icons.Default.Architecture,
                                        contentDescription = null,
                                        tint = AccentGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        "Hasil Desain",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                LocalImage(
                                    imagePath = currentOrder.designPath,
                                    contentDescription = "Design Preview",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .clip(RoundedCornerShape(Radius.Medium)),
                                    showPlaceholder = true,
                                    clickable = true
                                )
                                
                                // Download/Preview Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                                ) {
                                    OutlinedButton(
                                        onClick = { 
                                            com.example.fespace.utils.FileDownloadHelper.openFile(
                                                context, 
                                                currentOrder.designPath
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = AccentGold
                                        )
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Preview")
                                    }
                                    
                                    Button(
                                        onClick = { 
                                            com.example.fespace.utils.FileDownloadHelper.shareFile(
                                                context, 
                                                currentOrder.designPath
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AccentGold,
                                            contentColor = DarkCharcoal
                                        )
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Download")
                                    }
                                }
                            }
                        }
                    }
                    
                    // Client Document Upload
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(Radius.Large),
                        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.Card)
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.Large),
                            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                            ) {
                                Icon(
                                    Icons.Default.UploadFile,
                                    contentDescription = null,
                                    tint = Terracotta,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    "Dokumen Proyek",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            if (currentOrder.clientDocumentPath != null) {
                                LocalImage(
                                    imagePath = currentOrder.clientDocumentPath,
                                    contentDescription = "Client Document",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(Radius.Medium)),
                                    showPlaceholder = true,
                                    clickable = true
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SageGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        "Dokumen telah diunggah",
                                        color = SageGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            Button(
                                onClick = { 
                                    launcher.launch(arrayOf("image/jpeg", "image/png", "application/pdf"))
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Terracotta,
                                    contentColor = Cream
                                ),
                                shape = RoundedCornerShape(Radius.Medium)
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null)
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    if (currentOrder.clientDocumentPath != null) "Ganti Dokumen" else "Upload Dokumen",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Text(
                                "Format: JPG, PNG, PDF • Max: 10MB",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(Spacing.Large))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Terracotta,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
