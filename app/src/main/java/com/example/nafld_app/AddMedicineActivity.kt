//package com.example.nafld_app
//
//import android.app.DatePickerDialog
//import android.content.Context
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.*
//
//class AddMedicineActivity : AppCompatActivity() {
//
//    private lateinit var db: AppDatabase
//    private var patientId: Int = 1 // Default to patient with ID 1, should be set dynamically
//
//    private lateinit var startDateInput: EditText
//    private lateinit var endDateInput: EditText
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setAppLocale(AppSettings.currentLanguage)
//        setContentView(R.layout.activity_add_medicine)
//
//        // Get patientId from SharedPreferences
//        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
//        patientId = sharedPreferences.getInt("patientId", 1)
//
//        db = AppDatabase.getDatabase(this)
//
//        startDateInput = findViewById(R.id.startDateInput)
//        endDateInput = findViewById(R.id.endDateInput)
//        val medicineNameInput = findViewById<EditText>(R.id.medicineNameInput)
//        val doseInput = findViewById<EditText>(R.id.doseInput)
//        val notesInput = findViewById<EditText>(R.id.notesInput)
//        val addMedicineButton = findViewById<Button>(R.id.addMedicineButton)
//        findViewById<LinearLayout>(R.id.medicineListLayout)
//        val closeButton = findViewById<Button>(R.id.closeAddMedicineButton)
//
//        // Set up the close button
//        closeButton.setOnClickListener {
//            finish()  // This will close the activity
//        }
//
//        // Show DatePickerDialog when clicking on startDateInput
//        startDateInput.setOnClickListener {
//            showDatePickerDialog { date ->
//                startDateInput.setText(date)
//            }
//        }
//
//        // Show DatePickerDialog when clicking on endDateInput
//        endDateInput.setOnClickListener {
//            showDatePickerDialog { date ->
//                endDateInput.setText(date)
//            }
//        }
//
//        // Add medicine entry on button click
//        addMedicineButton.setOnClickListener {
//            val startDate = startDateInput.text.toString()
//            val endDate = endDateInput.text.toString() // Optional
//            val medicineName = medicineNameInput.text.toString()
//            val dose = doseInput.text.toString()
//            val notes = notesInput.text.toString()
//
//            // Check if any required fields are empty (excluding endDate)
//            if (startDate.isEmpty() || medicineName.isEmpty() || dose.isEmpty()) {
//                // Show a unified error popup or a message
//                showErrorPopup(getString(R.string.error_fill_all_fields))
//                return@setOnClickListener // Exits the lambda expression, preventing further execution
//            }
//
//            // If validation passes, proceed with creating the medicine entry
//            val medicineEntry = MedicineEntry(
//                startDate = startDate,
//                endDate = if (endDate.isEmpty()) "" else endDate,
//                medicineName = medicineName,
//                dose = dose,
//                notes = notes,
//                patientId = patientId
//            )
//
//            lifecycleScope.launch {
//                // Insert the new medicine entry into the database
//                MedicineManager.addMedicineEntry(db, medicineEntry)
//                // Refresh the list of entries
//                displayMedicineEntries()
//                // Clear the input fields after saving
//                clearInputFields()
//                Toast.makeText(this@AddMedicineActivity, getString(R.string.entry_saved), Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Display existing medicine entries
//        displayMedicineEntries()
//    }
//
//    private fun clearInputFields() {
//        startDateInput.text.clear()
//        endDateInput.text.clear() // Leave empty if it's optional
//        findViewById<EditText>(R.id.medicineNameInput).text.clear()
//        findViewById<EditText>(R.id.doseInput).text.clear()
//        findViewById<EditText>(R.id.notesInput).text.clear()
//    }
//
//    private fun setAppLocale(languageCode: String) {
//        val locale = Locale(languageCode)
//        Locale.setDefault(locale)
//        val config = android.content.res.Configuration()
//        config.setLocale(locale)
//        resources.updateConfiguration(config, resources.displayMetrics)
//    }
//
//    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val datePickerDialog = DatePickerDialog(
//            this,
//            { _, selectedYear, selectedMonth, selectedDay ->
//                val selectedDate = Calendar.getInstance().apply {
//                    set(selectedYear, selectedMonth, selectedDay)
//                }
//                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                onDateSelected(dateFormatter.format(selectedDate.time))
//            },
//            year, month, day
//        )
//        datePickerDialog.show()
//    }
//
//    private fun showErrorPopup(message: String) {
//        AlertDialog.Builder(this) // Apply the custom style
//            .setTitle(getString(R.string.error)) // Set the title
//            .setMessage(message) // Set the message
//            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() } // Set button
//            .show()
//    }
//
//    private fun displayMedicineEntries() {
//        lifecycleScope.launch {
//            val medicineEntries = MedicineManager.getMedicineEntriesForPatient(db, patientId)
//            val medicineListLayout = findViewById<LinearLayout>(R.id.medicineListLayout)
//            medicineListLayout.removeAllViews()
//
//            for (entry in medicineEntries) {
//                val medicineView = layoutInflater.inflate(R.layout.item_medicine_entry, null)
//
//                val medicineStartDate = medicineView.findViewById<TextView>(R.id.medicineStartDate)
//                val medicineName = medicineView.findViewById<TextView>(R.id.medicineName)
//                val deleteButton = medicineView.findViewById<Button>(R.id.deleteMedicineButton)
//
//                medicineStartDate.text = entry.startDate
//                medicineName.text = entry.medicineName
//
//                deleteButton.setOnClickListener {
//                    lifecycleScope.launch {
//                        MedicineManager.deleteMedicineEntry(db, entry)
//                        displayMedicineEntries()  // Refresh the list
//                    }
//                }
//
//                medicineListLayout.addView(medicineView)
//            }
//        }
//    }
//}
package com.example.nafld_app

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddMedicineActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var startDateInput: EditText
    private lateinit var endDateInput: EditText
    private lateinit var medicineListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the language based on the global setting
        setAppLocale(AppSettings.currentLanguage)

        setContentView(R.layout.activity_add_medicine)

        // Initialize views
        startDateInput = findViewById(R.id.startDateInput)
        endDateInput = findViewById(R.id.endDateInput)
        val medicineNameInput = findViewById<EditText>(R.id.medicineNameInput)
        val doseInput = findViewById<EditText>(R.id.doseInput)
        val notesInput = findViewById<EditText>(R.id.notesInput)
        val addMedicineButton = findViewById<Button>(R.id.addMedicineButton)
        val closeButton = findViewById<Button>(R.id.closeAddMedicineButton)
        medicineListLayout = findViewById(R.id.medicineListLayout)

        db = AppDatabase.getDatabase(this)

        // Set up listeners for DatePickerDialogs
        startDateInput.setOnClickListener {
            showDatePickerDialog { date ->
                startDateInput.setText(date)
            }
        }

        endDateInput.setOnClickListener {
            showDatePickerDialog { date ->
                endDateInput.setText(date)
            }
        }

        // Add medicine entry on button click
        addMedicineButton.setOnClickListener {
            addMedicineEntry(
                startDateInput.text.toString(),
                endDateInput.text.toString(),
                medicineNameInput.text.toString(),
                doseInput.text.toString(),
                notesInput.text.toString()
            )
        }

        // Set up the close button
        closeButton.setOnClickListener {
            finish()
        }

        // Display existing medicine entries
        displayMedicineEntries()
    }

    private fun addMedicineEntry(startDate: String, endDate: String, medicineName: String, dose: String, notes: String) {
        if (startDate.isEmpty() || medicineName.isEmpty() || dose.isEmpty()) {
            showErrorPopup(getString(R.string.error_fill_all_fields))
            return
        }

        val medicineEntry = MedicineEntry(
            startDate = startDate,
            endDate = if (endDate.isEmpty()) "" else endDate,
            medicineName = medicineName,
            dose = dose,
            notes = notes
        )

        lifecycleScope.launch {
            MedicineManager.addMedicineEntry(db, medicineEntry)
            displayMedicineEntries()
            clearInputFields()
            Toast.makeText(this@AddMedicineActivity, getString(R.string.entry_saved), Toast.LENGTH_SHORT).show()
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

    private fun displayMedicineEntries() {
        lifecycleScope.launch {
            val medicineEntries = MedicineManager.getMedicineEntry(db)
            medicineListLayout.removeAllViews()

            if (medicineEntries.isNotEmpty()) {
                for (entry in medicineEntries) {
                    val entryView = createMedicineEntryView(entry)
                    medicineListLayout.addView(entryView)
                }
            } else {
                val textView = TextView(this@AddMedicineActivity).apply {
                    text = getString(R.string.no_medicine_entries)
                }
                medicineListLayout.addView(textView)
            }
        }
    }

    private fun createMedicineEntryView(entry: MedicineEntry): View {
        val entryView = layoutInflater.inflate(R.layout.item_medicine_entry, null)

        // Input date format (assuming the date is stored in "dd/MM/yyyy" format)
        val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Output date format (desired format "dd MMMM yyyy")
        val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        // Parse and format the start date
        val parsedStartDate = inputDateFormat.parse(entry.startDate)
        val formattedStartDate = outputDateFormat.format(parsedStartDate!!)

        // Set the formatted start date
        entryView.findViewById<TextView>(R.id.medicineStartDate).text = formattedStartDate
        entryView.findViewById<TextView>(R.id.medicineName).text = entry.medicineName

        entryView.findViewById<Button>(R.id.deleteMedicineButton).setOnClickListener {
            lifecycleScope.launch {
                MedicineManager.deleteMedicineEntry(db, entry)
                displayMedicineEntries() // Refresh the list after deletion
            }
        }

        return entryView
    }

    private fun clearInputFields() {
        startDateInput.text.clear()
        endDateInput.text.clear()
        findViewById<EditText>(R.id.medicineNameInput).text.clear()
        findViewById<EditText>(R.id.doseInput).text.clear()
        findViewById<EditText>(R.id.notesInput).text.clear()
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = android.content.res.Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
