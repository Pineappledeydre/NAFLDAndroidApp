package com.example.nafld_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.nafld_app.databinding.SurveyLayoutBinding
import com.example.nafld_app.databinding.DialogLastSurveyResultsBinding
import com.example.nafld_app.AppSettings
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.text.Html
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: SurveyLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppLocale(AppSettings.currentLanguage)

        // Initialize the view binding
        binding = SurveyLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the survey title and handle form submission
        binding.submitSurveyButton.setOnClickListener {
            saveSurveyData()
        }

        binding.showLastResultsButton.setOnClickListener {
            showLastSurveyResults()
        }

        binding.closeSurveyButton.setOnClickListener {
            finish() // Close this activity and go back to the previous one (MainActivity)
        }
    }
    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun getLocalizedString(context: Context, resId: Int): String {
        val resName = context.resources.getResourceEntryName(resId)
        val localizedResName = if (AppSettings.currentLanguage == "ru") resName + "_ru" else resName
        val localizedResId = context.resources.getIdentifier(localizedResName, "string", context.packageName)
        return if (localizedResId != 0) context.getString(localizedResId) else context.getString(resId)
    }

    private fun saveSurveyData() {
        val sharedPreferences = getSharedPreferences("SurveyData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Save data from each EditText to SharedPreferences
        editor.putString("first_symptoms_date", binding.firstSymptomsDateEditText.text.toString())
        editor.putString("main_symptoms", binding.mainSymptomsEditText.text.toString())
        editor.putString("symptom_frequency", binding.symptomFrequencyEditText.text.toString())
        editor.putString("diagnosed_date", binding.diagnosedDateEditText.text.toString())
        editor.putString("liver_diseases", binding.liverDiseasesEditText.text.toString())
        editor.putString("chronic_diseases", binding.chronicDiseasesEditText.text.toString())
        editor.putString("medications", binding.medicationsEditText.text.toString())
        editor.putString("alcohol_consumption", binding.alcoholConsumptionEditText.text.toString())
        editor.putString("diet", binding.dietEditText.text.toString())
        editor.putString("physical_activity", binding.physicalActivityEditText.text.toString())
        editor.putString("family_history", binding.familyHistoryEditText.text.toString())
        editor.putString("stress_level", binding.stressLevelEditText.text.toString())
        editor.putString("sleep_regimen", binding.sleepRegimenEditText.text.toString())
        editor.putString("tests", binding.testsEditText.text.toString())
        editor.putString("alternative_treatments", binding.alternativeTreatmentsEditText.text.toString())

        // Commit the changes
        editor.apply()

        // Notify the user that the survey has been saved
        Toast.makeText(this, getString(R.string.survey_saved), Toast.LENGTH_SHORT).show()
    }

    private fun showLastSurveyResults() {
        val sharedPreferences = getSharedPreferences("SurveyData", MODE_PRIVATE)

        // Retrieve saved data
        val firstSymptomsDate = sharedPreferences.getString("first_symptoms_date", "")
        val mainSymptoms = sharedPreferences.getString("main_symptoms", "")
        val symptomFrequency = sharedPreferences.getString("symptom_frequency", "")
        val diagnosedDate = sharedPreferences.getString("diagnosed_date", "")
        val liverDiseases = sharedPreferences.getString("liver_diseases", "")
        val chronicDiseases = sharedPreferences.getString("chronic_diseases", "")
        val medications = sharedPreferences.getString("medications", "")
        val alcoholConsumption = sharedPreferences.getString("alcohol_consumption", "")
        val diet = sharedPreferences.getString("diet", "")
        val physicalActivity = sharedPreferences.getString("physical_activity", "")
        val familyHistory = sharedPreferences.getString("family_history", "")
        val stressLevel = sharedPreferences.getString("stress_level", "")
        val sleepRegimen = sharedPreferences.getString("sleep_regimen", "")
        val tests = sharedPreferences.getString("tests", "")
        val alternativeTreatments = sharedPreferences.getString("alternative_treatments", "")

        // Format the results with HTML for bold text
        val results = """
        <b>${getString(R.string.first_symptoms_date)} -</b> $firstSymptomsDate<br>
        <b>${getString(R.string.main_symptoms)} -</b> $mainSymptoms<br>
        <b>${getString(R.string.symptom_frequency)} -</b> $symptomFrequency<br>
        <b>${getString(R.string.diagnosed_date)} -</b> $diagnosedDate<br>
        <b>${getString(R.string.liver_diseases)} -</b> $liverDiseases<br>
        <b>${getString(R.string.chronic_diseases)} -</b> $chronicDiseases<br>
        <b>${getString(R.string.medications)} -</b> $medications<br>
        <b>${getString(R.string.alcohol_consumption)} -</b> $alcoholConsumption<br>
        <b>${getString(R.string.diet)} -</b> $diet<br>
        <b>${getString(R.string.physical_activity)} -</b> $physicalActivity<br>
        <b>${getString(R.string.family_history)} -</b> $familyHistory<br>
        <b>${getString(R.string.stress_level)} -</b> $stressLevel<br>
        <b>${getString(R.string.first_symptoms_date)} -</b> $firstSymptomsDate<br>
        <b>${getString(R.string.main_symptoms)} -</b> $mainSymptoms<br>
        <b>${getString(R.string.symptom_frequency)} -</b> $symptomFrequency<br>
        <b>${getString(R.string.diagnosed_date)} -</b> $diagnosedDate<br>
        <b>${getString(R.string.liver_diseases)} -</b> $liverDiseases<br>
        <b>${getString(R.string.chronic_diseases)} -</b> $chronicDiseases<br>
        <b>${getString(R.string.medications)} -</b> $medications<br>
        <b>${getString(R.string.alcohol_consumption)} -</b> $alcoholConsumption<br>
        <b>${getString(R.string.diet)} -</b> $diet<br>
        <b>${getString(R.string.physical_activity)} -</b> $physicalActivity<br>
        <b>${getString(R.string.family_history)} -</b> $familyHistory<br>
        <b>${getString(R.string.stress_level)} -</b> $stressLevel<br>
        <b>${getString(R.string.sleep_regimen)} -</b> $sleepRegimen<br>
        <b>${getString(R.string.tests)} -</b> $tests<br>
        <b>${getString(R.string.alternative_treatments)} -</b> $alternativeTreatments
        <b>${getString(R.string.sleep_regimen)} -</b> $sleepRegimen<br>
        <b>${getString(R.string.tests)} -</b> $tests<br>
        <b>${getString(R.string.alternative_treatments)} -</b> $alternativeTreatments
    """.trimIndent()


        // Inflate the custom dialog layout
        val dialogBinding = DialogLastSurveyResultsBinding.inflate(LayoutInflater.from(this))

        // Set the results to the TextView in the custom layout, parsing HTML
        dialogBinding.resultsTextView.text = Html.fromHtml(results, Html.FROM_HTML_MODE_LEGACY)

        // Create and show the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        // Handle close button
        dialogBinding.closeButton.setOnClickListener {
            dialog.dismiss() // Close the dialog when the Close button is clicked
        }

        // Handle "Save as PDF" button click
        dialogBinding.savePdfButton.setOnClickListener {
            saveInfoAsPdf(results)
        }

        dialog.show()
    }

    private fun saveInfoAsPdf(results: String) {
        // Convert the HTML string to plain text
        val plainTextResults = Html.fromHtml(results, Html.FROM_HTML_MODE_LEGACY).toString()

        val fileName = "SurveyResults_${System.currentTimeMillis()}.pdf"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        val pdfDocument = PdfDocument()
        val pageWidth = 595 // A4 width in points (72 points per inch)
        val pageHeight = 842 // A4 height in points

        // Set up paint and initial Y position for the first page
        val paint = Paint()
        var yPos = 70f
        var pageNumber = 1

        // Helper function to create a new page
        fun createPage(): PdfDocument.Page {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            return pdfDocument.startPage(pageInfo)
        }

        var page = createPage()
        var canvas: Canvas = page.canvas

        // Add a title for the PDF
        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("Survey Results", (pageWidth / 2) - 60f, 40f, paint)

        // Draw a line under the title
        paint.textSize = 1f
        canvas.drawLine(40f, 50f, (pageWidth - 40).toFloat(), 50f, paint)

        // Prepare body text style
        paint.textSize = 14f
        paint.isFakeBoldText = false

        // Split the plain text results into lines
        val lines = plainTextResults.split("\n")

        // Loop through lines and create a new page if the content exceeds the current page
        for (line in lines) {
            if (yPos + paint.descent() - paint.ascent() > pageHeight - 60) {
                // Finish the current page
                pdfDocument.finishPage(page)

                // Start a new page and reset Y position
                pageNumber++
                page = createPage()
                canvas = page.canvas
                yPos = 40f // Reset yPos for new page

                // Add continued title or page header if needed
                paint.textSize = 20f
                paint.isFakeBoldText = true
                canvas.drawText("Survey Results - Continued", 40f, yPos, paint)
                yPos += 30f // Move the Y position down after the header
                paint.textSize = 14f
                paint.isFakeBoldText = false
            }

            // Draw the current line on the page
            canvas.drawText(line, 40f, yPos, paint)
            yPos += (paint.descent() - paint.ascent()) + 5f // Move to the next line
        }

        // Finish the last page
        pdfDocument.finishPage(page)

        // Write the PDF to the file
        FileOutputStream(file).use { fos ->
            pdfDocument.writeTo(fos)
        }

        pdfDocument.close()

        // Use FileProvider to get a content URI
        val uri = androidx.core.content.FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            file
        )

        // Create an intent to share the PDF
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant temporary read permission
        startActivity(Intent.createChooser(intent, "Share survey results via"))
    }

}
