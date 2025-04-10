//package com.example.nafld_app
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.Index
//import androidx.room.PrimaryKey
//
//@Entity(
//    tableName = "exercise_logs",
//    foreignKeys = [
//        ForeignKey(
//            entity = Patient::class,
//            parentColumns = ["id"],
//            childColumns = ["patientId"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
//    indices = [Index(value = ["patientId"])]
//)
//data class ExerciseLog(
//    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    val date: String,
//    val details: String,
//    val patientId: Int
//)
//
package com.example.nafld_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_logs")
data class ExerciseLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val details: String
)
