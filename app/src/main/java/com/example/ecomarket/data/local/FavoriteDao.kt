
package com.example.ecomarket.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecomarket.model.FavoriteProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(product: FavoriteProductEntity)

    @Query("SELECT * FROM favorite_products ORDER BY title ASC")
    fun getAllFavorites(): Flow<List<FavoriteProductEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE id = :productId)")
    fun isFavorite(productId: Int): Flow<Boolean>

    @Query("DELETE FROM favorite_products WHERE id = :productId")
    suspend fun deleteFavorite(productId: Int)

    @Query("DELETE FROM favorite_products")
    suspend fun deleteAllFavorites()
}
