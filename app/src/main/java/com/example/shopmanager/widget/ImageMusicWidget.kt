package com.example.shopmanager.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.action.actionStartService
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import com.example.shopmanager.R
import androidx.core.net.toUri
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.text.Text

val WIDGET_IMAGES = listOf(
    R.drawable.apple,
    R.drawable.chair,
    R.drawable.place
)

class ImageMusicWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { WidgetContent() }
    }
}

@Composable
fun WidgetContent() {
    val prefs = currentState<Preferences>()
    val imageIndex = prefs[intPreferencesKey("image")] ?: 0
    val detected = prefs[stringPreferencesKey("detected")] ?: "Not detected"

    Column(
        modifier = GlanceModifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Detected: $detected"
        )

        Button(
            text = "Detect object",
            onClick = actionRunCallback<DetectObjectAction>()
        )

        Image(
            provider = ImageProvider(WIDGET_IMAGES[imageIndex.coerceIn(WIDGET_IMAGES.indices)]),
            contentDescription = "Widget image",
            modifier = GlanceModifier.height(120.dp)
        )

        Button(
            text = "Next image",
            onClick = actionRunCallback<NextImageAction>()
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        Row(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                text = "Play",
                onClick = actionStartService(
                    Intent().setClassName(
                        "com.example.shopmanager",
                        "com.example.shopmanager.service.MusicService"
                    ).setAction("PLAY")
                )
            )

            Button(
                text = "Stop",
                onClick = actionStartService(
                    Intent().setClassName(
                        "com.example.shopmanager",
                        "com.example.shopmanager.service.MusicService"
                    ).setAction("STOP")
                )
            )

            Button(
                text = "Next",
                onClick = actionStartService(
                    Intent().setClassName(
                        "com.example.shopmanager",
                        "com.example.shopmanager.service.MusicService"
                    ).setAction("NEXT")
                )
            )

            Button(
                text = "Open website",
                onClick = actionStartActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://google.com/search?q=$detected".toUri()
                    )
                )
            )
        }
    }
}