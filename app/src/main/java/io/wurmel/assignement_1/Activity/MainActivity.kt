package io.wurmel.assignement_1.Activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import io.wurmel.assignement_1.Model.TrackableAdapter
import io.wurmel.assignement_1.R
import io.wurmel.assignement_1.Service.TrackableService
import io.wurmel.assignement_1.Service.TrackingService
import java.text.DateFormat
import java.text.ParseException
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import io.wurmel.assignement_1.Model.Trackable
import io.wurmel.assignement_1.Model.TrackingAdapter


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var menu: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: SearchView
    private var displayingTrackable = true
    private var searchString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TrackableService(applicationContext).onStart()
        setContentView(R.layout.activity_main)

        searchBar = findViewById(R.id.searchBar)
        recyclerView = findViewById(R.id.displayList)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        displayTrackings()
        displayTrackables()
        menu = findViewById(R.id.navigation)
        menu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.tracking_action -> {
                    displayTrackings()
                }
                R.id.trackable_action -> {
                    displayTrackables()
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        searchBar.setOnQueryTextListener(this)
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != null) {
            searchString = p0
        }
        else {
            searchString = ""
        }
        displayTrackables()
        return true
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        if (displayingTrackable) {
            displayTrackables()
        }
        else {
            displayTrackings()
        }
    }

    private fun displayTrackables() {
        searchBar.visibility = View.VISIBLE
        var trackables = TrackableService(applicationContext).getTrackables()
        if (searchString.isNotEmpty()) {
            var tmpTrackagles = ArrayList<Trackable>()
            for (trackable in trackables) {
                val categories = trackable.getCategory().split(",")
                var match = false
                for (category in categories) {
                    if (category.startsWith(searchString, true)) {
                        match = true
                    }
                }
                if (match) {
                    tmpTrackagles.add(trackable)
                }
            }
            trackables = tmpTrackagles
        }
        val adapter = TrackableAdapter(trackables)
        recyclerView.adapter = adapter
        displayingTrackable = true
    }

    private fun displayTrackings() {
        searchBar.visibility = View.GONE
        val followedsTrackings = TrackableService(this).getTrackings()
        val adapter = TrackingAdapter(followedsTrackings)

        recyclerView.adapter = adapter
        displayingTrackable = false
    }

    override fun onStop() {
        super.onStop()
        TrackableService(this).onStop()
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