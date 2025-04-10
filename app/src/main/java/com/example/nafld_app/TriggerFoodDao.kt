package com.example.nafld_app

import androidx.room.*

@Dao
interface TriggerFoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTriggerFoodEntry(entry: TriggerFoodEntry)

    @Delete
    suspend fun deleteTriggerFoodEntry(entry: TriggerFoodEntry)

    @Query("SELECT * FROM trigger_food_entries")
    suspend fun getAllTriggerFoodEntries(): List<TriggerFoodEntry>
}
