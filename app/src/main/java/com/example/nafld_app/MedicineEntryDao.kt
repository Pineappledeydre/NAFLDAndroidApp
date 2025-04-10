//package com.example.nafld_app
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface MedicineEntryDao {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertMedicineEntry(medicineEntry: MedicineEntry)
//
//    @Delete
//    suspend fun deleteMedicineEntry(medicineEntry: MedicineEntry)
//
//    @Query("SELECT * FROM medicine_entries WHERE patientId = :patientId")
//    suspend fun getMedicineEntriesForPatient(patientId: Int): List<MedicineEntry>
//}
package com.example.nafld_app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MedicineEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicineEntry(medicineEntry: MedicineEntry)

    @Delete
    suspend fun deleteMedicineEntry(medicineEntry: MedicineEntry)

    @Query("SELECT * FROM medicine_entries")
    suspend fun getAllMedicineEntries(): List<MedicineEntry>
}
