package com.example.nafld_app

data class PredictionEntry(
    val date: String,
    val gender: Int, // 0 for Female, 1 for Male
    val ast: Double,
    val ggtp: Double,
    val lpvp: Double,
    val trigl: Double,
    val bilirr: Double,
    val crb: Double,
    val probability: Double
)
