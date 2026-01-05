package com.example.fespace.view.admin

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.fespace.data.local.entity.ServiceEntity
import com.example.fespace.data.local.entity.PortfolioEntity
import OrderEntity
import com.example.fespace.viewmodel.AdminViewModel
import kotlinx.coroutines.launch

// 1. Fungsi Validasi
fun isTextInputValid(text: String): Boolean {
    if (text.isBlank()) return false
    val trimmed = text.trim()
    val firstChar = trimmed.first()
    return firstChar.isLetter() && trimmed.any { it.isLetter() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel,
    navController: NavHostController
) {
    // State data dari ViewModel
    val services by adminViewModel.services.collectAsStateWithLifecycle()
    val portfolios by adminViewModel.portfolios.collectAsStateWithLifecycle()
    val orders by adminViewModel.orders.collectAsStateWithLifecycle()

    val clientCount by adminViewModel.clientCount.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // State UI
    var selectedMenu by remember { mutableStateOf("Dashboard") }
    var showServiceDialog by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<ServiceEntity?>(null) }
    var showPortfolioDialog by remember { mutableStateOf(false) }
    var selectedPortfolio by remember { mutableStateOf<PortfolioEntity?>(null) }

    // State Konfirmasi Hapus
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Any?>(null) }

    // --- LOGIKA DIALOG KONFIRMASI HAPUS ---
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus data ini? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        when (val item = itemToDelete) {
                            is ServiceEntity -> adminViewModel.deleteService(item)
                            is PortfolioEntity -> adminViewModel.deletePortfolio(item)
                            is OrderEntity -> adminViewModel.deleteOrder(item)
                        }
                        showDeleteConfirm = false
                        itemToDelete = null
                        Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal") }
            }
        )
    }

    // --- LOGIKA DIALOG SERVICE ---
    // --- LOGIKA DIALOG SERVICE ---
    if (showServiceDialog) {
        ServiceFormDialog(
            service = selectedService,
            onDismiss = { showServiceDialog = false; selectedService = null },
            onConfirm = { service ->
                if (selectedService == null) {
                    // JIKA TAMBAH BARU: Pastikan parameter 'category' disertakan
                    adminViewModel.addService(
                        name = service.nameServices,
                        category = service.category, // <--- INI KUNCINYA
                        desc = service.description,
                        price = service.priceStart,
                        duration = service.durationEstimate,
                        features = service.features ?: "",
                        adminId = 1
                    )
                } else {
                    // JIKA EDIT:
                    adminViewModel.updateService(service)
                }
                showServiceDialog = false
            }
        )
    }


    // --- LOGIKA DIALOG PORTFOLIO ---
    if (showPortfolioDialog) {
        PortfolioFormDialog(
            portfolio = selectedPortfolio,
            onDismiss = { showPortfolioDialog = false; selectedPortfolio = null },
            onConfirm = { title, desc, category, year ->
                if (selectedPortfolio == null) {
                    // PERBAIKAN DI SINI:
                    // Urutan: title, desc, category, year, imagePath, adminId
                    adminViewModel.addPortfolio(
                        title = title,
                        desc = desc,
                        category = category,
                        year = year,
                        imagePath = null, // Tambahkan null untuk imagePath (karena String?)
                        adminId = 1       // Berikan 1 untuk adminId (Int)
                    )
                } else {
                    val updated = selectedPortfolio!!.copy(
                        title = title,
                        description = desc,
                        category = category,
                        year = year,
                        updateAt = System.currentTimeMillis() // Pastikan nama variabel sesuai Entity (snake_case)
                    )
                    adminViewModel.updatePortfolio(updated)
                }
                showPortfolioDialog = false
            }
        )
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text("FeSpace Admin", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                HorizontalDivider()
                NavigationDrawerItem(label = { Text("Dashboard") }, selected = selectedMenu == "Dashboard", onClick = { selectedMenu = "Dashboard"; scope.launch { drawerState.close() } }, icon = { Icon(Icons.Default.Home, null) })
                NavigationDrawerItem(label = { Text("Kelola Layanan") }, selected = selectedMenu == "Services", onClick = { selectedMenu = "Services"; scope.launch { drawerState.close() } }, icon = { Icon(Icons.Default.Settings, null) })
                NavigationDrawerItem(label = { Text("Portfolio") }, selected = selectedMenu == "Portfolio", onClick = { selectedMenu = "Portfolio"; scope.launch { drawerState.close() } }, icon = { Icon(Icons.Default.Image, null) })
                NavigationDrawerItem(label = { Text("Pesanan Masuk") }, selected = selectedMenu == "Orders", onClick = { selectedMenu = "Orders"; scope.launch { drawerState.close() } }, icon = { Icon(Icons.Default.ShoppingCart, null) })
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            // PERBAIKAN: Pastikan tanda kurung kurawal { } mengapit popUpTo
                            navController.navigate("welcome") {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red) },
                    // Ganti unselectedLabelColor menjadi unselectedTextColor (Material 3)
                    colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = Color.Red)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedMenu) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null) } }
                )
            },
            floatingActionButton = {
                if (selectedMenu == "Services" || selectedMenu == "Portfolio") {
                    FloatingActionButton(onClick = {
                        if(selectedMenu == "Services") { selectedService = null; showServiceDialog = true }
                        else { selectedPortfolio = null; showPortfolioDialog = true }
                    }) { Icon(Icons.Default.Add, null) }
                }
            }
        ) { padding ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)) {
                when (selectedMenu) {
                    "Dashboard" -> AdminOverviewContent(services.size, portfolios.size, orders.size, clientCount = clientCount)
                    "Services" -> ServiceManagementList(services, { selectedService = it; showServiceDialog = true }, { itemToDelete = it; showDeleteConfirm = true })
                    "Portfolio" -> PortfolioManagementList(portfolios, { selectedPortfolio = it; showPortfolioDialog = true }, { itemToDelete = it; showDeleteConfirm = true })

                    // PERBAIKAN DI SINI:
                    "Orders" -> OrderManagementList(
                        orders = orders,
                        onOrderDetailClick = { order ->
                            // Navigasi ke detail order admin
                            navController.navigate("admin_order_detail/${order.idOrders}")
                        }
                    )
                }
                }
            }
        }
    }

