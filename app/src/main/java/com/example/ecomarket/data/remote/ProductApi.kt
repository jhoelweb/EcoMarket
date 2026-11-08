
package com.example.ecomarket.data.remote

import com.example.ecomarket.model.ProductResponse
import retrofit2.http.GET

interface ProductApi {

    @GET("products")
    suspend fun getProducts(): ProductResponse
}


