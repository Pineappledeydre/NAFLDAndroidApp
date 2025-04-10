package com.example.nafld_app

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class DietRecommendationsActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(AppSettings.currentLanguage)
        setContentView(R.layout.activity_diet_recommendation)

        // Retrieve the caloric needs and BMI passed from the previous activity
        val caloricNeeds = intent.getIntExtra("CALORIC_NEEDS", 0)
        val bmi = intent.getFloatExtra("BMI", 0f)

        // Display the caloric needs and BMI with formatted strings
        val caloricNeedsTextView = findViewById<TextView>(R.id.caloricNeedsTextView)
        val bmiTextView = findViewById<TextView>(R.id.bmiTextView)
        val menuTextView = findViewById<TextView>(R.id.menuTextView)

        caloricNeedsTextView.text = getString(R.string.caloric_needs, caloricNeeds)

        // Determine BMI category
        val bmiCategory = when {
            bmi < 18.5 -> getString(R.string.bmi_underweight)
            bmi in 18.5..24.9 -> getString(R.string.bmi_normal)
            bmi in 25.0..29.9 -> getString(R.string.bmi_overweight)
            else -> getString(R.string.bmi_obesity)
        }

        // Display the BMI value and category in a single line
        bmiTextView.text = getString(R.string.bmi_with_category, bmi, bmiCategory)


        // Generate and display a sample menu based on caloric needs
        val menu = generateSampleMenu(caloricNeeds)
        menuTextView.text = formatMenu(menu)

        // Set up close button to finish the activity
        val closeButton = findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            finish()  // Close the activity
        }
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun generateSampleMenu(caloricNeeds: Int): Map<String, List<String>> {
        // Define sample menus for different caloric ranges

        // Low Calorie Menu
        val lowCalorieMenu = mapOf(
            getString(R.string.breakfast) to listOf(
                getString(R.string.yogurt_granola_berries),   // Yogurt with granola and berries
                getString(R.string.scrambled_eggs_whole_grain_toast),   // Scrambled eggs with whole-grain toast
                getString(R.string.oatmeal_with_bananas)   // Oatmeal with bananas
            ),
            getString(R.string.lunch) to listOf(
                getString(R.string.lentil_soup_whole_grain_bread),   // Lentil soup with whole-grain bread
                getString(R.string.grilled_chicken_quinoa_salad),   // Grilled chicken and quinoa salad
                getString(R.string.vegetable_stir_fry)   // Vegetable stir-fry
            ),
            getString(R.string.dinner) to listOf(
                getString(R.string.stir_fried_tofu_with_brown_rice),   // Stir-fried tofu with brown rice
                getString(R.string.chicken_breast_with_roasted_sweet_potatoes),   // Chicken breast with roasted sweet potatoes
                getString(R.string.grilled_fish_with_steamed_vegetables)   // Grilled fish with steamed vegetables
            ),
            getString(R.string.snack) to listOf(
                getString(R.string.apple_peanut_butter),   // Apple with peanut butter
                getString(R.string.carrot_sticks_hummus),   // Carrot sticks with hummus
                getString(R.string.cucumber_slices_with_guacamole)   // Cucumber slices with guacamole
            )
        )

        // Medium Calorie Menu
        val mediumCalorieMenu = mapOf(
            getString(R.string.breakfast) to listOf(
                getString(R.string.avocado_on_rye_bread),   // Avocado on rye bread
                getString(R.string.scrambled_eggs_whole_grain_toast),   // Scrambled eggs with whole-grain toast
                getString(R.string.smoothie_with_almond_butter)   // Smoothie with almond butter
            ),
            getString(R.string.lunch) to listOf(
                getString(R.string.turkey_breast_with_steamed_veggies),   // Turkey breast with steamed veggies
                getString(R.string.grilled_chicken_quinoa_salad),   // Grilled chicken and quinoa salad
                getString(R.string.veggie_burger_whole_grain_bun)   // Veggie burger with a whole-grain bun
            ),
            getString(R.string.dinner) to listOf(
                getString(R.string.baked_salmon_spinach),   // Baked salmon with spinach
                getString(R.string.chicken_breast_with_roasted_sweet_potatoes),   // Chicken breast with roasted sweet potatoes
                getString(R.string.shrimp_stir_fry_with_vegetables)   // Shrimp stir-fry with vegetables
            ),
            getString(R.string.snack) to listOf(
                getString(R.string.handful_of_walnuts),   // Handful of walnuts
                getString(R.string.apple_peanut_butter),   // Apple with peanut butter
                getString(R.string.cheese_and_grapes)   // Cheese and grapes
            )
        )

        // High Calorie Menu
        val highCalorieMenu = mapOf(
            getString(R.string.breakfast) to listOf(
                getString(R.string.scrambled_eggs_whole_grain_toast),   // Scrambled eggs with whole-grain toast
                getString(R.string.avocado_on_rye_bread),   // Avocado on rye bread
                getString(R.string.pancakes_with_honey)   // Pancakes with honey
            ),
            getString(R.string.lunch) to listOf(
                getString(R.string.turkey_breast_with_steamed_veggies),   // Turkey breast with steamed veggies
                getString(R.string.lentil_soup_whole_grain_bread),   // Lentil soup with whole-grain bread
                getString(R.string.steak_with_quinoa_and_sweet_potatoes)   // Steak with quinoa and sweet potatoes
            ),
            getString(R.string.dinner) to listOf(
                getString(R.string.baked_salmon_spinach),   // Baked salmon with spinach
                getString(R.string.chicken_breast_with_roasted_sweet_potatoes),   // Chicken breast with roasted sweet potatoes
                getString(R.string.pasta_with_meatballs)   // Pasta with meatballs
            ),
            getString(R.string.snack) to listOf(
                getString(R.string.carrot_sticks_hummus),   // Carrot sticks with hummus
                getString(R.string.handful_of_walnuts),   // Handful of walnuts
                getString(R.string.yogurt_with_mixed_nuts)   // Yogurt with mixed nuts
            )
        )

        // Choose the menu based on caloric needs
        return when {
            caloricNeeds <= 1500 -> lowCalorieMenu
            caloricNeeds in 1501..2500 -> mediumCalorieMenu
            else -> highCalorieMenu
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun formatMenu(menu: Map<String, List<String>>): Spanned {
        val selectDishMessage = getString(R.string.select_dish_message)

        // Format the menu by adding the message and then each meal category
        return Html.fromHtml(
            "<b>$selectDishMessage</b><br><br>" +
                    menu.entries.joinToString("<br><br>") { (meal, items) ->
                        "<b>$meal</b>:<br>" + items.joinToString("<br>") { "- $it" }
                    }, Html.FROM_HTML_MODE_LEGACY
        )
    }
}
