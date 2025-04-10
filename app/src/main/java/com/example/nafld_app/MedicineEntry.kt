//package com.example.nafld_app
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.Index
//import androidx.room.PrimaryKey
//
//@Entity(
//    tableName = "medicine_entries",
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
//data class MedicineEntry(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val startDate: String,
//    val endDate: String,
//    val medicineName: String,
//    val dose: String,
//    val notes: String,
//    val patientId: Int
//)
//package com.example.nafld_app
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.Index
//import androidx.room.PrimaryKey
//
//@Entity(
//    tableName = "medicine_entries",
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
//data class MedicineEntry(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val startDate: String,
//    val endDate: String,
//    val medicineName: String,
//    val dose: String,
//    val notes: String,
//    val patientId: Int
//)
package com.example.nafld_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_entries")
data class MedicineEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: String,
    val endDate: String,
    val medicineName: String,
    val dose: String,
    val notes: String
)
