package com.example.nafld_app

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.PieChart
import android.graphics.Bitmap
import android.view.View
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.content.ContentValues
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast

class SaveInfoActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var selectedStartDate: Long = 0
    private var selectedEndDate: Long = 0
    private lateinit var dateRangeText: TextView

    // Declare chart views
    private lateinit var exerciseChart: ImageView // Change from BarChart to ImageView
    private lateinit var activeDaysText: TextView

    private lateinit var medicineChart: ScatterChart
    private lateinit var triggerFoodChart: PieChart

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(AppSettings.currentLanguage)
        setContentView(R.layout.activity_save_info)

        db = AppDatabase.getDatabase(this)

        val saveButton = findViewById<Button>(R.id.saveButton)
        val closeButton = findViewById<Button>(R.id.closeButton)
        val backButton = findViewById<Button>(R.id.backButton)
        val startDateButton = findViewById<Button>(R.id.startDateButton)
        val endDateButton = findViewById<Button>(R.id.endDateButton)
        val showDataButton = findViewById<Button>(R.id.showDataButton)
        val infoContent = findViewById<TextView>(R.id.infoContent)
        // Set up buttons and initialize back button visibility

        dateRangeText = findViewById(R.id.dateRangeText)
        dateRangeText.text = getString(R.string.selected_date_range_not_selected)


        // Initialize chart views
        exerciseChart = findViewById(R.id.exerciseChart)
        activeDaysText = findViewById(R.id.activeDaysText)
        medicineChart = findViewById(R.id.medicineChart)
        triggerFoodChart = findViewById(R.id.triggerFoodChart)

        loadAndDisplayInfo()

        saveButton.setOnClickListener { saveInfo() }
        closeButton.setOnClickListener {
            Log.d("SaveInfoActivity", "Close button clicked")
            finish()
        }

        startDateButton.setOnClickListener {
            showDatePicker { date ->
                selectedStartDate = date
                val formattedStartDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedStartDate))
                val formattedEndDate = formatSelectedEndDate()
                dateRangeText.text = getString(R.string.selected_date_range, formattedStartDate, formattedEndDate)
                Log.d("SaveInfoActivity", "Start date selected: $selectedStartDate")
            }
        }

        endDateButton.setOnClickListener {
            showDatePicker { date ->
                selectedEndDate = date
                val formattedStartDate = formatSelectedStartDate()
                val formattedEndDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedEndDate))
                dateRangeText.text = getString(R.string.selected_date_range, formattedStartDate, formattedEndDate)
                Log.d("SaveInfoActivity", "End date selected: $selectedEndDate")
            }
        }

        showDataButton.setOnClickListener {
            loadAndDisplayInfo()
        }
    }

    private fun formatSelectedStartDate(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedStartDate))
    }


    private fun formatSelectedEndDate(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedEndDate))
    }


    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                onDateSelected(selectedDate.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadAndDisplayInfo() {
        lifecycleScope.launch {
            try {
                val exerciseLogs = db.exerciseLogDao().getAllExerciseLogs()
                val medicineLogs = db.medicineEntryDao().getAllMedicineEntries()
                val triggerFoodEntries = db.triggerFoodDao().getAllTriggerFoodEntries()

                // Filter and sort logs by date
                val filteredExerciseLogs = filterLogs(exerciseLogs, selectedStartDate, selectedEndDate) { it.date }
                    .sortedBy { parseDateSafe(it.date) }
                val filteredMedicineLogs = filterLogs(medicineLogs, selectedStartDate, selectedEndDate) { it.startDate }
                    .sortedBy { parseDateSafe(it.startDate) }
                val filteredTriggerFoodEntries = filterLogs(triggerFoodEntries, selectedStartDate, selectedEndDate) { it.date }
                    .sortedBy { parseDateSafe(it.date) }

                // Update only the charts
                setupExerciseChart(filteredExerciseLogs)
                setupMedicineChart(filteredMedicineLogs)
                setupTriggerFoodChart(filteredTriggerFoodEntries)

            } catch (e: Exception) {
                Log.e("SaveInfoActivity", "Error loading info", e)
            }
        }
    }


    // Helper function to safely parse dates
    private fun parseDateSafe(dateString: String): Date? {
        val yyyyMMddFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val ddMMyyyyFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return try {
            // Try parsing based on date format pattern
            when {
                dateString.matches(Regex("\\d{8}")) -> yyyyMMddFormat.parse(dateString)  // yyyyMMdd format
                dateString.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) -> ddMMyyyyFormat.parse(dateString)  // dd/MM/yyyy format
                else -> {
                    Log.e("SaveInfoActivity", "Unrecognized date format: $dateString")
                    null
                }
            }
        } catch (e: ParseException) {
            Log.e("SaveInfoActivity", "Error parsing date: $dateString", e)
            null
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupExerciseChart(exerciseLogs: List<ExerciseLog>) {
        val totalDays = ((selectedEndDate - selectedStartDate) / (24 * 60 * 60 * 1000)).toInt() + 1
        val squareSize = 20
        val bitmap = Bitmap.createBitmap(squareSize * 10, squareSize * ((totalDays + 9) / 10), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Paint for filling squares
        val fillPaint = Paint().apply {
            isAntiAlias = true
        }

        // Paint for drawing borders
        val borderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }

        val loggedDays = exerciseLogs.map { SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(it.date)?.time }
            .filterNotNull()
            .distinct()
            .size

        val activityPercentage = if (totalDays > 0) (loggedDays.toFloat() / totalDays * 100).toInt() else 0
        val squaresToFill = (totalDays * (activityPercentage / 100.0)).toInt()

        // Draw filled squares with borders
        fillPaint.color = getColor(R.color.primaryColor)
        for (i in 0 until squaresToFill) {
            val x = i % 10
            val y = i / 10
            val left = x * squareSize.toFloat()
            val top = y * squareSize.toFloat()
            val right = (x + 1) * squareSize.toFloat()
            val bottom = (y + 1) * squareSize.toFloat()

            canvas.drawRect(left, top, right, bottom, fillPaint)
            canvas.drawRect(left, top, right, bottom, borderPaint)  // Draw border
        }

        // Draw unfilled squares with borders
        fillPaint.color = getColor(R.color.primaryColorLight)
        for (i in squaresToFill until totalDays) {
            val x = i % 10
            val y = i / 10
            val left = x * squareSize.toFloat()
            val top = y * squareSize.toFloat()
            val right = (x + 1) * squareSize.toFloat()
            val bottom = (y + 1) * squareSize.toFloat()

            canvas.drawRect(left, top, right, bottom, fillPaint)
            canvas.drawRect(left, top, right, bottom, borderPaint)  // Draw border
        }

        // Set percentage text in separate TextView for clarity
        activeDaysText.text = getString(R.string.active_days, activityPercentage)
        activeDaysText.visibility = View.VISIBLE  // Ensure visibility

        // Display the chart bitmap
        exerciseChart.setBackgroundColor(Color.TRANSPARENT)
        exerciseChart.setImageBitmap(bitmap)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupMedicineChart(medicineLogs: List<MedicineEntry>) {
        val startDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val entries = mutableListOf<Entry>()

        // Populate entries with log data, storing medicine name in the data field of each Entry
        medicineLogs.forEachIndexed { index, log ->
            val startDate = startDateFormat.parse(log.startDate)?.time ?: 0L
            val endDate = startDateFormat.parse(log.endDate)?.time ?: 0L
            val duration = (endDate - startDate) / (1000 * 60 * 60 * 24)
            entries.add(Entry(index.toFloat(), duration.toFloat()).apply {
                data = log.medicineName  // Store the medicine name in the data field
            })
        }

        if (entries.isEmpty()) {
            // Show description when no data
            medicineChart.apply {
                description.text = getString(R.string.medicine_chart_description)
                description.textColor = getColor(R.color.textColor)
                description.setPosition(width - 20f, height - 20f)
                setNoDataText(getString(R.string.medicine_chart_description))
                setNoDataTextColor(getColor(R.color.textColor))
                invalidate()
            }
            return
        }
        val maxYValue = (entries.maxOfOrNull { it.y } ?: 0f) + 10f

        // Create ScatterDataSet and configure with blue color
        val dataSet = ScatterDataSet(entries, getString(R.string.medicine_chart_description)).apply {
            scatterShapeSize = 40f
            setScatterShape(ScatterChart.ScatterShape.CIRCLE)
            color = Color.BLUE  // Set all points to blue color
            valueTextColor = getColor(R.color.textColor)
            valueTextSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String {
                    val daysLabel = getString(R.string.days)
                    val medicineName = entry?.data as? String ?: ""
                    return "\n$medicineName: ${entry?.y?.toInt()} $daysLabel"  // Add newline for spacing below point
                }
            }
            setDrawValues(true)
        }

        // Assign data to ScatterChart
        val scatterData = ScatterData(dataSet)
        medicineChart.apply {
            data = scatterData
            description.apply {
                text = getString(R.string.medicine_chart_description)
                textColor = getColor(R.color.textColor)
                isEnabled = true
                setPosition(width - 20f, height - 20f)
            }
            setNoDataText(getString(R.string.medicine_chart_description))
            setNoDataTextColor(getColor(R.color.textColor))

            axisLeft.apply {
                textColor = getColor(R.color.textColor)
                axisMaximum = maxYValue  // Set max Y axis value
            }
            axisLeft.textColor = getColor(R.color.textColor)
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            invalidate()  // Refresh the chart to apply new color settings
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupTriggerFoodChart(triggerFoodEntries: List<TriggerFoodEntry>) {
        if (triggerFoodEntries.isEmpty()) {
            triggerFoodChart.apply {
                description.text = getString(R.string.trigger_food_chart_description)
                description.textColor = getColor(R.color.textColor)
                description.setPosition(width - 20f, height - 20f)
                setNoDataText(getString(R.string.trigger_food_chart_description))
                setNoDataTextColor(getColor(R.color.textColor))
                invalidate()
            }
            return
        }
        val foodCounts = triggerFoodEntries.groupingBy { it.food }.eachCount()
        val entries = foodCounts.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, getString(R.string.trigger_food_distribution))

        val customColors = listOf(
            getColor(R.color.pieColor1),
            getColor(R.color.pieColor2),
            getColor(R.color.pieColor3),
            getColor(R.color.pieColor4),
            getColor(R.color.pieColor5)
        )

        dataSet.apply {
            colors = customColors
            valueTextColor = getColor(R.color.textColor)
            valueTextSize = 12f
        }

        triggerFoodChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            setEntryLabelColor(getColor(R.color.textColor))
            setCenterTextColor(getColor(R.color.textColor))
            setDrawEntryLabels(false)
            centerText = getString(R.string.trigger_food_chart_center)
            setCenterTextSize(14f)
            legend.textColor = getColor(R.color.textColor)
            setNoDataTextColor(getColor(R.color.textColor))
            invalidate() // Refresh the chart
        }
    }


    private fun <T> filterLogs(logs: List<T>, startDate: Long, endDate: Long, dateExtractor: (T) -> String): List<T> {
        return logs.filter { log ->
            val logDateString = dateExtractor(log)
            Log.d("SaveInfoActivity", "Extracted log date string: $logDateString") // Log the extracted date string

            val logDate = when {
                logDateString.matches(Regex("\\d{8}")) -> formatDateToComparableYYYYMMDD(logDateString)
                logDateString.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) -> formatDateToComparableDDMMYYYY(logDateString)
                else -> {
                    Log.e("SaveInfoActivity", "Invalid date format: $logDateString")
                    null
                }
            }

            if (logDate == null) {
                Log.e("SaveInfoActivity", "Failed to parse date: $logDateString")
            }

            logDate != null && logDate in Date(startDate)..Date(endDate)
        }
    }


    private fun formatDateToComparableYYYYMMDD(dateString: String): Date? {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return try {
            inputFormat.parse(dateString)
        } catch (e: ParseException) {
            Log.e("SaveInfoActivity", "Error parsing date (yyyyMMdd): $dateString", e)
            null
        }
    }


    private fun formatDateToComparableDDMMYYYY(dateString: String): Date? {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            inputFormat.parse(dateString)
        } catch (e: ParseException) {
            Log.e("SaveInfoActivity", "Error parsing date (dd/MM/yyyy): $dateString", e)
            null
        }
    }


    private fun formatDate(dateString: String, outputFormat: SimpleDateFormat): String {
        val date = parseDateSafe(dateString)
        return date?.let { outputFormat.format(it) } ?: dateString
    }


    private fun formatExerciseLogs(logs: List<ExerciseLog>): String {
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return logs.sortedBy {
            val parsedDate = parseDateSafe(it.date)
            Log.d("SaveInfoActivity", "Parsed exercise date '${it.date}' as '$parsedDate'")
            parsedDate
        }.joinToString(separator = "\n\n") { log ->
            val formattedDate = formatDate(log.date, outputDateFormat)
            "${getString(R.string.date)}: $formattedDate\n${getString(R.string.details_1, log.details)}"
        }
    }


    private fun formatMedicineLogs(logs: List<MedicineEntry>): String {
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return logs.sortedBy {
            val parsedDate = parseDateSafe(it.startDate)
            Log.d("SaveInfoActivity", "Parsed medicine start date '${it.startDate}' as '$parsedDate'")
            parsedDate
        }.joinToString(separator = "\n\n") { log ->
            val startDateFormatted = formatDate(log.startDate, outputDateFormat)
            val endDateFormatted = formatDate(log.endDate, outputDateFormat)
            "${getString(R.string.start_date_1)} $startDateFormatted\n" +
                    "${getString(R.string.end_date_1)} $endDateFormatted\n" +
                    "${getString(R.string.medicine_1)} ${log.medicineName}\n" +
                    "${getString(R.string.dose_1)} ${log.dose}"
        }
    }


    private fun formatTriggerFoodEntries(logs: List<TriggerFoodEntry>): String {
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return logs.sortedBy {
            val parsedDate = parseDateSafe(it.date)
            Log.d("SaveInfoActivity", "Parsed trigger food date '${it.date}' as '$parsedDate'")
            parsedDate
        }.joinToString(separator = "\n\n") { log ->
            val dateFormatted = formatDate(log.date, outputDateFormat)
            "${getString(R.string.date_1, dateFormatted)}\n" +
                    "${getString(R.string.food_1, log.food)}\n" +
                    "${getString(R.string.symptoms_1, log.symptoms)}\n" +
                    "${getString(R.string.info_1, log.info)}"
        }
    }


    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = android.content.res.Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    private val CREATE_FILE_REQUEST_CODE = 1
    private var tempHtmlContent: String? = null


    private fun saveInfo() {
        lifecycleScope.launch {
            try {
                // Prepare date range text
                val startDate = formatSelectedStartDate()
                val endDate = formatSelectedEndDate()
                val dateRangeText = "$startDate - $endDate"

                // Fetch and filter data based on the selected date range
                val exerciseLogs = db.exerciseLogDao().getAllExerciseLogs()
                val medicineLogs = db.medicineEntryDao().getAllMedicineEntries()
                val triggerFoodEntries = db.triggerFoodDao().getAllTriggerFoodEntries()

                val filteredExerciseLogs = filterLogs(exerciseLogs, selectedStartDate, selectedEndDate) { it.date }
                val filteredMedicineLogs = filterLogs(medicineLogs, selectedStartDate, selectedEndDate) { it.startDate }
                val filteredTriggerFoodEntries = filterLogs(triggerFoodEntries, selectedStartDate, selectedEndDate) { it.date }

                // Format logs with HTML line breaks
                val formattedExerciseLogs = formatExerciseLogs(filteredExerciseLogs).replace("\n", "<br>")
                val formattedMedicineLogs = formatMedicineLogs(filteredMedicineLogs).replace("\n", "<br>")
                val formattedTriggerFoodEntries = formatTriggerFoodEntries(filteredTriggerFoodEntries).replace("\n", "<br>")

                // Construct HTML content with filtered data
                val htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; padding: 20px; color: #333; }
                    h2 { color: #444; font-size: 1.5em; }
                    h3 { color: #555; font-size: 1.3em; margin-top: 20px; }
                    .section { margin-bottom: 20px; }
                    .log-entry { margin-left: 20px; margin-bottom: 10px; }
                </style>
            </head>
            <body>
                <h2>${getString(R.string.date)}: $dateRangeText</h2>
                
                <div class="section">
                    <h3>${getString(R.string.exercise_logs)}</h3>
                    <div class="log-entry">
                        <p>$formattedExerciseLogs</p>
                    </div>
                </div>
                
                <div class="section">
                    <h3>${getString(R.string.medicine_logs)}</h3>
                    <div class="log-entry">
                        <p>$formattedMedicineLogs</p>
                    </div>
                </div>
                
                <div class="section">
                    <h3>${getString(R.string.trigger_food_entries)}</h3>
                    <div class="log-entry">
                        <p>$formattedTriggerFoodEntries</p>
                    </div>
                </div>
            </body>
            </html>
            """.trimIndent()

                Log.d("SaveInfoActivity", "Filtered HTML content prepared")

                // Show the HTML in WebView
                showHtmlInWebView(htmlContent)

                // Trigger the file chooser to save the PDF, pass htmlContent to generatePdf()
                chooseFileLocation(htmlContent)

            } catch (e: Exception) {
                Log.e("SaveInfoActivity", "Error generating filtered HTML content", e)
            }
        }
    }


    private fun chooseFileLocation(htmlContent: String) {
        // Create an intent to open a file picker for creating a new document
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "SavedInfo_${System.currentTimeMillis()}.pdf")
        }
        // Store htmlContent for use in onActivityResult
        tempHtmlContent = htmlContent // Store content temporarily
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }


    // Handle the result of file picker intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.also { uri ->
                // The user picked a location, now save the PDF to that URI
                lifecycleScope.launch {
                    generatePdf(uri)
                }
            }
        }
    }


    // Generate and save the PDF using the selected URI
    private suspend fun generatePdf(uri: Uri) {
        // Create a PdfDocument object
        val pdfDocument = PdfDocument()

        // Create a page description with the desired size (A4 size: 595x842 points)
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)

        // Set up a Paint object for drawing text or content
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // Retrieve HTML content and convert it to plain text
        val htmlContent = tempHtmlContent ?: "<html><body><p>No Content</p></body></html>"
        val plainTextContent = Html.fromHtml(htmlContent).toString()

        // Draw content on the page's canvas as plain text
        val canvas = page.canvas
        val lines = plainTextContent.split("\n")
        var yPosition = 20f // Initial Y position

        for (line in lines) {
            // Check if we have reached the end of the page
            if (yPosition > pageInfo.pageHeight - 40) {
                // Finish the current page and start a new one
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                yPosition = 20f // Reset Y position for the new page
            }
            // Draw the text line on the PDF canvas
            canvas.drawText(line, 20f, yPosition, paint)
            yPosition += paint.descent() - paint.ascent() + 10 // Adjust line height
        }

        // Finish the page
        pdfDocument.finishPage(page)

        // Save the PDF to the selected URI
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
                Log.d("SaveInfoActivity", "PDF saved to $uri")
                notifyUserOfPdfSaved(uri)
            }
        } catch (e: Exception) {
            Log.e("SaveInfoActivity", "Error saving PDF", e)
        } finally {
            // Close the document
            pdfDocument.close()
        }
    }


    private fun notifyUserOfPdfSaved(uri: Uri) {
        runOnUiThread {
            Toast.makeText(this, "PDF saved at $uri", Toast.LENGTH_LONG).show()
        }
    }


    private fun showHtmlInWebView(htmlContent: String) {
        val webViewContainer = findViewById<FrameLayout>(R.id.webViewContainer)

        // Create WebView instance dynamically
        val webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
        }

        // Add WebView to the container
        webViewContainer.removeAllViews() // Clear any previous WebViews if needed
        webViewContainer.addView(webView)

        // Show the back button and set up its functionality
        val backButton = findViewById<Button>(R.id.backButton).apply {
            visibility = View.VISIBLE  // Make it visible
            setOnClickListener {
                webViewContainer.removeAllViews()  // Remove the WebView when going back
                visibility = View.GONE  // Hide back button again
            }
        }
    }

}