package com.example.shopmanager.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.shopmanager.model.Product

class DBHandler(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "shop_manager.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "products"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "productName"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_COUNT = "count"
        private const val COLUMN_IS_BOUGHT = "isBought"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT,
            $COLUMN_PRICE REAL,
            $COLUMN_COUNT INTEGER,
            $COLUMN_IS_BOUGHT INTEGER
            )
            """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertProduct(product: Product) : Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, product.productName)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_COUNT, product.count)
            put(COLUMN_IS_BOUGHT, if(product.isBought) 1 else 0)
        }

        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getAllProducts(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    productName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                    count = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT)),
                    isBought = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_BOUGHT)) == 1
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return productList
    }

    fun deleteProduct(id: Int): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    fun updateProduct(product: Product): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, product.productName)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_COUNT, product.count)
            put(COLUMN_IS_BOUGHT, if (product.isBought) 1 else 0)
        }
        val rowsUpdated = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(product.id.toString()))
        db.close()
        return rowsUpdated
    }
}