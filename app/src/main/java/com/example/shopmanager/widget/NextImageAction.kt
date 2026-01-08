package com.example.shopmanager.widget

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState

class NextImageAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val key = intPreferencesKey("image")
            val current = prefs[key] ?: 0
            prefs[key] = (current + 1) % 3
        }

        ImageMusicWidget().update(context, glanceId)
    }
}