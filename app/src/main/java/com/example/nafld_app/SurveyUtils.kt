package com.example.nafld_app

import android.content.Context
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.core.view.setPadding

fun showPatientSurveyWindow(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("PatientSurvey", Context.MODE_PRIVATE)
    val surveyLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(20)
    }

    val questions = mapOf(
        "first_symptoms_date" to context.getString(R.string.first_symptoms_date),
        "main_symptoms" to context.getString(R.string.main_symptoms),
        "symptom_frequency" to context.getString(R.string.symptom_frequency),
        "diagnosed_date" to context.getString(R.string.diagnosed_date),
        "liver_diseases" to context.getString(R.string.liver_diseases),
        "chronic_diseases" to context.getString(R.string.chronic_diseases),
        "medications" to context.getString(R.string.medications),
        "alcohol_consumption" to context.getString(R.string.alcohol_consumption),
        "diet" to context.getString(R.string.diet_ques),
        "physical_activity" to context.getString(R.string.physical_activity),
        "family_history" to context.getString(R.string.family_history),
        "stress_level" to context.getString(R.string.stress_level),
        "sleep_regimen" to context.getString(R.string.sleep_regimen),
        "tests" to context.getString(R.string.tests),
        "alternative_treatments" to context.getString(R.string.alternative_treatments)
    )

    val entries = mutableMapOf<String, EditText>()

    questions.forEach { (key, question) ->
        val textView = TextView(context).apply {
            text = question
            setPadding(0, 10, 0, 5)
        }
        val editText = EditText(context).apply {
            setText(sharedPreferences.getString(key, ""))
        }
        entries[key] = editText
        surveyLayout.addView(textView)
        surveyLayout.addView(editText)
    }

    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.patient_survey))
        .setView(surveyLayout)
        .setPositiveButton(context.getString(R.string.save)) { _, _ ->
            entries.forEach { (key, editText) ->
                sharedPreferences.edit {
                    putString(key, editText.text.toString())
                }
            }
            Toast.makeText(context, context.getString(R.string.survey_saved), Toast.LENGTH_SHORT).show()
        }
        .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        .show()
}
