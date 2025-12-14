package com.example.shopmanager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.shopmanager.broadcast.GeofenceHelper
import com.example.shopmanager.database.ShopRepository
import com.example.shopmanager.model.Shop
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ShopsActivity : ComponentActivity() {

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->

        }

    fun requestPermissionFromActivity() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopsScreen(
                askPermission = { requestPermissionFromActivity() }
            )
        }
    }
}

@Composable
fun ShopsScreen(askPermission: () -> Unit) {

    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val repo = remember { ShopRepository(user?.uid ?: "Error") }

    var shops by remember { mutableStateOf(listOf<Shop>()) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("100") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        repo.listenToShops { shops = it }
    }

    LaunchedEffect(shops) {
        if (shops.isNotEmpty()) {
            registerAllShopGeofences(context, shops)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Text("Shops", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        TextField(value = name, onValueChange = { name = it }, label = { Text("Shop Name") })
        TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        TextField(value = radius, onValueChange = { radius = it }, label = { Text("Radius (meters)") })

        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            checkLocationAndRun(context, askPermission) { loc ->
                scope.launch {
                    val shop = Shop(
                        name = name,
                        description = description,
                        radius = radius.toFloatOrNull() ?: 100f,
                        latitude = loc.latitude,
                        longitude = loc.longitude
                    )
                    repo.addShop(shop)

                    val geofence = GeofenceHelper(context)
                    val gf = geofence.createGeofence(
                        "${shop.id}:${shop.name}",
                        shop.latitude,
                        shop.longitude,
                        shop.radius
                    )
                    val req = geofence.createRequest(gf)

                    val client = LocationServices.getGeofencingClient(context)

                    try {
                        registerAllShopGeofences(context, listOf(shop))
                        client.addGeofences(req, geofence.geofencePendingIntent())
                    } catch (e: SecurityException) {  }

                    name = ""
                    description = ""
                    radius = "100"
                }
            }
        }) {
            Text("Add shop where I am")
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = {
            context.startActivity(Intent(context, ShopsMapActivity::class.java))
        }) {
            Text("Open map")
        }

        Spacer(Modifier.height(20.dp))

        LazyColumn {
            items(shops) { shop ->
                Card(Modifier.fillMaxWidth().padding(6.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(shop.name)
                        Text(shop.description)
                        Text("Radius: ${shop.radius}m")
                        Text("Lat: ${shop.latitude}, Lon: ${shop.longitude}")
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    repo.deleteShop(shop)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.onError)
                        }
                    }
                }
            }
        }
    }
}

fun checkLocationAndRun(
    context: Context,
    askPermission: () -> Unit,
    onGet: (Location) -> Unit
) {
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) {
        askPermission()
        return
    }

    getSafeLocation(context, onGet)
}

fun getSafeLocation(context: Context, onGet: (Location) -> Unit) {
    try {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { location ->
                if (location != null) onGet(location)
            }
    } catch (_: SecurityException) {
    }
}

fun registerAllShopGeofences(context: Context, shops: List<Shop>) {
    val hasForegroundPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasBackgroundPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasForegroundPermission || !hasBackgroundPermission) {
        return
    }

    val geofenceHelper = GeofenceHelper(context)
    val client = LocationServices.getGeofencingClient(context)

    shops.forEach { shop ->
        val geofence = geofenceHelper.createGeofence(
            "${shop.id}:${shop.name}",
            shop.latitude,
            shop.longitude,
            shop.radius
        )
        val request = geofenceHelper.createRequest(geofence)

        try {
            client.addGeofences(request, geofenceHelper.geofencePendingIntent())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
