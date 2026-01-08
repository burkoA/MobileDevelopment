package com.example.shopmanager.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import com.example.shopmanager.R

class DetectObjectAction : ActionCallback {

    private val detector = ObjectDetectionHelper()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Safe update state
        updateAppWidgetState(context, glanceId) { prefs ->

            val imageIndex = prefs[intPreferencesKey("image")] ?: 0
            val safeIndex = imageIndex.coerceIn(WIDGET_IMAGES.indices)

            val bitmap = BitmapFactory.decodeResource(
                context.resources,
                WIDGET_IMAGES[safeIndex],
                BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
            ) ?: run {
                prefs[stringPreferencesKey("detected")] = "Image decode failed"
                return@updateAppWidgetState
            }

            val result = try {
                // ObjectDetectionHelper().detect must return a non-null String
                detector.detect(bitmap)
            } catch (e: Exception) {
                "Detection failed"
            }

            prefs[stringPreferencesKey("detected")] = result
        }

        // Refresh the widget
        ImageMusicWidget().update(context, glanceId)
    }
}