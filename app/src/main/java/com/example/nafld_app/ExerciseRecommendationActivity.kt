package com.example.nafld_app

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nafld_app.databinding.ActivityExerciseRecommendationBinding
import java.util.Locale

class ExerciseRecommendationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExerciseRecommendationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the language based on the global setting
        setAppLocale(AppSettings.currentLanguage)

        binding = ActivityExerciseRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Set up gender dropdown
        val genders = listOf(getString(R.string.male), getString(R.string.female))
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)
        binding.genderSpinner.adapter = genderAdapter

        // Set up comorbidity dropdown
        val healthConditions = listOf(
            getString(R.string.none),
            getString(R.string.diabetes),
            getString(R.string.hypertension),
            getString(R.string.joint_issues)
        )
        val healthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, healthConditions)
        binding.healthConditionSpinner.adapter = healthAdapter

        // Set up activity level switch listener
        binding.activityLevelSwitch.setOnCheckedChangeListener { _, _ -> }

        // Set up submit button
        binding.submitButton.setOnClickListener {
            displayExerciseRecommendations()
        }

        // Set up close button
        binding.closeButton.setOnClickListener {
            finish() // Close the activity
        }
    }

    private fun displayExerciseRecommendations() {
        // Retrieve and validate inputs
        val ageString = binding.ageInput.text.toString()
        val age = ageString.toIntOrNull()
        val gender = binding.genderSpinner.selectedItem?.toString()
        val healthCondition = binding.healthConditionSpinner.selectedItem?.toString()

        // Check if age, gender, and health condition are all filled
        if (age == null || age <= 0) {
            showErrorPopup(getString(R.string.error_fill_all_fields))
            return
        }

        if (gender.isNullOrEmpty()) {
            showErrorPopup(getString(R.string.error_fill_all_fields))
            return
        }

        if (healthCondition.isNullOrEmpty()) {
            showErrorPopup(getString(R.string.error_fill_all_fields))
            return
        }

        // Check if the activity level switch is checked
        val highActivityLevel = binding.activityLevelSwitch.isChecked

        // Proceed with generating and displaying recommendations if all fields are valid
        val exerciseInfo = generateExerciseRecommendations(age, gender, healthCondition, highActivityLevel)
        binding.exerciseRecommendationText.visibility = View.VISIBLE
        binding.exerciseRecommendationText.text = Html.fromHtml(exerciseInfo)
    }

    private fun showErrorPopup(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun generateExerciseRecommendations(age: Int, gender: String, healthCondition: String, highActivityLevel: Boolean): String {
        val baseInfo = getString(R.string.base_info).replace(".", ".<br>")
        val additionalInfo = if (highActivityLevel) {
            getString(R.string.additional_info_high).replace(".", ".<br>")
        } else {
            getString(R.string.additional_info_moderate).replace(".", ".<br>")
        }

        val specificInfo = when (healthCondition) {
            getString(R.string.diabetes) -> getString(R.string.diabetes_info).replace(".", ".<br>")
            getString(R.string.hypertension) -> getString(R.string.hypertension_info).replace(".", ".<br>")
            getString(R.string.joint_issues) -> getString(R.string.joint_issues_info).replace(".", ".<br>")
            else -> ""
        }

        // Format the text to start each recommendation on a new line with bullet points
        return buildString {
            if (baseInfo.isNotEmpty()) {
                append(baseInfo)
                if (additionalInfo.isNotEmpty() || specificInfo.isNotEmpty()) append("")
            }
            if (additionalInfo.isNotEmpty()) {
                append(additionalInfo)
                if (specificInfo.isNotEmpty()) append("<br>")
            }
        }.trim()
    }


    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = android.content.res.Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
