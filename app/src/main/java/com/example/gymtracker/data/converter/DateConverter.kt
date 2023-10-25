package com.example.gymtracker.data.converter


import androidx.room.TypeConverter
import java.time.LocalDate

class DateConverter {

    @TypeConverter
    fun toLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String{
        return date.toString()
    }
}