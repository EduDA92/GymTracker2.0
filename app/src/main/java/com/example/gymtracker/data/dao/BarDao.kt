package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.gymtracker.data.model.BarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BarDao {

    @Query("SELECT * FROM bar")
    fun observeBars(): Flow<List<BarEntity>>

    @Upsert
    suspend fun upsertBar(bar: BarEntity): Long

    @Query("DELETE FROM bar WHERE id = :barId")
    suspend fun deleteBar(barId: Long)

    @Query("UPDATE bar SET isSelected = :isBarSelected WHERE id = :barId")
    suspend fun updateBarIsSelected(barId: Long, isBarSelected: Boolean)

}