package com.example.shopmanager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.shopmanager.ui.theme.ShopManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopManagerTheme {
                Surface (modifier = Modifier.fillMaxSize()) {
                    val preferences = remember { PreferencesManager(this) }
                    val currentFontSize = remember { mutableStateOf(preferences.getFontSize("fontSize")) }
                    val currentButtonColor = remember { mutableStateOf(preferences.getButtonColor("buttonColor")) }
                    Greeting(
                        fontSize = currentFontSize.value,
                        buttonColor = currentButtonColor.value
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(fontSize: Float, buttonColor: String) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        // Intro

        Text(
            text = "Welcome to Shop Manager!",
            fontSize = fontSize.sp
        )
        Spacer(modifier = Modifier.height(100.dp))

        // List Button

        Button(onClick = {
            val intent = Intent(context, ShoppingActivity::class.java)
            context.startActivity(intent)
        }, colors = ButtonDefaults.buttonColors(
            containerColor = Color(buttonColor.toColorInt())
        )) {
            Text("Shop List",
                fontSize = fontSize.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Settings Button

        Button(onClick = {
            val intent = Intent(context, SettingActivity::class.java)
            context.startActivity(intent)
        },colors = ButtonDefaults.buttonColors(
            containerColor = Color(buttonColor.toColorInt())
        )) {
            Text("Settings",
                fontSize = fontSize.sp)
        }
    }
}