import com.example.nafld_app.PredictionEntry

object PredictionManager {
    private val predictionList = mutableListOf<PredictionEntry>()

    fun addPredictionEntry(entry: PredictionEntry) {
        predictionList.add(entry)
    }

    fun deletePredictionEntry(entry: PredictionEntry) {
        predictionList.remove(entry)
    }

    fun getPredictionEntries(): List<PredictionEntry> {
        return predictionList
    }
}
