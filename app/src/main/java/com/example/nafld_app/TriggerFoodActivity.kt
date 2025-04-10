package com.example.nafld_app

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TriggerFoodActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var dateInput: EditText
    private lateinit var foodInput: EditText
    private lateinit var symptomsInput: AutoCompleteTextView
    private lateinit var infoInput: EditText
    private lateinit var triggerFoodListLayout: LinearLayout

    private val previousSymptoms = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the language based on the global setting BEFORE setting the content view
        setAppLocale(AppSettings.currentLanguage)

        setContentView(R.layout.activity_trigger_food)

        // Initialize views
        dateInput = findViewById(R.id.dateInput)
        foodInput = findViewById(R.id.foodInput)
        symptomsInput = findViewById(R.id.symptomsInput)
        infoInput = findViewById(R.id.infoInput)
        val addTriggerFoodButton = findViewById<Button>(R.id.addTriggerFoodButton)
        val closeTriggerFoodButton = findViewById<Button>(R.id.closeTriggerFoodButton)
        triggerFoodListLayout = findViewById(R.id.triggerFoodListLayout)

        db = AppDatabase.getDatabase(this)

        setupAutoCompleteSymptoms()

        addTriggerFoodButton.setOnClickListener {
            addTriggerFoodEntry()
        }

        closeTriggerFoodButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish() // Finish this activity
        }

        dateInput.setOnClickListener {
            showDatePickerDialog { date ->
                dateInput.setText(date)
            }
        }

        displayTriggerFoodEntries()
    }


    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = android.content.res.Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupAutoCompleteSymptoms() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, previousSymptoms)
        symptomsInput.setAdapter(adapter)
    }

    private fun addTriggerFoodEntry() {
        val date = dateInput.text.toString()
        val food = foodInput.text.toString()
        val symptoms = symptomsInput.text.toString().lowercase(Locale.getDefault())
        val info = infoInput.text.toString()

        if (date.isEmpty() || food.isEmpty() || symptoms.isEmpty()) {
            showErrorPopup(getString(R.string.error_fill_all_fields))
            return
        }

        if (!previousSymptoms.contains(symptoms)) {
            previousSymptoms.add(symptoms)
            savePreviousSymptoms()
        }

        val newEntry = TriggerFoodEntry(date = date, food = food, symptoms = symptoms, info = info)

        lifecycleScope.launch {
            TriggerFoodManager.addTriggerFoodEntry(db, newEntry)
            displayTriggerFoodEntries()
            clearInputFields()
            showSuccessPopup(getString(R.string.entry_saved))
        }
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                onDateSelected(dateFormatter.format(selectedDate.time))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun showErrorPopup(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSuccessPopup(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.success))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun displayTriggerFoodEntries() {
        lifecycleScope.launch {
            try {
                val triggerFoodEntries = TriggerFoodManager.getTriggerFoodEntries(db)
                triggerFoodListLayout.removeAllViews()

                if (triggerFoodEntries.isEmpty()) {
                    showToast(getString(R.string.no_trigger_food_entries))
                } else {
                    triggerFoodEntries.forEach { entry ->
                        val entryView = createTriggerFoodEntryView(entry)
                        triggerFoodListLayout.addView(entryView)
                    }
                }
            } catch (e: Exception) {
                Log.e("TriggerFoodActivity", "Error displaying trigger food entries: ${e.message}", e)
                showErrorPopup("An error occurred while fetching the data: ${e.message}")
            }

        }
    }

    private fun createTriggerFoodEntryView(entry: TriggerFoodEntry): View {
        val entryView = layoutInflater.inflate(R.layout.item_trigger_food_entry, null)

        // Input date format (assuming the date is stored in "dd/MM/yyyy" format)
        val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Output date format (desired format "dd MMMM yyyy")
        val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        // Parse and format the date
        val parsedDate = inputDateFormat.parse(entry.date)
        val formattedDate = outputDateFormat.format(parsedDate!!)

        // Set the formatted date
        entryView.findViewById<TextView>(R.id.triggerFoodDate).text = formattedDate
        entryView.findViewById<TextView>(R.id.triggerFoodItem).text = entry.food

        entryView.findViewById<Button>(R.id.deleteTriggerFoodButton).setOnClickListener {
            lifecycleScope.launch {
                TriggerFoodManager.deleteTriggerFoodEntry(db, entry)
                displayTriggerFoodEntries()
            }
        }

        return entryView
    }

    private fun clearInputFields() {
        dateInput.text.clear()
        foodInput.text.clear()
        symptomsInput.text.clear()
        infoInput.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun savePreviousSymptoms() {
        val sharedPreferences = getSharedPreferences("TriggerFoodPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("previousSymptoms", previousSymptoms.toSet())
        editor.apply()
    }
}
