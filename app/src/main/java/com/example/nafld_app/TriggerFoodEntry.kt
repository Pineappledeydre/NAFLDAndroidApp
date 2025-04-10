package com.example.nafld_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trigger_food_entries")
data class TriggerFoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val food: String,
    val symptoms: String,
    val info: String
)
