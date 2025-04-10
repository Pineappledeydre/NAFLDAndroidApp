package com.example.nafld_app

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.nafld_app.databinding.ActivityAddPredictionBinding

class UpdatePredictionList(private val binding: ActivityAddPredictionBinding) {

    fun updatePredictionList() {
        binding.predictionListLayout.removeAllViews()

        val predictionEntries = PredictionManager.getPredictionEntries()
        if (predictionEntries.isEmpty()) {
            showToast(binding.root.context.getString(R.string.no_prediction_entries))
            return
        }

        predictionEntries.forEach { entry ->
            val entryView = createPredictionEntryView(entry)
            binding.predictionListLayout.addView(entryView)
        }
    }

    private fun createPredictionEntryView(entry: PredictionEntry): View {
        val entryView = LayoutInflater.from(binding.root.context).inflate(R.layout.item_prediction_entry, null)
        entryView.findViewById<TextView>(R.id.predictionDate).text = entry.date
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
            ${binding.root.context.getString(R.string.date)}: ${entry.date}
            ${binding.root.context.getString(R.string.gender)}: ${if (entry.gender == 1) binding.root.context.getString(R.string.male) else binding.root.context.getString(R.string.female)}
            ${binding.root.context.getString(R.string.ast)}: ${entry.ast}
            ${binding.root.context.getString(R.string.ggtp)}: ${entry.ggtp}
            ${binding.root.context.getString(R.string.lpvp)}: ${entry.lpvp}
            ${binding.root.context.getString(R.string.trigl)}: ${entry.trigl}
            ${binding.root.context.getString(R.string.bilirr)}: ${entry.bilirr}
            ${binding.root.context.getString(R.string.crb)}: ${entry.crb}
            ${binding.root.context.getString(R.string.probability)}: ${String.format("%.2f", entry.probability * 100)}%
        """.trimIndent()

        AlertDialog.Builder(binding.root.context)
            .setTitle(binding.root.context.getString(R.string.prediction_details))
            .setMessage(details)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deletePredictionEntry(entry: PredictionEntry) {
        PredictionManager.deletePredictionEntry(entry)
        updatePredictionList()
    }

    private fun showToast(message: String) {
        Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
    }
}
