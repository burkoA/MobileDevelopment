package com.example.shopmanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.shopmanager.database.DBHandler
import androidx.core.net.toUri

class ProductProvider : ContentProvider() {
    companion object {
        const val AUTHORITY = "com.example.shopmanager.provider"
        const val TABLE_NAME = "products"
        val CONTENT_URI: Uri = "content://$AUTHORITY/$TABLE_NAME".toUri()

        private const val PRODUCTS = 1
        private const val PRODUCT_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, TABLE_NAME, PRODUCTS)
            addURI(AUTHORITY, "$TABLE_NAME/#", PRODUCT_ID)
        }
    }

        private lateinit var dbHelper: DBHandler

        override fun onCreate(): Boolean {
            dbHelper = DBHandler(context!!)
            return true
        }

        override fun query(
            uri: Uri,
            projection: Array<String>?,
            selection: String?,
            selectionArgs: Array<String>?,
            sortOrder: String?
        ): Cursor? {
            val db = dbHelper.readableDatabase
            val cursor = when (uriMatcher.match(uri)) {
                PRODUCTS -> db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
                PRODUCT_ID -> {
                    val id = ContentUris.parseId(uri)
                    db.query(TABLE_NAME, projection, "id = ?", arrayOf(id.toString()), null, null, sortOrder)
                }
                else -> throw IllegalArgumentException("Unknown URI: $uri")
            }
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }

        override fun getType(uri: Uri): String? = when (uriMatcher.match(uri)) {
            PRODUCTS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.$TABLE_NAME"
            PRODUCT_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.$TABLE_NAME"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        override fun insert(uri: Uri, values: ContentValues?): Uri? {
            val db = dbHelper.writableDatabase
            val id = db.insert(TABLE_NAME, null, values)
            if (id > 0) {
                val resultUri = ContentUris.withAppendedId(CONTENT_URI, id)
                context!!.contentResolver.notifyChange(resultUri, null)
                return resultUri
            }
            throw IllegalArgumentException("Failed to insert row into $uri")
        }

        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
            val db = dbHelper.writableDatabase
            val rowsDeleted = when (uriMatcher.match(uri)) {
                PRODUCTS -> db.delete(TABLE_NAME, selection, selectionArgs)
                PRODUCT_ID -> {
                    val id = ContentUris.parseId(uri)
                    db.delete(TABLE_NAME, "id = ?", arrayOf(id.toString()))
                }
                else -> throw IllegalArgumentException("Unknown URI: $uri")
            }
            if (rowsDeleted > 0) {
                context!!.contentResolver.notifyChange(uri, null)
            }
            return rowsDeleted
        }

        override fun update(
            uri: Uri,
            values: ContentValues?,
            selection: String?,
            selectionArgs: Array<String>?
        ): Int {
            val db = dbHelper.writableDatabase
            val rowsUpdated = when (uriMatcher.match(uri)) {
                PRODUCTS -> db.update(TABLE_NAME, values, selection, selectionArgs)
                PRODUCT_ID -> {
                    val id = ContentUris.parseId(uri)
                    db.update(TABLE_NAME, values, "id = ?", arrayOf(id.toString()))
                }
                else -> throw IllegalArgumentException("Unknown URI: $uri")
            }
            if (rowsUpdated > 0) {
                context!!.contentResolver.notifyChange(uri, null)
            }
            return rowsUpdated
        }
}