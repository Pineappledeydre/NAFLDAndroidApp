package com.example.nafld_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient): Long  // Returns the generated ID

    @Query("SELECT * FROM patients WHERE id = :patientId")
    suspend fun getPatientById(patientId: Int): Patient?

    @Query("SELECT * FROM patients WHERE name = :name LIMIT 1")
    suspend fun getPatientByName(name: String): Patient?

    @Query("SELECT * FROM patients")
    suspend fun getAllPatients(): List<Patient>
}
