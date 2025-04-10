package com.example.nafld_app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.util.Log

@Database(
    entities = [MedicineEntry::class, Patient::class, ExerciseLog::class, TriggerFoodEntry::class],
    version = 6, // Updated to version 3
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicineEntryDao(): MedicineEntryDao
    abstract fun patientDao(): PatientDao
    abstract fun exerciseLogDao(): ExerciseLogDao
    abstract fun triggerFoodDao(): TriggerFoodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nafld_app_database"
                )
                    .setJournalMode(JournalMode.TRUNCATE)
                    .setQueryCallback({ sqlQuery, bindArgs ->
                        Log.d("ROOM_QUERY", "SQL Query: $sqlQuery SQL Args: $bindArgs")
                    }, { Log.d("ROOM_QUERY", "Query executed") })
                    .fallbackToDestructiveMigration() // Clears the database if the schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
