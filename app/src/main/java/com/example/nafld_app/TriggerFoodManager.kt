package com.example.nafld_app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TriggerFoodManager {

    suspend fun addTriggerFoodEntry(db: AppDatabase, entry: TriggerFoodEntry) {
        withContext(Dispatchers.IO) {
            db.triggerFoodDao().insertTriggerFoodEntry(entry)
        }
    }

    suspend fun deleteTriggerFoodEntry(db: AppDatabase, entry: TriggerFoodEntry) {
        withContext(Dispatchers.IO) {
            db.triggerFoodDao().deleteTriggerFoodEntry(entry)
        }
    }

    suspend fun getTriggerFoodEntries(db: AppDatabase): List<TriggerFoodEntry> {
        return withContext(Dispatchers.IO) {
            db.triggerFoodDao().getAllTriggerFoodEntries()
        }
    }
}
