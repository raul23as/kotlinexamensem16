package com.tecsup.productmanager_infante.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productmanager_infante.data.model.Product
import com.tecsup.productmanager_infante.data.repository.AuthRepository
import com.tecsup.productmanager_infante.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val authRepository = AuthRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _productState = MutableStateFlow<ProductState>(ProductState.Idle)
    val productState: StateFlow<ProductState> = _productState

    init {
        loadProducts()
    }

    private fun loadProducts() {
        val userId = authRepository.getCurrentUser()?.uid ?: return

        viewModelScope.launch {
            productRepository.getUserProducts(userId).collect { productList ->
                _products.value = productList
            }
        }
    }

    fun createProduct(nombre: String, precio: String, stock: String, categoria: String) {
        if (nombre.isBlank()) {
            _productState.value = ProductState.Error("El nombre es obligatorio")
            return
        }

        val userId = authRepository.getCurrentUser()?.uid ?: return

        val product = Product(
            nombre = nombre,
            precio = precio.toDoubleOrNull() ?: 0.0,
            stock = stock.toIntOrNull() ?: 0,
            categoria = categoria,
            userId = userId
        )

        viewModelScope.launch {
            _productState.value = ProductState.Loading
            val result = productRepository.createProduct(product)

            _productState.value = if (result.isSuccess) {
                ProductState.Success("Producto creado exitosamente")
            } else {
                ProductState.Error(result.exceptionOrNull()?.message ?: "Error al crear producto")
            }
        }
    }

    fun updateProduct(productId: String, nombre: String, precio: String, stock: String, categoria: String) {
        if (nombre.isBlank()) {
            _productState.value = ProductState.Error("El nombre es obligatorio")
            return
        }

        val userId = authRepository.getCurrentUser()?.uid ?: return

        val product = Product(
            id = productId,
            nombre = nombre,
            precio = precio.toDoubleOrNull() ?: 0.0,
            stock = stock.toIntOrNull() ?: 0,
            categoria = categoria,
            userId = userId
        )

        viewModelScope.launch {
            _productState.value = ProductState.Loading
            val result = productRepository.updateProduct(productId, product)

            _productState.value = if (result.isSuccess) {
                ProductState.Success("Producto actualizado exitosamente")
            } else {
                ProductState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _productState.value = ProductState.Loading
            val result = productRepository.deleteProduct(productId)

            _productState.value = if (result.isSuccess) {
                ProductState.Success("Producto eliminado exitosamente")
            } else {
                ProductState.Error(result.exceptionOrNull()?.message ?: "Error al eliminar")
            }
        }
    }

    fun resetState() {
        _productState.value = ProductState.Idle
    }
}

sealed class ProductState {
    object Idle : ProductState()
    object Loading : ProductState()
    data class Success(val message: String) : ProductState()
    data class Error(val message: String) : ProductState()
}