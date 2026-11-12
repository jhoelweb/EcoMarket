
package com.example.ecomarket.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomarket.data.local.AppDatabase
import com.example.ecomarket.data.repository.ProductRepository
import com.example.ecomarket.model.FavoriteProductEntity
import com.example.ecomarket.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository()

    private val favoriteDao =
        AppDatabase.getDatabase(application).favoriteDao()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Lista de productos favoritos guardados en Room
    val favorites: Flow<List<FavoriteProductEntity>> =
        favoriteDao.getAllFavorites()

    // Cargar productos desde DummyJSON
    fun loadProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                _products.value = repository.getProducts()

            } catch (e: Exception) {
                _error.value =
                    "Error al cargar los productos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Guardar un producto en favoritos
    fun addFavorite(product: Product) {
        viewModelScope.launch {
            favoriteDao.insertFavorite(
                FavoriteProductEntity(
                    id = product.id,
                    title = product.title,
                    description = product.description,
                    category = product.category,
                    price = product.price,
                    thumbnail = product.thumbnail,
                    rating = product.rating,
                    stock = product.stock
                )
            )
        }
    }

    // Eliminar un producto de favoritos
    fun removeFavorite(productId: Int) {
        viewModelScope.launch {
            favoriteDao.deleteFavorite(productId)
        }
    }

    // Consultar si un producto es favorito
    fun isFavorite(productId: Int): Flow<Boolean> {
        return favoriteDao.isFavorite(productId)
    }
}
