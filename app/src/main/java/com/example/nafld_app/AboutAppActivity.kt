package com.example.nafld_app

import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class AboutAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale(AppSettings.currentLanguage)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)

        // Set up the close button
        val closeButton = findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            finish()
        }

        // Set up the about content based on the current language
        val aboutContent = findViewById<TextView>(R.id.aboutContent)
        val currentLanguage = AppSettings.currentLanguage

        val aboutInfo = if (currentLanguage == "en") {
            """
    <b>Welcome to the NAFLD Management App!</b><br><br>
    Designed for patients with Non-Alcoholic Fatty Liver Disease (NAFLD), this app helps you manage your condition with these features:<br><br>
    <ul>
        <li><b>Patient Survey:</b> Regular health status checks.</li>
        <li><b>Prediction History:</b> Track your health progress.</li>
        <li><b>Medicine and Trigger Food History:</b> Monitor medications and food impacts.</li>
        <li><b>Diet and Exercise Recommendations:</b> Personalized plans just for you.</li>
        <li><b>Save Your Data:</b> Easily access your health info on your phone.</li>
    </ul>
    Remember to <b>check your health daily</b> and log <b>all medications and trigger foods</b> for accurate recommendations. Stay proactive in managing your health!
    """.trimIndent()
        } else {
            """
    <b>Добро пожаловать в приложение для управления НАЖБП!</b><br><br>
    Это приложение помогает пациентам с НАЖБП управлять своим состоянием с помощью следующих функций:<br><br>
    <ul>
        <li><b>Опросник для пациентов:</b> Проверяйте здоровье.</li>
        <li><b>История прогнозов:</b> Отслеживайте прогресс.</li>
        <li><b>История лекарств и  продуктов-триггеров:</b> Контролируйте лечение и влияние продуктов.</li>
        <li><b>Рекомендации по питанию и упражнениям:</b> Персонализированные м постройтепланы.</li>
        <li><b>Сохранение данных:</b> Легкий доступ к информации и возможность поделиться ей.</li>
    </ul>
    Не забудьте <b>ежедневно контролировать здоровье</b> и <b>регистрировать все</b> для точных рекомендаций. Будьте проактивны в контроле своего здоровья!
    """.trimIndent()
        }

        aboutContent.text = Html.fromHtml(aboutInfo, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = android.content.res.Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
