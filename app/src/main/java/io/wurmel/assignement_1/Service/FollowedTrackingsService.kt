package io.wurmel.assignement_1.Service

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.wurmel.assignement_1.Model.Tracking
import java.sql.Date
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class   FollowedTrackingsService(context: Context): SQLiteOpenHelper(context, FollowedTrackingsService.DB_NAME, null, FollowedTrackingsService.DB_VERSION) {

    private val followedTrackings: ArrayList<Tracking>
        get() {
            var result = ArrayList<Tracking>()
            val db = writableDatabase
            val selectQuery = "SELECT * FROM ${FollowedTrackingsService.TABLE_NAME}"
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val trackingId = cursor.getString(cursor.getColumnIndex(FollowedTrackingsService.ID))
                    val trackableId = cursor.getInt(cursor.getColumnIndex(FollowedTrackingsService.TRACKABLEID))
                    val title = cursor.getString(cursor.getColumnIndex(FollowedTrackingsService.TITLE))
                    val tracking = Tracking(trackingId, trackableId, title)
                    val startDate = cursor.getLong(cursor.getColumnIndex(START_TIME))
                    val endDate = cursor.getLong(cursor.getColumnIndex(END_TIME))
                    val meetTime = cursor.getLong(cursor.getColumnIndex(MEET_TIME))
                    val meetLocation = cursor.getString(cursor.getColumnIndex(MEET_LOCATION))
                    tracking.setTargetStartEndTime(Date(startDate), ((endDate - startDate) / 60).toInt())
                    tracking.setMeetLocation(meetLocation)
                    val meetTimedate = LocalDateTime.ofInstant(Instant.ofEpochMilli(meetTime), ZoneId.systemDefault())
                    tracking.setMeetTime(Date.from(meetTimedate.atZone(ZoneId.systemDefault()).toInstant()))
                    result.add(tracking)
                } while (cursor.moveToNext())
            }
            db.close()
            return result
        }


    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
            ID + " STRING PRIMARY KEY," +
            TRACKABLEID + " INTEGER," +
            TITLE + " TEXT," + START_TIME + " INTEGER," +
            END_TIME + " INTEGER," +
            MEET_TIME + " INTEGER," + MEET_LOCATION + " TEXT);"
        println(CREATE_TABLE)
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME
        db.execSQL(DROP_TABLE)
        onCreate(db)
    }

    fun addTracking(tracking: Tracking): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(FollowedTrackingsService.ID, tracking.getId())
        values.put(FollowedTrackingsService.TITLE, tracking.getTitle())
        values.put(FollowedTrackingsService.TRACKABLEID, tracking.getTrackableId())
        values.put(FollowedTrackingsService.START_TIME, tracking.getTargetStartDate().time)
        values.put(FollowedTrackingsService.END_TIME, tracking.getTargetEndTime().time)
        values.put(FollowedTrackingsService.MEET_TIME, tracking.getMeetTime().time)
        values.put(FollowedTrackingsService.MEET_LOCATION, tracking.getMeetLocation())
        val _success = db.insert(FollowedTrackingsService.TABLE_NAME, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)
    }

    fun updateTracking(tracking: Tracking): Boolean {
        return removeTracking(tracking) && addTracking(tracking)
    }

    fun removeTracking(tracking: Tracking): Boolean {
        val trackingId = tracking.getId()
        val db = writableDatabase
        db.delete(TABLE_NAME, "$ID='$trackingId'", null)
        db.close()
        return true
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

    companion object {
        private val DB_VERSION = 1
        private val DB_NAME = "FoodTruckInfosTrackings"
        private val TABLE_NAME = "FollowedTrackings"
        private val ID = "id"
        private val TITLE = "title"
        private val TRACKABLEID = "trackable_id"
        private val START_TIME = "start_time"
        private val END_TIME = "end_time"
        private val MEET_TIME = "meet_time"
        private val MEET_LOCATION = "meet_location"
    }
}