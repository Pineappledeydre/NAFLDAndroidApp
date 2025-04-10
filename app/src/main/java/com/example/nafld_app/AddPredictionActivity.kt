package com.example.nafld_app

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nafld_app.databinding.ActivityAddPredictionBinding
import java.text.SimpleDateFormat
import java.util.*

class AddPredictionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPredictionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the language based on the global setting
        setAppLocale(AppSettings.currentLanguage)

        binding = ActivityAddPredictionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGenderSpinner()
        setupUI()

        binding.addPredictionButton.setOnClickListener {
            addPredictionEntry()
        }

        binding.closeAddPredictionButton.setOnClickListener {
            finish() // Close this activity and go back to the previous one (MainActivity)
        }

        binding.dateInput.setOnClickListener {
            showDatePickerDialog(binding.dateInput)
        }
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = android.content.res.Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupGenderSpinner() {
        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_options, // Add this array to your resources
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genderAdapter
    }

    private fun setupUI() {
        updatePredictionList()
    }

    private fun addPredictionEntry() {
        val date = binding.dateInput.text.toString()
        val gender = binding.genderSpinner.selectedItem.toString()
        val genderValue = if (gender == getString(R.string.male)) 1 else 0
        val ast = binding.astInput.text.toString().toDoubleOrNull()
        val ggtp = binding.ggtpInput.˚text.toString().toDoubleOrNull()
        val lpvp = binding.lpvpInput.text.toString().toDoubleOrNull()
        val trigl = binding.triglInput.text.toString().toDoubleOrNull()
        val bilirr = binding.bilirrInput.text.toString().toDoubleOrNull()
        val crb = binding.crbInput.text.toString().toDoubleOrNull()

        if (date.isEmpty() || ast == null || ggtp == null || lpvp == null || trigl == null || bilirr == null || crb == null) {
            showErrorPopup(getString(R.string.error_fill_all_fields))
            return
        }

        val probability = calculateNlfdPrediction(genderValue, ast, ggtp, lpvp, trigl, bilirr, crb)
        val newEntry = PredictionEntry(date, genderValue, ast, ggtp, lpvp, trigl, bilirr, crb, probability)

        PredictionManager.addPredictionEntry(newEntry)
        updatePredictionList()

        clearInputFields()

        showSuccessPopup(getString(R.string.entry_saved))
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
            editText.setText(selectedDate)
        }, year, month, day).show()
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

    private fun updatePredictionList() {
        binding.predictionListLayout.removeAllViews()

        val predictionEntries = PredictionManager.getPredictionEntries()
        if (predictionEntries.isEmpty()) {
            showToast(getString(R.string.no_prediction_entries))
            return
        }

        predictionEntries.forEach { entry ->
            val entryView = createPredictionEntryView(entry)
            binding.predictionListLayout.addView(entryView)
        }
    }

    private fun createPredictionEntryView(entry: PredictionEntry): View {
        val entryView = layoutInflater.inflate(R.layout.item_prediction_entry, null)

        // Format the date
        val inputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) // Adjust this to your input format if needed
        val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val parsedDate = inputDateFormat.parse(entry.date)
        val formattedDate = outputDateFormat.format(parsedDate!!)

        // Set the formatted date
        entryView.findViewById<TextView>(R.id.predictionDate).text = formattedDate
        entryView.findViewById<TextView>(R.id.predictionProbability).text = String.format("%.2f", entry.probability * 100) + "%"

        entryView.setOnClickListener {
            showPredictionDetails(entry)
        }

        entryView.findViewById<Button>(R.id.deletePredictionButton).setOnClickListener {
            deletePredictionEntry(entry)
        }

        return entryView
    }

    private fun showPredictionDetails(entry: PredictionEntry) {
        val details = """
            ${getString(R.string.date)}: ${entry.date}
            ${getString(R.string.gender)}: ${if (entry.gender == 1) getString(R.string.male) else getString(R.string.female)}
            ${getString(R.string.ast)}: ${entry.ast}
            ${getString(R.string.ggtp)}: ${entry.ggtp}
            ${getString(R.string.lpvp)}: ${entry.lpvp}
            ${getString(R.string.trigl)}: ${entry.trigl}
            ${getString(R.string.bilirr)}: ${entry.bilirr}
            ${getString(R.string.crb)}: ${entry.crb}
            ${getString(R.string.probability)}: ${String.format("%.2f", entry.probability * 100)}%
        """.trimIndent()
//        val details = """
//        ${getString(R.string.date)}: ${entry.date}
//        ${getString(R.string.gender)}: ${if (entry.gender == 1) getString(R.string.male) else getString(R.string.female)}
//        ${getString(R.string.ast)}: ${entry.ast}
//        ${getString(R.string.ggtp)}: ${entry.ggtp}
//        ${getString(R.string.lpvp)}: ${entry.lpvp}
//        ${getString(R.string.trigl)}: ${entry.trigl}
//        ${getString(R.string.bilirr)}: ${entry.bilirr}
//        ${getString(R.string.crb)}: ${entry.crb}
//        ${getString(R.string.probability)}: ${String.format("%.2f", entry.probability)}
//    """.trimIndent()
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.prediction_details))
            .setMessage(details)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deletePredictionEntry(entry: PredictionEntry) {
        PredictionManager.deletePredictionEntry(entry)
        updatePredictionList()
    }

    private fun clearInputFields() {
        binding.dateInput.text.clear()
        binding.astInput.text.clear()
        binding.ggtpInput.text.clear()
        binding.lpvpInput.text.clear()
        binding.triglInput.text.clear()
        binding.bilirrInput.text.clear()
        binding.crbInput.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun calculateNlfdPrediction(
        gender: Int,
        ast: Double,
        ggtp: Double,
        lpvp: Double,
        trigl: Double,
        bilirr: Double,
        crb: Double
    ): Double {
        val interceptAdjusted = -5.8922
        val coefficientsAdjusted = mapOf(
            "gender" to 1.3358,
            "ast" to 0.1574,
            "ggtp" to 0.0936,
            "lpvp" to -0.4675,
            "trigl" to 0.1024,
            "bilirr" to -0.0495,
            "crb" to -0.0726
        )

        var prediction = interceptAdjusted
        prediction += coefficientsAdjusted["gender"]!! * gender
        prediction += coefficientsAdjusted["ast"]!! * ast
        prediction += coefficientsAdjusted["ggtp"]!! * ggtp
        prediction += coefficientsAdjusted["lpvp"]!! * lpvp
        prediction += coefficientsAdjusted["trigl"]!! * trigl
        prediction += coefficientsAdjusted["bilirr"]!! * bilirr
        prediction += coefficientsAdjusted["crb"]!! * crb

        return 1 / (1 + Math.exp(-prediction))
    }
}
