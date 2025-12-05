package com.tecsup.productmanager_infante.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.productmanager_infante.data.model.Product
import com.tecsup.productmanager_infante.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onNavigateToForm: (Product?) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Productos") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm(null) }) {
                Icon(Icons.Default.Add, "Agregar Producto")
            }
        }
    ) { padding ->
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay productos. Presiona + para agregar uno.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onEdit = { onNavigateToForm(product) },
                        onDelete = { showDeleteDialog = product }
                    )
                }
            }
        }
    }

    showDeleteDialog?.let { product ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Producto") },
            text = { Text("¿Estás seguro de eliminar ${product.nombre}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProduct(product.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ProductItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Precio: S/ ${product.precio}")
                Text("Stock: ${product.stock}")
                Text("Categoría: ${product.categoria}")
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}