package io.wurmel.assignement_1.Activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.LinearLayout
import io.wurmel.assignement_1.Model.CustomAdapter
import io.wurmel.assignement_1.Model.User
import io.wurmel.assignement_1.R
import io.wurmel.assignement_1.Service.TrackableService
import io.wurmel.assignement_1.Service.TrackingService
import java.text.DateFormat
import java.text.ParseException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TrackableService.getTrackables(applicationContext)
        TestTrackingService.test(applicationContext)

        //getting recyclerview from xml
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerView) as RecyclerView

        //adding a layoutmanager
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)


        //crating an arraylist to store users using the data class user
        val users = ArrayList<User>()

        //adding some dummy data to the list
        users.add(User("Belal Khan", "Ranchi Jharkhand"))
        users.add(User("Ramiz Khan", "Ranchi Jharkhand"))
        users.add(User("Faiz Khan", "Ranchi Jharkhand"))
        users.add(User("Yashar Khan", "Ranchi Jharkhand"))

        //creating our adapter
        val adapter = CustomAdapter(users)

        //now adding the adapter to recyclerview
        recyclerView.adapter = adapter
    }
}

object TestTrackingService {
    private val LOG_TAG = TrackingService::class.java!!.getName()

    // call this method to run simple hard coded test (note you will need to handle the parsing Exception)

    fun test(context: Context) {
        val trackingService = TrackingService.getSingletonInstance(context)
        Log.i(LOG_TAG, "Parsed File Contents:")
        trackingService.logAll()

        try {
            // 5 mins either side of 05/07/2018 1:05:00 PM
            // PRE: make sure tracking_data.txt contains valid matches
            // PRE: make sure device locale matches provided DateFormat (you can change either device settings or String param)
            val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)
            val searchDate = "05/07/2018 1:05:00 PM"
            val searchWindow = 5 // +/- 5 mins
            val date = dateFormat.parse(searchDate)
            val matched = trackingService
                    .getTrackingInfoForTimeRange(date, searchWindow, 0)
            Log.i(LOG_TAG, String.format("Matched Query: %s, +/-%d mins", searchDate, searchWindow))
            trackingService.log(matched)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}