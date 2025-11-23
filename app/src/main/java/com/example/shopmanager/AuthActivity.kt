package com.example.shopmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.shopmanager.ui.theme.ShopManagerTheme
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        setContent {
            ShopManagerTheme {
                val preferences = remember { PreferencesManager(this) }
                val currentFontSize = remember { mutableStateOf(preferences.getFontSize("fontSize")) }
                val currentButtonColor = remember { mutableStateOf(preferences.getButtonColor("buttonColor")) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting4(
                        auth,
                        modifier = Modifier.padding(innerPadding),
                        currentFontSize.value,
                        currentButtonColor.value
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting4(auth: FirebaseAuth, modifier: Modifier = Modifier,fontSize: Float, buttonColor : String) {
    val context = LocalContext.current
    var inputTextLogin by remember { mutableStateOf("") }
    var inputTextPass by remember { mutableStateOf("") }

    Column( modifier =
        Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        TextField(
            value = inputTextLogin,
            onValueChange = {
                inputTextLogin = it
            },
            label = {
                Text("Login",
                    fontSize = fontSize.sp)
            }
        )
        Spacer(modifier = Modifier.height(50.dp))
        TextField(
            value = inputTextPass,
            onValueChange = {
                inputTextPass = it
            },
            label = {
                Text("Pass",
                    fontSize = fontSize.sp)
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = {
                auth.createUserWithEmailAndPassword(
                    inputTextLogin,
                    inputTextPass
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Zarejestrowano pomyślnie.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Błąd rejestracji.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(buttonColor.toColorInt())
            )
        ) {
            Text("Register",
                fontSize = fontSize.sp)
        }
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = {
                auth.signInWithEmailAndPassword(
                    inputTextLogin,
                    inputTextPass
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    } else {
                        Toast.makeText(context, "Błąd logowania.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(buttonColor.toColorInt())
            )
        ) {
            Text("Log in",
                fontSize = fontSize.sp)
        }
    }
}
