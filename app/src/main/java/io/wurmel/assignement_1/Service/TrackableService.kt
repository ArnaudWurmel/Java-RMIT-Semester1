package io.wurmel.assignement_1.Service

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import io.wurmel.assignement_1.Model.Trackable
import io.wurmel.assignement_1.Model.Tracking
import io.wurmel.assignement_1.R
import java.io.FileReader


/**
 * Created by wurmel_a on 19/7/18.
 */

class   TrackableService(context: Context): SQLiteOpenHelper(context, TrackableService.DB_NAME, null, TrackableService.DB_VERSION) {

    private var context: Context
    private var followedTrackings = ArrayList<Tracking>()
    private var trackablesList = ArrayList<Trackable>()

    init {
        this.context = context
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                ID + " INTEGER PRIMARY KEY," +
                NAME + " TEXT," + DESC + " TEXT," +
                URL + " TEXT," +
                CATEGORY + " TEXT," + PICTURE_URL + " TEXT);"
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME
        db.execSQL(DROP_TABLE)
        onCreate(db)
    }

    fun addTracking(tracking: Tracking) {
        followedTrackings.add(tracking)
    }

    fun getTrackings(): ArrayList<Tracking> {
        return followedTrackings
    }

    fun getTrackingById(id: String): Tracking? {
        for (tracking in followedTrackings) {
            if (tracking.getId() == id) {
                return tracking
            }
        }
        return null
    }

    fun getTrackables(): ArrayList<Trackable> {

        return trackablesList
    }

    fun addTrackable(trackable: Trackable): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(ID, trackable.getId())
        values.put(NAME, trackable.getName())
        values.put(DESC, trackable.getDescription())
        values.put(URL, trackable.getUrl())
        values.put(PICTURE_URL, trackable.getPictureUrl())
        values.put(CATEGORY, trackable.getCategory())
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)
    }

    fun onStart() {
        val result = arrayListOf<Trackable>()
        val dataList = loadResourceFile()
        for (data in dataList) {
            val trackable: Trackable? = createATrackableObjectFromString(data)

            if (trackable != null && getTrackableFromId(trackable.getId()) == null) {
                addTrackable(trackable)
            }
            else {
                println("Ignored corrupted Trackable")
            }
        }
        this.trackablesList = result
    }

    fun onStop() {
    }

    fun getTrackableFromId(id: Int): Trackable? {
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $ID = $id"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val trackable = Trackable()
                trackable.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID))))
                trackable.setName(cursor.getString(cursor.getColumnIndex(NAME)))
                trackable.setDescription(cursor.getString(cursor.getColumnIndex(DESC)))
                trackable.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)))
                trackable.setUrl(cursor.getString(cursor.getColumnIndex(URL)))
                trackable.setPictureUrl(cursor.getString(cursor.getColumnIndex(PICTURE_URL)))
                return trackable
            }
        }
        return null
    }


    private fun loadResourceFile(): List<String> {
        val fileContent = context.assets.open(context.getString(R.string.trackable_file)).bufferedReader().use {
            it.readText()
        }

        return fileContent.split("\n")
    }

    private fun createATrackableObjectFromString(string: String): Trackable? {
        if (string.isNotEmpty()) {
            val tokens = arrayListOf<String>()
            var token = String()
            var insideQuote = false

            for (char in string) {
                if (char == '\"') {
                    insideQuote = !insideQuote;
                }
                else {
                    if (!insideQuote && char == ',') {
                        tokens.add(token)
                        token = ""
                    }
                    else {
                        token += char
                    }
                }
            }
            if (token.isNotEmpty()) {
                tokens.add(token)
            }
            if (tokens.size == 5 || tokens.size == 6) {
                return Trackable(tokens)
            }
            else {
                println("Couldn't parse : " + string)
            }
        }
        return null
    }


    companion object {
        private val DB_VERSION = 1
        private val DB_NAME = "FoodTruckInfos"
        private val TABLE_NAME = "Trackable"
        private val ID = "id"
        private val NAME = "name"
        private val DESC = "description"
        private val URL = "url"
        private val CATEGORY = "category"
        private val PICTURE_URL = "picture_url"
    }
}