package com.example.nafld_app

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.nafld_app.databinding.ActivityMainBinding
import java.util.Calendar
import android.content.res.Configuration
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import android.widget.LinearLayout
import android.util.Log
import java.util.Locale

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SystemInfoUtil.logSystemInfo();

        // Optionally hide the action bar title if the title TextView is being used
        supportActionBar?.title = ""

        // Set up buttons in the UI
        setupButtons()

        // Create the notification channel for daily exercise reminders
        createNotificationChannel()

        // Schedule the daily exercise reminder notification
        scheduleDailyReminder()

        // Log SharedPreferences to verify data
        logSharedPreferences()
    }

    override fun onResume() {
        super.onResume()
        // Reapply locale settings when the activity resumes
        setAppLocale(AppSettings.currentLanguage)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reapply locale settings when the configuration changes
        setAppLocale(AppSettings.currentLanguage)
    }

    private fun setAppLocale(languageCode: String) {
        if (AppSettings.currentLanguage != languageCode) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)

            AppSettings.currentLanguage = languageCode

            recreate()
        }
    }

    private fun logSharedPreferences() {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        if (allEntries.isEmpty()) {
            Log.d("SharedPreferences", "No data found in SharedPreferences")
        } else {
            for ((key, value) in allEntries) {
                Log.d("SharedPreferences", "$key: $value")
            }
        }
    }

    private fun setupButtons() {
        val buttonTexts = arrayOf(
            getString(R.string.patient_survey),
            getString(R.string.add_medicine),
            getString(R.string.add_prediction),
            getString(R.string.add_trigger_food),
            getString(R.string.diet),
            getString(R.string.exercise),
            getString(R.string.save)
        )

        val buttonCallbacks = arrayOf(
            { showPatientSurveyWindow() },
            { showMedicineEntryWindow() },
            { showPredictionEntryWindow() },
            { showTriggerFoodEntryWindow() },
            { showDietRecommendations() },
            { showExerciseWindow() },
            { saveInfo() }
        )

        for (i in buttonTexts.indices) {
            val button = createButton(buttonTexts[i], buttonCallbacks[i])
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 15, 0, 15)
            }
            button.layoutParams = layoutParams
            binding.buttonLayout.addView(button)
        }

        val smallButtonTexts = arrayOf(
            getString(R.string.about_app),
            getString(R.string.language)
        )

        val smallButtonCallbacks = arrayOf(
            { showAboutApp() },
            { showLanguageSelectionDialog() }
        )

        for (i in smallButtonTexts.indices) {
            val button = createButton(smallButtonTexts[i], smallButtonCallbacks[i])
            val layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(10, 0, 10, 0)
            }
            button.layoutParams = layoutParams
            binding.smallButtonLayout.addView(button)
        }
    }

    private fun createButton(text: String, callback: () -> Unit): CustomButton {
        return CustomButton(this).apply {
            this.text = text
            this.textSize = 18f
            this.setPadding(20, 20, 20, 20)
            this.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor))
            this.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            this.setOnClickListener { callback() }
            this.elevation = 10f
            this.textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
            this.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(20, 20, 20, 20)
            }
            this.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        }
    }

    private fun showPatientSurveyWindow() {
        val intent = Intent(this, SurveyActivity::class.java)
        startActivity(intent)
    }

    private fun showMedicineEntryWindow() {
        val intent = Intent(this, AddMedicineActivity::class.java)
        startActivity(intent)
    }

    private fun showPredictionEntryWindow() {
        val intent = Intent(this, AddPredictionActivity::class.java)
        startActivity(intent)
    }

    private fun showTriggerFoodEntryWindow() {
        val intent = Intent(this, TriggerFoodActivity::class.java)
        startActivity(intent)
    }

    private fun showDietRecommendations() {
        val intent = Intent(this, CalorieCalculatorActivity::class.java)
        startActivity(intent)
    }

    private fun showExerciseWindow() {
        val intent = Intent(this, ExerciseCalendarActivity::class.java)
        startActivity(intent)
    }

    private fun saveInfo() {
        val intent = Intent(this, SaveInfoActivity::class.java)
        startActivity(intent)
    }

    private fun showAboutApp() {
        val intent = Intent(this, AboutAppActivity::class.java)
        startActivity(intent)
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "Русский")
        val selectedLanguage = AppSettings.currentLanguage

        val checkedItem = if (selectedLanguage == "ru") 1 else 0

        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle(R.string.language)
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                when (which) {
                    0 -> setAppLocale("en")
                    1 -> setAppLocale("ru")
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = getString(R.string.exercise_channel_name)
            val descriptionText = getString(R.string.exercise_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("exercise_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleDailyReminder() {
        val intent = Intent(applicationContext, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
