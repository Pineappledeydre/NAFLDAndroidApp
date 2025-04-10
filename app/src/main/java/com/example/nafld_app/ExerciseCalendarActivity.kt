//package com.example.nafld_app
//
//import android.app.TimePickerDialog
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.*
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import com.example.nafld_app.databinding.ActivityExerciseCalendarBinding
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.text.SimpleDateFormat
//import java.util.*
//
//class ExerciseCalendarActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityExerciseCalendarBinding
//    private var selectedDate: Long = 0
//    private lateinit var db: AppDatabase
//    private var currentPatientId: Int = 1  // Default to 0 (no patient)
//    private val patientIdMap: MutableMap<Int, String> = mutableMapOf()
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setAppLocale(AppSettings.currentLanguage)
//
//        binding = ActivityExerciseCalendarBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        db = AppDatabase.getDatabase(this)
//        val calendar = Calendar.getInstance()
//        selectedDate = calendar.timeInMillis
//
//        binding.closeCalendarButton.setOnClickListener {
//            finish()
//        }
//
//        binding.setNotificationTimeButton.setOnClickListener {
//            showTimePicker()
//        }
//
//        binding.openQuestionnaireButton.setOnClickListener {
//            val intent = Intent(this, ExerciseRecommendationActivity::class.java)
//            startActivity(intent)
//        }
//
//        binding.logExerciseButton.setOnClickListener {
//            Log.d("ExerciseCalendar", "Log Exercise button clicked")
//            logExercise()
//        }
//
//
//        binding.exerciseCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
//            val calendar = Calendar.getInstance()
//            calendar.set(year, month, dayOfMonth)
//            selectedDate = calendar.timeInMillis
//            showExercisesForDate(selectedDate)
//        }
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
//    private suspend fun addNewPatient(name: String, age: Int) {
//        val existingPatient = db.patientDao().getPatientByName(name)
//        if (existingPatient == null) {
//            val newPatient = Patient(name = name, age = age)
//            val patientId = db.patientDao().insertPatient(newPatient).toInt()
//
//            withContext(Dispatchers.Main) {
//                currentPatientId = patientId
//                patientIdMap[patientId] = name
//                Log.d("ExerciseCalendar", "Added new patient: $name with ID: $patientId")
//            }
//        } else {
//            withContext(Dispatchers.Main) {
//                currentPatientId = existingPatient.id
//                patientIdMap[existingPatient.id] = name
//                Log.d("ExerciseCalendar", "Patient $name already exists with ID: ${existingPatient.id}")
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun logExercise() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
//                val currentPatientId = sharedPreferences.getInt("patientId", 0)
//                Log.d("ExerciseCalendarActiv", "Using currentPatientId: $currentPatientId")
//
//                if (currentPatientId == 0) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(this@ExerciseCalendarActivity, "No patient ID found. Please select a patient.", Toast.LENGTH_LONG).show()
//                    }
//                    return@launch
//                }
//
//                val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
//                val date = dateFormat.format(selectedDate)
//
//                val exerciseDetails = binding.exerciseDetailsInput.text.toString()
//                if (exerciseDetails.isBlank()) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(this@ExerciseCalendarActivity, "Please enter exercise details.", Toast.LENGTH_LONG).show()
//                    }
//                    return@launch
//                }
//
//                Log.d("ExerciseCalendarActiv", "Inserting log with date: $date, details: $exerciseDetails, patientId: $currentPatientId")
//                val exerciseLog = ExerciseLog(date = date, details = exerciseDetails, patientId = currentPatientId)
//                val exerciseLogId = db.exerciseLogDao().insertExerciseLog(exerciseLog)
//                Log.d("ExerciseCalendarActiv", "Inserted exercise log with ID: $exerciseLogId")
//
//                // Fetch logs for the inserted date to display
//                showExercisesForDate(selectedDate)
//
//            } catch (e: Exception) {
//                Log.e("ExerciseCalendarActiv", "Error inserting exercise log", e)
//            }
//        }
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun showExercisesForDate(date: Long) {
//        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
//        val dateString = dateFormat.format(Date(date))
//
//        lifecycleScope.launch(Dispatchers.IO) {
//            val exerciseLogs = db.exerciseLogDao().getExerciseLogsForDateAndPatient(dateString, currentPatientId)
//            Log.d("ExerciseCalendar", "Fetched logs: $exerciseLogs")
//
//            withContext(Dispatchers.Main) {
//                binding.exerciseLogContainer.removeAllViews()
//                if (exerciseLogs.isNotEmpty()) {
//                    for (log in exerciseLogs) {
//                        val parsedDate = dateFormat.parse(log.date)
//                        val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(parsedDate!!)
//
//                        val logLayout = LinearLayout(this@ExerciseCalendarActivity).apply {
//                            orientation = LinearLayout.HORIZONTAL
//                            layoutParams = LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.MATCH_PARENT,
//                                LinearLayout.LayoutParams.WRAP_CONTENT
//                            )
//                        }
//
//                        val logTextView = TextView(this@ExerciseCalendarActivity).apply {
//                            text = "$formattedDate: ${log.details}"
//                            layoutParams = LinearLayout.LayoutParams(
//                                0,
//                                LinearLayout.LayoutParams.WRAP_CONTENT,
//                                1f
//                            )
//                        }
//
//                        val deleteButton = Button(this@ExerciseCalendarActivity).apply {
//                            id = View.generateViewId()
//                            text = getString(R.string.delete)
//                            setTextColor(resources.getColor(android.R.color.white, null))
//                            backgroundTintList = resources.getColorStateList(R.color.red, null)
//                            setPadding(4, 4, 4, 4)
//                            layoutParams = LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.WRAP_CONTENT,
//                                LinearLayout.LayoutParams.WRAP_CONTENT
//                            )
//                            setOnClickListener {
//                                deleteExerciseLog(log)
//                                showExercisesForDate(date)
//                            }
//                        }
//
//                        logLayout.addView(logTextView)
//                        logLayout.addView(deleteButton)
//                        binding.exerciseLogContainer.addView(logLayout)
//                    }
//                    Log.d("ExerciseCalendar", "Exercises found for date: $dateString -> $exerciseLogs")
//                } else {
//                    val textView = TextView(this@ExerciseCalendarActivity).apply {
//                        text = getString(R.string.no_exercises_found)
//                    }
//                    binding.exerciseLogContainer.addView(textView)
//                    Log.d("ExerciseCalendar", "No exercises found for date: $dateString")
//                }
//            }
//        }
//    }
//
//    private fun deleteExerciseLog(log: ExerciseLog) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            db.exerciseLogDao().deleteExerciseLog(log)
//            Log.d("ExerciseCalendar", "Deleted exercise log: ${log.date} - ${log.details}")
//            withContext(Dispatchers.Main) {
//                Toast.makeText(this@ExerciseCalendarActivity, getString(R.string.exercise_deleted), Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun showTimePicker() {
//        val calendar = Calendar.getInstance()
//        val hour = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute = calendar.get(Calendar.MINUTE)
//
//        TimePickerDialog(this, { _, selectedHour: Int, selectedMinute: Int ->
//            setNotificationTime(selectedHour, selectedMinute)
//        }, hour, minute, true).show()
//    }
//
//    private fun setNotificationTime(hour: Int, minute: Int) {
//        val sharedPreferences = getSharedPreferences("ExercisePrefs", MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putInt("notification_hour", hour)
//        editor.putInt("notification_minute", minute)
//        editor.apply()
//
//        Log.d("ExerciseCalendar", "Notification time set to $hour:$minute")
//        Toast.makeText(this, getString(R.string.notification_time_set_to, hour, minute), Toast.LENGTH_SHORT).show()
//    }
//
//    private fun showErrorPopup(message: String) {
//        AlertDialog.Builder(this)
//            .setTitle(getString(R.string.error))
//            .setMessage(message)
//            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
//            .show()
//    }
//
//    private fun formatDate(date: Long): String {
//        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
//        return dateFormat.format(Date(date))
//    }
//}
package com.example.nafld_app

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nafld_app.databinding.ActivityExerciseCalendarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ExerciseCalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExerciseCalendarBinding
    private var selectedDate: Long = 0
    private lateinit var db: AppDatabase

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppLocale(AppSettings.currentLanguage)

        binding = ActivityExerciseCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Initialize selectedDate to current date
        val calendar = Calendar.getInstance()
        selectedDate = calendar.timeInMillis

        binding.closeCalendarButton.setOnClickListener {
            finish()
        }

        binding.setNotificationTimeButton.setOnClickListener {
            showTimePicker()
        }

        binding.openQuestionnaireButton.setOnClickListener {
            val intent = Intent(this, ExerciseRecommendationActivity::class.java)
            startActivity(intent)
        }

        binding.logExerciseButton.setOnClickListener {
            logExercise()
        }

        binding.exerciseCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
            showExercisesForDate(selectedDate)
        }
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = android.content.res.Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun logExercise() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val date = dateFormat.format(selectedDate)

                val exerciseDetails = binding.exerciseDetailsInput.text.toString()
                if (exerciseDetails.isBlank()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ExerciseCalendarActivity, "Please enter exercise details.", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                val exerciseLog = ExerciseLog(date = date, details = exerciseDetails)
                val exerciseLogId = db.exerciseLogDao().insertExerciseLog(exerciseLog)
                Log.d("ExerciseCalendar", "Inserted exercise log with ID: $exerciseLogId")

                // Fetch logs for the inserted date to display
                showExercisesForDate(selectedDate)

            } catch (e: Exception) {
                Log.e("ExerciseCalendar", "Error inserting exercise log", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showExercisesForDate(date: Long) {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val dateString = dateFormat.format(Date(date))

        lifecycleScope.launch(Dispatchers.IO) {
            val exerciseLogs = db.exerciseLogDao().getExerciseLogsForDate(dateString)
            Log.d("ExerciseCalendar", "Fetched logs: $exerciseLogs")

            withContext(Dispatchers.Main) {
                binding.exerciseLogContainer.removeAllViews()
                if (exerciseLogs.isNotEmpty()) {
                    for (log in exerciseLogs) {
                        val parsedDate = dateFormat.parse(log.date)
                        val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(parsedDate!!)

                        val logLayout = LinearLayout(this@ExerciseCalendarActivity).apply {
                            orientation = LinearLayout.HORIZONTAL
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        }

                        val logTextView = TextView(this@ExerciseCalendarActivity).apply {
                            text = "$formattedDate: ${log.details}"
                            layoutParams = LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                        }

                        val deleteButton = Button(this@ExerciseCalendarActivity).apply {
                            id = View.generateViewId()
                            text = getString(R.string.delete)
                            setTextColor(resources.getColor(android.R.color.white, null))
                            backgroundTintList = resources.getColorStateList(R.color.red, null)
                            setPadding(4, 4, 4, 4)
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            setOnClickListener {
                                deleteExerciseLog(log)
                                showExercisesForDate(date)
                            }
                        }

                        logLayout.addView(logTextView)
                        logLayout.addView(deleteButton)
                        binding.exerciseLogContainer.addView(logLayout)
                    }
                    Log.d("ExerciseCalendar", "Exercises found for date: $dateString -> $exerciseLogs")
                } else {
                    val textView = TextView(this@ExerciseCalendarActivity).apply {
                        text = getString(R.string.no_exercises_found)
                    }
                    binding.exerciseLogContainer.addView(textView)
                    Log.d("ExerciseCalendar", "No exercises found for date: $dateString")
                }
            }
        }
    }

    private fun deleteExerciseLog(log: ExerciseLog) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.exerciseLogDao().deleteExerciseLog(log)
            Log.d("ExerciseCalendar", "Deleted exercise log: ${log.date} - ${log.details}")
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ExerciseCalendarActivity, getString(R.string.exercise_deleted), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour: Int, selectedMinute: Int ->
            setNotificationTime(selectedHour, selectedMinute)
        }, hour, minute, true).show()
    }

    private fun setNotificationTime(hour: Int, minute: Int) {
        val sharedPreferences = getSharedPreferences("ExercisePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("notification_hour", hour)
        editor.putInt("notification_minute", minute)
        editor.apply()

        Log.d("ExerciseCalendar", "Notification time set to $hour:$minute")
        Toast.makeText(this, getString(R.string.notification_time_set_to, hour, minute), Toast.LENGTH_SHORT).show()
    }

    private fun showErrorPopup(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
