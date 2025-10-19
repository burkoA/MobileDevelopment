package com.example.shopmanager

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.shopmanager.database.DBHandler
import com.example.shopmanager.model.Product
import com.example.shopmanager.provider.ProductProvider
import com.example.shopmanager.ui.theme.ui.theme.ShopManagerTheme

class ShoppingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val preferences = remember { PreferencesManager(this) }
                    val currentFontSize = remember { mutableStateOf(preferences.getFontSize("fontSize")) }
                    val currentButtonColor = remember { mutableStateOf(preferences.getButtonColor("buttonColor")) }
                    Greeting3(
                        fontSize = currentFontSize.value,
                        buttonColor = currentButtonColor.value
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting3(fontSize: Float, buttonColor : String) {
    val context = LocalContext.current
    val dbHelper = DBHandler(context)
    val productList = remember { dbHelper.getAllProducts() }
    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("") }
    var isBought by remember { mutableStateOf(false) }
    val cursor = context.contentResolver.query(
        ProductProvider.CONTENT_URI,
        null,
        null,
        null,
        null
    )

    val products = mutableListOf<Product>()
    cursor?.use {
        while (it.moveToNext()) {
            val id = it.getInt(it.getColumnIndexOrThrow("id"))
            val name = it.getString(it.getColumnIndexOrThrow("productName"))
            val price = it.getDouble(it.getColumnIndexOrThrow("price"))
            val count = it.getInt(it.getColumnIndexOrThrow("count"))
            val isBought = it.getInt(it.getColumnIndexOrThrow("isBought")) == 1

            products.add(Product(id, name, price, count, isBought))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(Modifier.height(30.dp))

        Text("Write the needed product",
            fontSize = fontSize.sp)

        Column(horizontalAlignment = Alignment.Start)
        {
            TextField(
                onValueChange = {
                    productName = it
                },
                value = productName,
                label = {
                    Text("Provide product name",
                        fontSize = fontSize.sp)
                }
            )
            TextField(
                onValueChange = {
                    price = it
                },
                value = price,
                label = {
                    Text("Provide product price",
                        fontSize = fontSize.sp)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            TextField(
                onValueChange = {
                    count = it
                },
                value = count,
                label = {
                    Text("Provide product count",
                        fontSize = fontSize.sp)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row{
                Checkbox(
                    checked = isBought,
                    onCheckedChange = { isBought = it }
                )
                Text(
                    text = "Bought",
                    fontSize = fontSize.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 10.dp)
                )
            }

            Row {
                Button(
                    onClick = {
                        val product = ContentValues().apply {
                            put("productName", productName)
                            put("price", price)
                            put("count", count)
                            put("isBought", isBought)
                        }

                        context.contentResolver.insert(ProductProvider.CONTENT_URI, product)

                        (context as? Activity)?.recreate()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color(buttonColor.toColorInt())
                    )
                ) {
                    Text(
                        "Submit",
                        fontSize = fontSize.sp
                    )
                }
            }
        }

        Text(
            "Product List",
            fontSize = fontSize.sp
        )

        LazyColumn(
            modifier = Modifier.heightIn(max = 400.dp)
                .fillMaxWidth()
        )
        {
            if (productList.isEmpty()) {
                item {
                    Text(
                        "List is empty :) !",
                        fontSize = fontSize.sp
                    )
                }
            } else {
                items(productList, key = {it.id ?: 0}) { product ->
                    var isEditing by remember { mutableStateOf(false) }
                    var name by remember { mutableStateOf(product.productName) }
                    var priceText by remember { mutableStateOf(product.price.toString()) }
                    var countText by remember { mutableStateOf(product.count.toString()) }
                    var bought by remember { mutableStateOf(product.isBought) }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .padding(horizontal = 8.dp),
                        shadowElevation = 3.dp,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (isEditing) {
                                TextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Product name", fontSize = fontSize.sp) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                TextField(
                                    value = priceText,
                                    onValueChange = { priceText = it },
                                    label = { Text("Price", fontSize = fontSize.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                TextField(
                                    value = countText,
                                    onValueChange = { countText = it },
                                    label = { Text("Count", fontSize = fontSize.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Checkbox(
                                        checked = bought,
                                        onCheckedChange = { bought = it }
                                    )
                                    Text("Bought", fontSize = fontSize.sp)
                                }

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(onClick = {
                                        val updated = Product(
                                            id = product.id,
                                            productName = name,
                                            price = priceText.toDoubleOrNull() ?: 0.0,
                                            count = countText.toIntOrNull() ?: 0,
                                            isBought = bought
                                        )
                                        dbHelper.updateProduct(updated)
                                        (context as? Activity)?.recreate()
                                    }, colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(buttonColor.toColorInt())
                                    )) {
                                        Text("Save", fontSize = fontSize.sp)
                                    }
                                    Button(
                                        onClick = { isEditing = false },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(buttonColor.toColorInt())
                                        )
                                    ) {
                                        Text("Cancel", fontSize = fontSize.sp)
                                    }
                                }
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(name, fontSize = fontSize.sp)
                                        Text("Price: $priceText", fontSize = (fontSize - 2).sp)
                                        Text("Count: $countText", fontSize = (fontSize - 2).sp)
                                    }
                                    Text(
                                        if (bought) "+" else "-",
                                        color = if (bought) Color.Green else Color.Red,
                                        fontSize = fontSize.sp
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(onClick = { isEditing = true },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(buttonColor.toColorInt())
                                        )) {
                                        Text("Edit", fontSize = fontSize.sp)
                                    }
                                    Button(
                                        onClick = {
                                            product.id?.let { dbHelper.deleteProduct(it) }
                                            (context as? Activity)?.recreate()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                    ) {
                                        Text("Delete", fontSize = fontSize.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Column {
            Button(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(buttonColor.toColorInt())
                )) {
                Text("Return to Home Page",
                    fontSize = fontSize.sp)
            }

            Button(onClick = {
                val intent = Intent(context, SettingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(buttonColor.toColorInt())
                )) {
                Text("Return to Setting Page",
                    fontSize = fontSize.sp)
            }
        }
    }
}