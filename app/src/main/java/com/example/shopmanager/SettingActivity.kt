package com.example.shopmanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.shopmanager.ui.theme.ShopManagerTheme
import androidx.core.graphics.toColorInt

class SettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopManagerTheme {
                Surface (modifier = Modifier.fillMaxSize()) {
                    Greeting2()
                }
            }
        }
    }
}

class PreferencesManager(var context: Context){
    private val sp = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun saveFontSize(key: String, value: Float){
        sp.edit {
            putFloat(key, value)
        }
    }

    fun getFontSize(key: String?): Float{
        return sp.getFloat(key, 16f)
    }

    fun saveButtonColor(key: String, value: String) {
        sp.edit {
            putString(key,value)
        }
    }

    fun getButtonColor(key: String?) : String {
        return sp.getString(key, "#FF6200EE") ?: "#FF6200EE"
    }
}

@Composable
fun Greeting2() {

    val context = LocalContext.current
    val preferences = remember { PreferencesManager(context) }
    var fontSize by remember { mutableStateOf("") }
    val currentFontSize = remember { mutableStateOf(preferences.getFontSize("fontSize")) }
    val currentButtonColor = remember { mutableStateOf(preferences.getButtonColor("buttonColor")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text("Settings",
            fontSize = currentFontSize.value.sp)

        Spacer(Modifier.height(20.dp))

        // Change Font Size

        Row {
            TextField(
                value = fontSize,
                onValueChange = {
                    fontSize = it
                },
                label = {
                    Text("Change font size",
                        fontSize = currentFontSize.value.sp)
                }
            )

            Button(onClick = {
                var size = fontSize.toFloatOrNull();

                if(size != null) {
                    preferences.saveFontSize("fontSize",size)
                    currentFontSize.value = size
                } else {
                    Toast.makeText(context, "Font size must be value!", Toast.LENGTH_LONG)
                        .show()
                }

                (context as? Activity)?.recreate()
            },colors = ButtonDefaults.buttonColors(
                containerColor = Color(currentButtonColor.value.toColorInt())
            )) {
                Text("Submit",
                    fontSize = currentFontSize.value.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Change button background

        Text("Select Button Color:",
            fontSize = currentFontSize.value.sp)

        Row {
            Button(
                onClick = {
                    preferences.saveButtonColor("buttonColor", "#FF4CAF50") // Green
                    currentButtonColor.value = "#FF4CAF50"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    "Green",
                    fontSize = currentFontSize.value.sp
                )
            }

            Button(
                onClick = {
                    preferences.saveButtonColor("buttonColor", "#FFFFFFFF") // White
                    currentButtonColor.value = "#FFFFFFFF"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFFFFF),
                )
            ) {
                Text(
                    "White",
                    fontSize = currentFontSize.value.sp,
                )
            }

            Button(
                onClick = {
                    preferences.saveButtonColor("buttonColor", "#FF2196F3") // Blue
                    currentButtonColor.value = "#FF2196F3"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text(
                    "Blue",
                    fontSize = currentFontSize.value.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Navigation

        Button(onClick = {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }, colors = ButtonDefaults.buttonColors(
            containerColor = Color(currentButtonColor.value.toColorInt())
        ))
        {
            Text("Return to Home Page",
                fontSize = currentFontSize.value.sp)
        }

        Spacer(Modifier.height(10.dp))

        Button(onClick = {

        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(currentButtonColor.value.toColorInt())
            )) {
            Text("Return to List Page",
                fontSize = currentFontSize.value.sp)
        }
    }
}

