package com.example.shopmanager

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.shopmanager.database.ShopRepository
import com.example.shopmanager.model.Shop
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.plugin.animation.MapAnimationOptions

class ShopsMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MapScreen()
        }
    }
}

@Composable
fun MapScreen() {

    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val repo = remember { ShopRepository(user?.uid ?: "Error") }

    var shops by remember { mutableStateOf(listOf<Shop>()) }
    
    val viewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(14.0)
            center(Point.fromLngLat(20.92, 52.20))
        }
    }

    val markerBitmap by remember(context) {
        derivedStateOf {
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)
        }
    }

    LaunchedEffect(Unit) {
        repo.listenToShops { shops = it }
    }

    LaunchedEffect(shops) {
        if (shops.isNotEmpty()) {
            val firstShop = shops.first()
            viewportState.easeTo(
                com.mapbox.maps.CameraOptions.Builder()
                    .center(Point.fromLngLat(firstShop.longitude, firstShop.latitude))
                    .zoom(14.0)
                    .build(),
                MapAnimationOptions.Builder()
                    .duration(1000L) // Animation duration in milliseconds
                    .build()
            )
        }
    }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = viewportState,
        style = {
            MapboxStandardStyle()
        }
    ) {
        shops.forEach { shop ->
            val point = Point.fromLngLat(shop.longitude, shop.latitude)

            CircleAnnotation(
                point = point
            ) {
                circleRadius = 8.0
                circleColor = Color.Red
                circleOpacity = 0.9
                circleStrokeColor = Color.White
                circleStrokeWidth = 2.0
            }

            PointAnnotation(
                point = point
            ) {
                textField = shop.name
                textSize = 12.0
                textColor = Color.Black
                textOffset = listOf(0.0, -2.0)
                iconOpacity = 0.0
            }
        }
    }
}
