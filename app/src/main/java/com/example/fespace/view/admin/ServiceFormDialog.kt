package com.example.fespace.view.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fespace.data.local.entity.ServiceEntity
import com.example.fespace.ui.theme.*
import com.example.fespace.utils.RupiahFormatter
import com.example.fespace.utils.ValidationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceFormDialog(
    service: ServiceEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Double, String, String, String?) -> Unit
) {
    val context = LocalContext.current
    
    var name by remember { mutableStateOf(service?.nameServices ?: "") }
    var category by remember { mutableStateOf(service?.category ?: "residential") }
    var description by remember { mutableStateOf(service?.description ?: "") }
    var price by remember { mutableStateOf(service?.priceStart?.toLong()?.toString() ?: "") }
    var duration by remember { mutableStateOf(service?.durationEstimate ?: "1-2 Minggu") }
    var features by remember { mutableStateOf(service?.features ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf(service?.imagePath) }
    
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showDurationDropdown by remember { mutableStateOf(false) }
    
    // Error states
    var nameError by remember { mutableStateOf("") }
    var descError by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf("") }
    
    val durations = listOf("3-5 Hari", "1-2 Minggu", "1 Bulan", "3 Bulan", "6 Bulan", "1 Tahun")
    val categories = listOf("residential" to "Residential", "commercial" to "Commercial", "renovation" to "Renovation", "interior" to "Interior")
    
    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? -> uri?.let { selectedImageUri = it } }
    
    // Validation with error updates
    val isFormValid = remember(name, description, price, category) {
        val nameVal = ValidationUtils.validateServiceName(name)
        nameError = if (!nameVal.isValid) nameVal.errorMessage else ""
        
        val descVal = ValidationUtils.validateDescription(description)
        descError = if (!descVal.isValid) descVal.errorMessage else ""
        
        val priceVal = ValidationUtils.validatePrice(price)
        priceError = if (!priceVal.isValid) priceVal.errorMessage else ""
        
        nameVal.isValid && descVal.isValid && priceVal.isValid && category.isNotBlank()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (service == null) "Tambah Layanan" else "Edit Layanan", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(500.dp)) {
                // Name field with error
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            val validation = ValidationUtils.validateServiceName(it)
                            nameError = if (!validation.isValid) validation.errorMessage else ""
                        },
                        label = { Text("Nama Layanan *", color = AccentGold) },
                        placeholder = { Text("Contoh: Desain 3D Rumah", color = TextSecondary.copy(alpha = 0.5f)) },
                        isError = nameError.isNotEmpty(),
                        supportingText = {
                            if (nameError.isNotEmpty()) Text(nameError, color = AccentRed)
                            else Text("Harus kombinasi huruf dengan angka/simbol", color = TextSecondary.copy(alpha = 0.7f))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary, 
                            unfocusedTextColor = TextPrimary, 
                            focusedBorderColor = Terracotta, 
                            unfocusedBorderColor = AccentGold.copy(alpha = 0.5f),
                            errorBorderColor = AccentRed,
                            focusedLabelColor = Terracotta,
                            unfocusedLabelColor = TextSecondary
                        )
                    )
                }
                
                // Category dropdown
                item {
                    ExposedDropdownMenuBox(expanded = showCategoryDropdown, onExpandedChange = { showCategoryDropdown = it }) {
                        OutlinedTextField(
                            value = categories.find { it.first == category }?.second ?: category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori *", color = AccentGold) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary, 
                                unfocusedTextColor = TextPrimary, 
                                focusedBorderColor = Terracotta, 
                                unfocusedBorderColor = AccentGold.copy(alpha = 0.5f)
                            )
                        )
                        ExposedDropdownMenu(expanded = showCategoryDropdown, onDismissRequest = { showCategoryDropdown = false }) {
                            categories.forEach { (key, label) ->
                                DropdownMenuItem(text = { Text(label, color = TextPrimary) }, onClick = { category = key; showCategoryDropdown = false }, colors = MenuDefaults.itemColors(textColor = TextPrimary))
                            }
                        }
                    }
                }
                
                // Description field with error
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { 
                            description = it
                            val validation = ValidationUtils.validateDescription(it)
                            descError = if (!validation.isValid) validation.errorMessage else ""
                        },
                        label = { Text("Deskripsi *", color = AccentGold) },
                        placeholder = { Text("Jelaskan detail layanan...", color = TextSecondary.copy(alpha = 0.5f)) },
                        isError = descError.isNotEmpty(),
                        supportingText = {
                            if (descError.isNotEmpty()) Text(descError, color = AccentRed)
                            else Text("Minimal 20 karakter, kombinasi huruf+angka/simbol", color = TextSecondary.copy(alpha = 0.7f))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary, 
                            unfocusedTextColor = TextPrimary, 
                            focusedBorderColor = Terracotta, 
                            unfocusedBorderColor = AccentGold.copy(alpha = 0.5f),
                            errorBorderColor = AccentRed
                        )
                    )
                }
                
                // Price field with error
                item {
                    OutlinedTextField(
                        value = RupiahFormatter.formatInputWithDots(price),
                        onValueChange = { 
                            if (it.replace(".", "").length <= 12) {
                                price = it.replace(".", "")
                                val validation = ValidationUtils.validatePrice(price)
                                priceError = if (!validation.isValid) validation.errorMessage else ""
                            }
                        },
                        label = { Text("Harga Mulai *", color = AccentGold) },
                        leadingIcon = { Text("Rp", color = AccentGold, fontWeight = FontWeight.Bold) },
                        placeholder = { Text("1.000.000", color = TextSecondary.copy(alpha = 0.5f)) },
                        isError = priceError.isNotEmpty(),
                        supportingText = {
                            if (priceError.isNotEmpty()) Text(priceError, color = AccentRed)
                            else Text("Harga harus lebih dari 0", color = TextSecondary.copy(alpha = 0.7f))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary, 
                            unfocusedTextColor = TextPrimary, 
                            focusedBorderColor = Terracotta, 
                            unfocusedBorderColor = AccentGold.copy(alpha = 0.5f),
                            errorBorderColor = AccentRed
                        )
                    )
                }
                
                // Duration dropdown
                item {
                    ExposedDropdownMenuBox(expanded = showDurationDropdown, onExpandedChange = { showDurationDropdown = it }) {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Durasi *", color = AccentGold) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDurationDropdown) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary, 
                                unfocusedTextColor = TextPrimary, 
                                focusedBorderColor = Terracotta, 
                                unfocusedBorderColor = AccentGold.copy(alpha = 0.5f)
                            )
                        )
                        ExposedDropdownMenu(expanded = showDurationDropdown, onDismissRequest = { showDurationDropdown = false }) {
                            durations.forEach { dur ->
                                DropdownMenuItem(text = { Text(dur, color = TextPrimary) }, onClick = { duration = dur; showDurationDropdown = false })
                            }
                        }
                    }
                }
                
                // Features field
                item {
                    OutlinedTextField(
                        value = features,
                        onValueChange = { features = it },
                        label = { Text("Fitur (Opsional)", color = AccentGold) },
                        placeholder = { Text("Contoh: 3D Render, Revisi 2x", color = TextSecondary.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary, 
                            unfocusedTextColor = TextPrimary, 
                            focusedBorderColor = Terracotta, 
                            unfocusedBorderColor = AccentGold.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isFormValid) {
                        val finalImagePath = selectedImageUri?.let {
                            val fileName = "service_${System.currentTimeMillis()}.jpg"
                            val file = context.filesDir.resolve(fileName)
                            context.contentResolver.openInputStream(it)?.use { input ->
                                file.outputStream().use { output -> input.copyTo(output) }
                            }
                            file.absolutePath
                        } ?: imagePath
                        
                        onSave(name, category, description, price.toDouble(), duration, features, finalImagePath)
                        onDismiss()
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = Cream)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        containerColor = DarkSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary
    )
}
