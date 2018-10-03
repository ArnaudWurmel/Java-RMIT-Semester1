package io.wurmel.assignement_1.Activity

import android.Manifest
import android.app.AlertDialog
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
import android.widget.*
import io.wurmel.assignement_1.Model.TrackableAdapter
import io.wurmel.assignement_1.R
import io.wurmel.assignement_1.Service.TrackableService
import io.wurmel.assignement_1.Service.TrackingService
import java.text.DateFormat
import java.text.ParseException
import io.wurmel.assignement_1.Model.Trackable
import io.wurmel.assignement_1.Model.TrackingAdapter
import android.widget.TimePicker
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import java.text.SimpleDateFormat
import java.util.*
import android.view.MenuInflater
import io.wurmel.assignement_1.R.id.refreshTime
import io.wurmel.assignement_1.R.string.trackable
import io.wurmel.assignement_1.Service.GMatrixService
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener, GMatrixService.AsyncResponse {

    private lateinit var menu: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: SearchView
    private lateinit var refreshTime: EditText
    private lateinit var setTime: Button
    private lateinit var filter: CardView
    private var displayingTrackable = true
    private var searchString: String = ""
    val toto = "toto"

    private var api = "AIzaSyB53F80b_RJpHszOLVspQgbutvNtHSCs94"

    private var locationManager : LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBar = findViewById(R.id.searchBar)
        recyclerView = findViewById(R.id.displayList)
        refreshTime = findViewById(R.id.refreshTime)
        setTime = findViewById(R.id.button)
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
        refreshTime.isEnabled = false
        setTime.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                refreshTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
                val h = cal.get(Calendar.HOUR)
                val m = cal.get(Calendar.MINUTE)
                val ms = h * 3600000 + m * 60000
                INTERVAL = ms.toLong()
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        searchBar.setOnQueryTextListener(this)

        //MARK: get location
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    0x0)
        }
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    0x0)
        }
        // Create persistent LocationManager reference
    }
    val TAG = "LocationTrackingService"

    var INTERVAL = 1000.toLong()
    val DISTANCE = 10.toFloat() // In meters

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun uriBuilder(origins: String, destination: String): String {
        val builder = Uri.Builder()
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .path("maps/api/distancematrix/json")
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("origins", origins)
                .appendQueryParameter("destinations", destination)
                .appendQueryParameter("mode", "walking")
                .appendQueryParameter("key", "AIzaSyB53F80b_RJpHszOLVspQgbutvNtHSCs94")
        return builder.build().toString()
    }

    var trackablesFiltered: ArrayList<Trackable>? = null
    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            //TODO foreach trackable get time to go there, keep the one with enough time, get nearest of them
            var trackables = TrackableService.getTrackables(applicationContext)
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
            trackablesFiltered = ArrayList()

            val trackingService = TrackingService.getSingletonInstance(this@MainActivity)
            for (trackable in trackables) {
                val id = trackable.getId()
                val pos_test = trackingService.getTrackingForTrackable(id)
                if (pos_test.count() > 0)
                {
                    val loc = pos_test[0].getLocation()
                    Log.d("origin location", location.latitude.toString() + "," + location.longitude.toString())
                    Log.d("Destination Location", loc)
                    val url = uriBuilder(location.latitude.toString() + "," + location.longitude.toString(),loc)
                    GMatrixService(this@MainActivity, pos_test[0]).execute(url)
                    trackablesFiltered!!.add(trackable)
                }
            }
            count = trackablesFiltered!!.count()
//            locationManager?.removeUpdates(this)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    var i: Int = 0
    var count = 0

    fun getClosestTrackable(trackables: ArrayList<Trackable>) : Trackable? {
        var tmp: Trackable? = null
        for (trackable in trackables!!) {
            Log.d("let's see this shit=", trackable.getTrackingInfo().distanceText + trackable.getTrackingInfo().distanceValue + "|" + trackable.getTrackingInfo().durationText + trackable.getTrackingInfo().durationValue)
            if (tmp == null) {
                tmp = trackable
            }
            else if (trackable.getTrackingInfo().distanceValue < tmp.getTrackingInfo().distanceValue) {
                tmp = trackable
            }
        }
        return tmp
    }

    fun displaySug(trackable: Trackable, index: Int) {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("You're lucky")
        builder.setMessage(trackable.getName()
                + " is close to you"
                + " it is going to take: "
                + trackable.getTrackingInfo().durationText
                + " to reach it if you leave now")
                .setNegativeButton("Cancel"
                ) { dialog, id ->
                    //do nothing
                }
                .setNeutralButton("Next one"
                ) { dialog, id ->
                    // User Skipped
                    if (index + 1 < trackableData.count()){
                        displaySug(trackableData.get(index + 1), index + 1)
                    }
                }
                .setPositiveButton("Accept"
                ) { dialog, id ->
                    // User accepted -> create a tracking
                }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    lateinit var trackableData: ArrayList<Trackable>

    override fun processFinish(output: TrackingService.TrackingInfo?) {
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        if (trackablesFiltered != null) {
            for (trackable in this!!.trackablesFiltered!!) {
                if (output != null) {
                    if (trackable.getId() == output.trackableId){
                        trackable.setTrackingInfo(output)
                        i++
                        break
                    }
                }
            }
            if (i == count) {
                //Les calls api sont termines
                var n = 0
                trackableData = ArrayList()
                while (n < count) {
                    var tmp = getClosestTrackable(trackablesFiltered!!)
                    if (tmp != null) {
                        trackableData.add(tmp)
                        trackablesFiltered!!.remove(tmp)
                    }
                    n++
                }
                for (trackable in trackableData) {
                    Log.d("toto trackable ordered:", trackable.getTrackingInfo().distanceText + "--" + trackable.getName())
                }
                //tmp est le trackable le plus proche
                if (trackableData.count() > 0) {
                    Log.d("toto", "Getting inside if")
                    displaySug(trackableData.get(0), 0)
                }
                if (trackableData.count() == 0) {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("No luck")
                    builder.setMessage("There is no reachable truck at this time")
                            .setNeutralButton("Ok"
                            ) { dialog, id ->
                                // User Skipped
                            }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
                i = 0
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_favorite -> {
            // get current location
            try {
                if (locationManager == null)
                    locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                try {
                    locationManager?.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
                } catch (e: SecurityException) {
                    Log.e(TAG, "Fail to request location update", e)
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "GPS provider does not exist", e)
                }
            } catch(ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available");
            }
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
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
        var trackables = TrackableService.getTrackables(applicationContext)
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
        val followedsTrackings = TrackableService.getTrackings()
        val adapter = TrackingAdapter(followedsTrackings)

        recyclerView.adapter = adapter
        displayingTrackable = false
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