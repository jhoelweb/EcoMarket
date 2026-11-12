
package com.example.ecomarket.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val thumbnail: String,
    val rating: Double,
    val stock: Int
)
