//package com.example.nafld_app
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//object MedicineManager {
//
//    suspend fun addMedicineEntry(db: AppDatabase, entry: MedicineEntry) {
//        withContext(Dispatchers.IO) {
//            db.medicineEntryDao().insertMedicineEntry(entry)
//        }
//    }
//
//    suspend fun deleteMedicineEntry(db: AppDatabase, entry: MedicineEntry) {
//        withContext(Dispatchers.IO) {
//            db.medicineEntryDao().deleteMedicineEntry(entry)
//        }
//    }
//
//    suspend fun getMedicineEntriesForPatient(db: AppDatabase, patientId: Int): List<MedicineEntry> {
//        return withContext(Dispatchers.IO) {
//            db.medicineEntryDao().getMedicineEntriesForPatient(patientId)
//        }
//    }
//}
package com.example.nafld_app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MedicineManager {

    suspend fun addMedicineEntry(db: AppDatabase, entry: MedicineEntry) {
        withContext(Dispatchers.IO) {
            db.medicineEntryDao().insertMedicineEntry(entry)
        }
    }

    suspend fun deleteMedicineEntry(db: AppDatabase, entry: MedicineEntry) {
        withContext(Dispatchers.IO) {
            db.medicineEntryDao().deleteMedicineEntry(entry)
        }
    }

    suspend fun getMedicineEntry(db: AppDatabase): List<MedicineEntry> {
        return withContext(Dispatchers.IO) {
            db.medicineEntryDao().getAllMedicineEntries()
        }
    }
}
