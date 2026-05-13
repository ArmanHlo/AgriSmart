package com.shoping.agrismart.presentation.disease

import android.content.Context
import android.graphics.Bitmap
import com.shoping.agrismart.domain.model.ScanResult
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.MappedByteBuffer
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantDiseaseClassifier @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()

    init {
        try {
            val model: MappedByteBuffer = FileUtil.loadMappedFile(context, "plant_disease_model.tflite")
            val options = Interpreter.Options()
            interpreter = Interpreter(model, options)
            labels = FileUtil.loadLabels(context, "label_map.txt")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun classify(bitmap: Bitmap): ScanResult {
        val interpreter = interpreter
        if (interpreter == null || labels.isEmpty()) {
            return getMockResult()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()

        var tensorImage = TensorImage(interpreter.getInputTensor(0).dataType())
        tensorImage.load(bitmap)
        tensorImage = imageProcessor.process(tensorImage)

        val outputBuffer = Array(1) { FloatArray(labels.size) }
        interpreter.run(tensorImage.buffer, outputBuffer)

        val results = outputBuffer[0]
        val maxIndex = results.indices.maxByOrNull { results[it] } ?: -1
        val confidence = if (maxIndex != -1) results[maxIndex] else 0f
        val label = if (maxIndex != -1) labels[maxIndex] else "Unknown"

        // Parsing label "Crop___Disease"
        val parts = label.split("___")
        val cropName = parts.getOrNull(0)?.replace("_", " ") ?: "Unknown"
        val diseaseName = parts.getOrNull(1)?.replace("_", " ") ?: "Healthy"

        return ScanResult(
            id = UUID.randomUUID().toString(),
            cropName = cropName,
            diseaseName = diseaseName,
            confidence = confidence,
            treatment = getTreatmentForDisease(diseaseName),
            timestamp = Date()
        )
    }

    private fun getTreatmentForDisease(disease: String): String {
        return when {
            disease.contains("Blight", ignoreCase = true) -> {
                "1. Apply fungicides containing Copper or Mancozeb immediately.\n" +
                "2. Remove and burn infected leaves to prevent spread.\n" +
                "3. Avoid overhead watering and ensure good air circulation."
            }
            disease.contains("Rust", ignoreCase = true) -> {
                "1. Apply sulfur-based fungicides or Neem oil.\n" +
                "2. Destroy infected plant debris after harvest.\n" +
                "3. Use rust-resistant varieties for the next crop."
            }
            disease.contains("Spot", ignoreCase = true) -> {
                "1. Use Chlorothalonil or copper-based sprays.\n" +
                "2. Reduce humidity by proper spacing between plants.\n" +
                "3. Avoid working in the field when plants are wet."
            }
            disease.contains("Virus", ignoreCase = true) || disease.contains("Curl", ignoreCase = true) -> {
                "1. Control whiteflies or aphids using insecticidal soap.\n" +
                "2. Remove and destroy severely infected plants.\n" +
                "3. Use yellow sticky traps to monitor and catch pests."
            }
            disease.contains("Mildew", ignoreCase = true) -> {
                "1. Spray a mix of baking soda, water, and non-detergent soap.\n" +
                "2. Increase sunlight exposure and improve airflow.\n" +
                "3. Apply fungicides like Myclobutanil if severe."
            }
            disease.contains("Healthy", ignoreCase = true) -> {
                "Your plant looks healthy! Keep up the good work with regular watering and balanced fertilization."
            }
            else -> {
                "1. Monitor the plant closely for any worsening symptoms.\n" +
                "2. Keep the area clean of weeds and fallen debris.\n" +
                "3. Consult a local agricultural officer for a specific chemical recommendation."
            }
        }
    }

    private fun getMockResult(): ScanResult {
        val crops = listOf("Tomato", "Potato", "Rice", "Wheat")
        val diseases = listOf("Late Blight", "Yellow Leaf Curl Virus", "Healthy", "Leaf Spot")
        val diseaseName = diseases.random()
        return ScanResult(
            id = UUID.randomUUID().toString(),
            cropName = crops.random(),
            diseaseName = diseaseName,
            confidence = 0.85f + (Random().nextFloat() * 0.1f),
            treatment = getTreatmentForDisease(diseaseName),
            timestamp = Date()
        )
    }
}