// --- KOMPONEN UI PENDUKUNG ---

@Composable
fun AdminOverviewContent(serviceCount: Int, portfolioCount: Int, orderCount: Int, clientCount: Int) {
    Column {
        Text("Ringkasan Bisnis", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Orders", orderCount.toString(), Icons.Default.ShoppingCart, Modifier.weight(1f))
            StatCard("Services", serviceCount.toString(), Icons.Default.Settings, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Portfolios", portfolioCount.toString(), Icons.Default.Image, Modifier.weight(1f))
            StatCard(
                title = "Users/Clients",
                value = clientCount.toString(), // Sekarang angkanya dinamis
                icon = Icons.Default.Person,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null)
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ServiceManagementList(services: List<ServiceEntity>, onEdit: (ServiceEntity) -> Unit, onDelete: (ServiceEntity) -> Unit) {
    if (services.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Belum ada layanan.") }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(services) { service ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(service.nameServices, fontWeight = FontWeight.Bold)
                            Text("Rp ${service.priceStart}", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { onEdit(service) }) { Icon(Icons.Default.Edit, null, tint = Color.Blue) }
                        IconButton(onClick = { onDelete(service) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderManagementList(
    orders: List<OrderEntity>,
    onOrderDetailClick: (OrderEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }
    val statusOptions = listOf("All", "pending", "approved", "survey_scheduled", "in_design", "completed", "rejected")

    Column {
        // Filter Row
        Row(Modifier
            .fillMaxWidth()
            .padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari Client/Lokasi") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
        }

        // Chip Filter Status
        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(statusOptions) { status ->
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { selectedStatus = status },
                    label = { Text(status) },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }

        LazyColumn {
            val filtered = orders.filter {
                (selectedStatus == "All" || it.status == selectedStatus) &&
                        (it.locationAddress.contains(searchQuery, true))
            }
            items(filtered) { order ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable { onOrderDetailClick(order) }
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Order #${order.idOrders}", fontWeight = FontWeight.Bold)
                            Text("Status: ${order.status.uppercase()}", color = MaterialTheme.colorScheme.primary)
                            Text("Lokasi: ${order.locationAddress}", style = MaterialTheme.typography.bodySmall)
                        }
                        Icon(Icons.Default.ChevronRight, null)
                    }
                }
            }
        }
    }
}


@Composable
fun PortfolioManagementList(portfolios: List<PortfolioEntity>, onEdit: (PortfolioEntity) -> Unit, onDelete: (PortfolioEntity) -> Unit) {
    if (portfolios.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Belum ada portfolio.") }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(portfolios) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.Bold)
                            Text("${item.category} • ${item.year}", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { onEdit(item) }) { Icon(Icons.Default.Edit, null, tint = Color.Blue) }
                        IconButton(onClick = { onDelete(item) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                    }
                }
            }
        }
    }
}

// --- DIALOGS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceFormDialog(service: ServiceEntity?,
                      onDismiss: () -> Unit,
                      onConfirm: (ServiceEntity) -> Unit
) {
    var name by remember { mutableStateOf(service?.nameServices ?: "") }
    var category by remember { mutableStateOf(service?.category ?: "residential") }
    var desc by remember { mutableStateOf(service?.description ?: "") }
    var price by remember { mutableStateOf(service?.priceStart?.toLong()?.toString() ?: "") }
    var duration by remember { mutableStateOf(service?.durationEstimate ?: "") }
    var features by remember { mutableStateOf(service?.features ?: "") }

    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("residential", "commercial", "interior", "renovation")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (service == null) "Tambah Layanan" else "Edit Layanan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(
                rememberScrollState()
            )) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Layanan") }, modifier = Modifier.fillMaxWidth())

                // Dropdown Kategori Jasa
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = category, onValueChange = {}, readOnly = true,
                        label = { Text("Kategori Jasa") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; expanded = false })
                        }
                    }
                }

                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Harga Mulai") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Estimasi Durasi") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = features, onValueChange = { features = it }, label = { Text("Fitur Layanan (Gunakan Koma)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(ServiceEntity(
                    idServices = service?.idServices ?:0,
                    idAdmin = 1,
                    nameServices = name,
                    // PERBAIKAN: Pastikan variabel 'category' dari state dropdown yang dikirim
                    category = category.trim(),
                    description = desc,
                    priceStart = price.toDoubleOrNull() ?: 0.0,
                    durationEstimate = duration,
                    features = features
                ))
            }) { Text("Simpan") }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioFormDialog(
    portfolio: PortfolioEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf(portfolio?.title ?: "") }
    var desc by remember { mutableStateOf(portfolio?.description ?: "") }
    var year by remember { mutableStateOf(portfolio?.year?.toString() ?: "2024") }
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("residential", "commercial", "renovation", "interior")
    var selectedCategory by remember { mutableStateOf(portfolio?.category ?: categories[0]) }

    val isTitleValid = isTextInputValid(title)
    val isYearValid = year.length == 4 && year.all { it.isDigit() } && (year.toIntOrNull() in 1900..2100)
    val isFormValid = isTitleValid && isYearValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (portfolio == null) "Tambah Portfolio" else "Edit Portfolio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Proyek") },
                    isError = !isTitleValid && title.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Deskripsi Proyek") },
                    minLines = 3 // Agar kolomnya lebih luas
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) year = it },
                    label = { Text("Tahun Proyek (YYYY)") },
                    isError = !isYearValid && year.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = { selectedCategory = item; expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, desc, selectedCategory, year.toIntOrNull() ?: 0) },
                enabled = isFormValid
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
