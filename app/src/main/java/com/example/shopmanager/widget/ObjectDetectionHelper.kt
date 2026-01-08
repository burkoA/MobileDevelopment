package com.example.shopmanager.widget

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.tasks.await

class ObjectDetectionHelper {
    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
        .enableClassification()
        .build()

    private val detector = ObjectDetection.getClient(options)

    suspend fun detect(bitmap: Bitmap): String {
        val image = InputImage.fromBitmap(bitmap, 0)

        val objects = detector.process(image).await()

        if (objects.isEmpty()) return "No object detected"

        val labels = objects.flatMap { it.labels }

        return if (labels.isNotEmpty()) {
            labels.joinToString { "${it.text} (${String.format("%.2f", it.confidence)})" }
        } else {
            "Object detected (no label)"
        }
    }
}