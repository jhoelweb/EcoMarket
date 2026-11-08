
package com.example.ecomarket.data.repository

import com.example.ecomarket.data.remote.RetrofitInstance
import com.example.ecomarket.model.Product

class ProductRepository {

    suspend fun getProducts(): List<Product> {

        return RetrofitInstance.api
            .getProducts()
            .products
    }
}


