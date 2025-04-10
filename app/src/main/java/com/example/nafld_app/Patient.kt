package com.example.nafld_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generate ID by Room
    val name: String,
    val age: Int
)
