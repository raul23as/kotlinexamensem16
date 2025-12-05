package com.tecsup.productmanager_infante.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.productmanager_infante.data.model.Product
import com.tecsup.productmanager_infante.ui.viewmodel.ProductState
import com.tecsup.productmanager_infante.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    product: Product?,
    onNavigateBack: () -> Unit,
    viewModel: ProductViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf(product?.nombre ?: "") }
    var precio by remember { mutableStateOf(product?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    var categoria by remember { mutableStateOf(product?.categoria ?: "") }

    val productState by viewModel.productState.collectAsState()
    val isEditMode = product != null

    LaunchedEffect(productState) {
        when (productState) {
            is ProductState.Success -> {
                onNavigateBack()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Producto" else "Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (productState is ProductState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (isEditMode) {
                            viewModel.updateProduct(
                                product!!.id,
                                nombre,
                                precio,
                                stock,
                                categoria
                            )
                        } else {
                            viewModel.createProduct(nombre, precio, stock, categoria)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditMode) "Actualizar" else "Guardar")
                }
            }

            if (productState is ProductState.Error) {
                Text(
                    text = (productState as ProductState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}