//package com.example.nafld_app
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//
//@Dao
//interface ExerciseLogDao {
//
//    @Query("SELECT * FROM exercise_logs WHERE patientId = :patientId")
//    suspend fun getExerciseLogsForPatient(patientId: Int): List<ExerciseLog>
//
//    @Query("SELECT * FROM exercise_logs")
//    suspend fun getAllExerciseLogs(): List<ExerciseLog>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertExerciseLog(exerciseLog: ExerciseLog): Long
//
//    @Query("DELETE FROM exercise_logs WHERE date = :date AND patientId = :patientId")
//    suspend fun deleteAllExercisesForDateAndPatient(date: String, patientId: Int): Int
//
//    @Query("SELECT * FROM exercise_logs WHERE date = :date AND patientId = :patientId")
//    suspend fun getExerciseLogsForDateAndPatient(date: String, patientId: Int): List<ExerciseLog>
//
//    @Delete
//    suspend fun deleteExerciseLog(exerciseLog: ExerciseLog)
//
//}
//
package com.example.nafld_app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExerciseLogDao {

    @Query("SELECT * FROM exercise_logs WHERE date = :date")
    suspend fun getExerciseLogsForDate(date: String): List<ExerciseLog>

    @Query("SELECT * FROM exercise_logs")
    suspend fun getAllExerciseLogs(): List<ExerciseLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(exerciseLog: ExerciseLog): Long

    @Delete
    suspend fun deleteExerciseLog(exerciseLog: ExerciseLog)
}
