package com.example.fespace.view.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fespace.data.local.entity.PortfolioEntity
import com.example.fespace.ui.theme.*
import com.example.fespace.utils.ImageUploadHelper
import com.example.fespace.utils.ValidationUtils
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioFormDialog(
    portfolio: PortfolioEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int, String?) -> Unit
) {
    val context = LocalContext.current
    
    var title by remember { mutableStateOf(portfolio?.title ?: "") }
    var description by remember { mutableStateOf(portfolio?.description ?: "") }
    var category by remember { mutableStateOf(portfolio?.category ?: "") }
    var year by remember { mutableStateOf(portfolio?.year?.toString() ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf(portfolio?.imagePath) }
    
    var showCategoryDropdown by remember { mutableStateOf(false) }
    
    // Validation states
    var titleError by remember { mutableStateOf("") }
    var descError by remember { mutableStateOf("") }
    var yearError by remember { mutableStateOf("") }
    var imageError by remember { mutableStateOf("") }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = ImageUploadHelper.getFileName(context, it)
            val formatValidation = ValidationUtils.validateFileFormat(fileName)
            
            if (!formatValidation.isValid) {
                imageError = formatValidation.errorMessage
                return@let
            }
            
            val fileSize = ImageUploadHelper.getFileSize(context, it)
            val sizeValidation = ValidationUtils.validateFileSize(fileSize)
            
            if (!sizeValidation.isValid) {
                imageError = sizeValidation.errorMessage
                return@let
            }
            
            selectedImageUri = it
            imageError = ""
        }
    }
    
    val categories = listOf(
        "residential" to "Residential",
        "commercial" to "Commercial",
        "renovation" to "Renovation",
        "interior" to "Interior"
    )
    
    // Check if form is valid
    val isFormValid = remember(title, description, category, year) {
        val titleVal = ValidationUtils.validatePortfolioTitle(title)
        val descVal = ValidationUtils.validateDescription(description)
        val yearVal = ValidationUtils.validateYear(year)
        
        titleVal.isValid && descVal.isValid && yearVal.isValid && category.isNotBlank()
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (portfolio == null) "Tambah Portfolio Baru" else "Edit Portfolio",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(550.dp)
            ) {
                item {
                    Text(
                        "Informasi Proyek",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Terracotta
                    )
                }
                
                // Title field
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { 
                            title = it
                            val validation = ValidationUtils.validatePortfolioTitle(it)
                            titleError = if (!validation.isValid) validation.errorMessage else ""
                        },
                        label = { Text("Judul Proyek *", color = AccentGold) },
                        placeholder = { Text("Contoh: Villa Modern Bali", color = TextSecondary.copy(alpha = 0.5f)) },
                        isError = titleError.isNotEmpty(),
                        supportingText = {
                            if (titleError.isNotEmpty()) Text(titleError, color = AccentRed)
                            else Text("5-200 karakter", color = TextSecondary.copy(alpha = 0.7f))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Radius.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = AccentGold,
                            unfocusedLabelColor = TextSecondary,
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = AccentGold.copy(alpha = 0.5f)
                        )
                    )
                }
                
                // Category dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = showCategoryDropdown,
                        onExpandedChange = { showCategoryDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = categories.find { it.first == category }?.second ?: category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori *", color = AccentGold) },
                            trailingIcon = {
                                Icon(
                                    if (showCategoryDropdown) Icons.Default.KeyboardArrowUp 
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = AccentGold
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(Radius.Medium),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedLabelColor = AccentGold,
                                unfocusedLabelColor = TextSecondary,
                                focusedBorderColor = Terracotta,
                                unfocusedBorderColor = AccentGold.copy(alpha = 0.5f)
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = showCategoryDropdown,
                            onDismissRequest = { showCategoryDropdown = false }
                        ) {
                            categories.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label, color = TextPrimary) },
                                    onClick = {
                                        category = key
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Description field
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { 
                            description = it
                            val validation = ValidationUtils.validateDescription(it)
                            descError = if (!validation.isValid) validation.errorMessage else ""
                        },
                        label = { Text("Deskripsi Proyek *", color = AccentGold) },
                        placeholder = { Text("Jelaskan detail proyek yang dikerjakan...", color = TextSecondary.copy(alpha = 0.5f)) },
                        isError = descError.isNotEmpty(),
                        supportingText = {
                            if (descError.isNotEmpty()) Text(descError, color = AccentRed)
                            else Text("Minimal 20 karakter", color = TextSecondary.copy(alpha = 0.7f))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(Radius.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = AccentGold,
                            unfocusedLabelColor = TextSecondary,
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = AccentGold.copy(alpha = 0.5f)
                        )
                    )
                }
                
                // Year field
                item {
                    OutlinedTextField(
                        value = year,
                        onValueChange = { 
                            year = it.filter { char -> char.isDigit() }.take(4)
                            val validation = ValidationUtils.validateYear(year)
                            yearError = if (!validation.isValid) validation.errorMessage else ""
                        },
                        label = { Text("Tahun Pengerjaan *", color = AccentGold) },
                        placeholder = { Text("2024", color = TextSecondary.copy(alpha = 0.5f)) },
                        isError = yearError.isNotEmpty(),
                        supportingText = {
                            if (yearError.isNotEmpty()) Text(yearError, color = AccentRed)
                            else Text("Tahun proyek diselesaikan (1900-sekarang)", color = TextSecondary.copy(alpha = 0.7f))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Radius.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = AccentGold,
                            unfocusedLabelColor = TextSecondary,
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = AccentGold.copy(alpha = 0.5f)
                        )
                    )
                }
                
                // Image upload section
                item {
                    Text(
                        "Gambar Portfolio",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Terracotta
                    )
                }
                
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(Radius.Medium),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = AccentGold)
                            Spacer(Modifier.width(8.dp))
                            Text("Pilih Gambar")
                        }
                        
                        if (selectedImageUri != null || imagePath != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                shape = RoundedCornerShape(Radius.Medium)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Image preview using Coil
                                    AsyncImage(
                                        model = selectedImageUri ?: if (imagePath != null) File(imagePath!!) else null,
                                        contentDescription = "Preview",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    
                                    IconButton(
                                        onClick = {
                                            selectedImageUri = null
                                            imagePath = null
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Hapus",
                                            tint = AccentRed
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (imageError.isNotEmpty()) {
                            Text(imageError, color = AccentRed, fontSize = 12.sp)
                        } else {
                            Text(
                                "Format: JPG, PNG, PDF • Maks: 10 MB",
                                color = TextSecondary.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalImagePath = if (selectedImageUri != null) {
                        ImageUploadHelper.saveImageToInternalStorage(context, selectedImageUri!!, "portfolio")
                    } else {
                        imagePath
                    }
                    
                    onSave(
                        title.trim(),
                        description.trim(),
                        category,
                        year.toInt(),
                        finalImagePath
                    )
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) Terracotta else TextTertiary,
                    contentColor = Cream
                ),
                shape = RoundedCornerShape(Radius.Medium)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Simpan Portfolio")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(Radius.Medium),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextSecondary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, TextSecondary.copy(alpha = 0.3f))
            ) {
                Text("Batal")
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp)
    )
}
