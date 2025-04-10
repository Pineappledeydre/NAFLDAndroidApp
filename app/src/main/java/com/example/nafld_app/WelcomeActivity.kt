package com.example.nafld_app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.nafld_app.databinding.ActivityWelcomeBinding
import java.util.Locale
import java.util.UUID

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(AppSettings.currentLanguage)

        // Initialize the view binding
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get or create a unique patientId
        val patientId = getOrCreatePatientId()
        Log.d("WelcomeActivity", "Assigned patientId: $patientId")

        // Store patientId in SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("patientId", patientId)
        editor.apply()

        // Set up the enter button to go to the main activity
        binding.enterButton.setOnClickListener {
            // Continue with the login process
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun getOrCreatePatientId(): Int {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        var patientId = sharedPreferences.getInt("patientId", -1)

        if (patientId == -1) {  // No patientId found, generate one
            patientId = UUID.randomUUID().hashCode()
            val editor = sharedPreferences.edit()
            editor.putInt("patientId", patientId)
            editor.apply()
            Log.d("WelcomeActivity", "Generated new patientId: $patientId")
        } else {
            Log.d("WelcomeActivity", "Found existing patientId: $patientId")
        }

        return patientId
    }
}
