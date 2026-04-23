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
            return ScanResult(
                id = UUID.randomUUID().toString(),
                cropName = "Error",
                diseaseName = "Model not loaded",
                confidence = 0f,
                treatment = "Please ensure plant_disease_model.tflite and label_map.txt are in assets.",
                timestamp = Date()
            )
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
            disease.contains("Blight", ignoreCase = true) -> "Use fungicides containing copper or mancozeb. Remove infected plants."
            disease.contains("Rust", ignoreCase = true) -> "Apply sulfur-based fungicides. Ensure good air circulation."
            disease.contains("Spot", ignoreCase = true) -> "Reduce overhead watering. Use neem oil or appropriate fungicides."
            disease.contains("Healthy", ignoreCase = true) -> "Your plant looks healthy! Keep up the good work with regular care."
            else -> "Consult a local agricultural expert for a detailed treatment plan."
        }
    }
}
