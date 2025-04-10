package com.example.nafld_app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class CalorieCalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the language based on the global setting before setting the content view
        setAppLocale(AppSettings.currentLanguage)

        setContentView(R.layout.activity_calorie_calculator)

        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val activityLevelSpinner = findViewById<Spinner>(R.id.activityLevelSpinner)
        val goalSpinner = findViewById<Spinner>(R.id.goalSpinner)
        val calculateButton = findViewById<Button>(R.id.calculateButton)

        // Set up gender dropdown
        val genders = listOf(getString(R.string.male), getString(R.string.female))
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)
        genderSpinner.adapter = genderAdapter

        // Set up activity level dropdown
        val activityLevels = listOf(
            getString(R.string.sedentary),
            getString(R.string.lightly_active),
            getString(R.string.moderately_active),
            getString(R.string.very_active),
            getString(R.string.extra_active)
        )
        val activityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, activityLevels)
        activityLevelSpinner.adapter = activityAdapter

        // Set up goal dropdown
        val goals = listOf(
            getString(R.string.maintenance),
            getString(R.string.weight_loss),
            getString(R.string.weight_gain)
        )
        val goalAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, goals)
        goalSpinner.adapter = goalAdapter

        // Set up calculate button
        calculateButton.setOnClickListener {
            val heightString = findViewById<EditText>(R.id.heightInput).text.toString()
            val weightString = findViewById<EditText>(R.id.weightInput).text.toString()
            val ageString = findViewById<EditText>(R.id.ageInput).text.toString()

            val height = heightString.toIntOrNull()
            val weight = weightString.toIntOrNull()
            val age = ageString.toIntOrNull()

            val gender = genderSpinner.selectedItem?.toString()
            val activityLevel = activityLevelSpinner.selectedItem?.toString()
            val goal = goalSpinner.selectedItem?.toString()

            // Validation checks for missing or invalid inputs
            if (height == null || height <= 0) {
                showErrorPopup(getString(R.string.error_fill_all_fields))
                return@setOnClickListener
            }
            if (weight == null || weight <= 0) {
                showErrorPopup(getString(R.string.error_fill_all_fields))
                return@setOnClickListener
            }
            if (age == null || age <= 0) {
                showErrorPopup(getString(R.string.error_fill_all_fields))
                return@setOnClickListener
            }
            if (gender.isNullOrEmpty()) {
                showErrorPopup(getString(R.string.error_fill_all_fields))
                return@setOnClickListener
            }
            if (activityLevel.isNullOrEmpty()) {
                showErrorPopup(getString(R.string.error_fill_all_fields))
                return@setOnClickListener
            }
            if (goal.isNullOrEmpty()) {
                showErrorPopup(getString(R.string.error_fill_all_fields))
                return@setOnClickListener
            }

            // Calculate caloric needs and BMI only if all fields are valid
            val caloricNeeds = calculateCaloricNeeds(height, weight, age, gender, activityLevel, goal)
            val bmi = calculateBMI(height, weight)

            // Pass the results to the DietRecommendationsActivity
            val intent = Intent(this, DietRecommendationsActivity::class.java).apply {
                putExtra("CALORIC_NEEDS", caloricNeeds)
                putExtra("BMI", bmi)
            }
            startActivity(intent)
        }
        // Close button action
        findViewById<Button>(R.id.closeButton).setOnClickListener {
            finish()
        }
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun showErrorPopup(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun calculateCaloricNeeds(height: Int, weight: Int, age: Int, gender: String, activityLevel: String, goal: String): Int {
        // Placeholder formula for caloric needs calculation
        val bmr = 10 * weight + 6.25 * height - 5 * age + if (gender == getString(R.string.male)) 5 else -161
        val activityMultiplier = when (activityLevel) {
            getString(R.string.sedentary) -> 1.2
            getString(R.string.lightly_active) -> 1.375
            getString(R.string.moderately_active) -> 1.55
            getString(R.string.very_active) -> 1.725
            else -> 1.9
        }
        val goalAdjustment = when (goal) {
            getString(R.string.weight_loss) -> -500
            getString(R.string.weight_gain) -> 500
            else -> 0
        }
        return (bmr * activityMultiplier).toInt() + goalAdjustment
    }

    private fun calculateBMI(height: Int, weight: Int): Float {
        // BMI calculation: weight(kg) / height(m)^2
        return weight / ((height / 100f) * (height / 100f))
    }
}
