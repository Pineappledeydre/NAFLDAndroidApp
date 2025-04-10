package com.example.nafld_app

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Data Classes for JSON Handling
data class Prediction(
    val date: String,
    val probability: Double
)

data class Medicine(
    val medicine: String,
    val dose: String,
    val startDate: String,
    val endDate: String
)

data class TriggerFood(
    val date: String,
    val food: String,
    val symptoms: String,
    val info: String
)

data class HealthData(
    val predictions: List<Prediction>,
    val medicines: List<Medicine>,
    val triggerFoods: List<TriggerFood>
)

// JSON Parsing Implementation
fun parseHealthData(json: String): HealthData {
    val gson = Gson()
    val type = object : TypeToken<HealthData>() {}.type
    return gson.fromJson(json, type)
}
