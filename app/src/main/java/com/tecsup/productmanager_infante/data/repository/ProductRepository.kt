package com.tecsup.productmanager_infante.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.tecsup.productmanager_infante.data.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")

    // Crear producto
    suspend fun createProduct(product: Product): Result<String> {
        return try {
            val docRef = productsCollection.add(product.toMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener productos del usuario en tiempo real
    fun getUserProducts(userId: String): Flow<List<Product>> = callbackFlow {
        val listener: ListenerRegistration = productsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(products)
            }

        awaitClose { listener.remove() }
    }

    // Actualizar producto
    suspend fun updateProduct(productId: String, product: Product): Result<Unit> {
        return try {
            productsCollection.document(productId).update(product.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar producto
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}