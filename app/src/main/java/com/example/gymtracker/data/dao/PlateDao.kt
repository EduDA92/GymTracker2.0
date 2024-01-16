package com.example.gymtracker.data.dao

import androidx.compose.ui.text.font.FontWeight
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.gymtracker.data.model.PlateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlateDao {

    @Query("SELECT * FROM plate")
    fun observePlates(): Flow<List<PlateEntity>>

    @Upsert
    suspend fun upsertPlate(plate: PlateEntity): Long

    @Query("DELETE FROM plate WHERE id = :plateId")
    suspend fun deletePlate(plateId: Long)

    @Query("UPDATE plate SET isSelected = :isPlateSelected WHERE id = :plateId")
    suspend fun updatePlateIsSelected(plateId: Long, isPlateSelected: Boolean)

    @Query("SELECT EXISTS(SELECT * FROM plate WHERE weight = :weight)")
    suspend fun checkIfPlateWeightExist(weight: Float): Boolean

}