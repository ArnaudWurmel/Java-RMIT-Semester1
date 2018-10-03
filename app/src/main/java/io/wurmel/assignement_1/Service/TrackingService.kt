package io.wurmel.assignement_1.Service

// simulated tracking service by Caspar for MAD s2, 2018
// Usage: add this class to project in appropriate package
// add tracking_data.txt to res/raw folder
// see: TestTrackingService.test() method for example

// NOTE: you may need to explicitly add the import for the generated some.package.R class
// which is based on your package declaration in the manifest

import android.content.Context
import android.content.res.Resources
import android.util.Log
import io.wurmel.assignement_1.R

import java.text.DateFormat
import java.text.ParseException
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Scanner

class TrackingService// Singleton
private constructor() {
    private val trackingList = ArrayList<TrackingInfo>()

    // This is only a data access object (DAO)
    // You must extract data and place in your model
    class TrackingInfo {
        var date: Date? = null
        var trackableId: Int = 0
        var stopTime: Int = 0
        var latitude: Double = 0.toDouble()
        var longitude: Double = 0.toDouble()

        //Properties from gmatrix
        var distanceText: String = ""
        var distanceValue: Int = 0
        var durationText: String = ""
        var durationValue: Int = 0

        override fun toString(): String {
            return String.format(Locale.getDefault(), "Date/Time=%s, trackableId=%d, stopTime=%d, lat=%.5f, long=%.5f", DateFormat.getDateTimeInstance(
                    DateFormat.SHORT, DateFormat.MEDIUM).format(date), trackableId, stopTime, latitude, longitude)
        }

        fun getLocation(): String {
            return latitude.toString() + "," + longitude.toString()
        }
    }

    // check if the source date is with the range of target date +/- minutes and seconds
    private fun dateInRange(source: Date?, target: Date, periodMinutes: Int, periodSeconds: Int): Boolean {
        val sourceCal = Calendar.getInstance()
        val targetCalStart = Calendar.getInstance()
        val targetCalEnd = Calendar.getInstance()
        // set the calendars for comparison
        sourceCal.time = source
        targetCalStart.time = target
        targetCalEnd.time = target

        // set up start and end range match for mins/secs
        // +/- period minutes/seconds to check
        targetCalStart.set(Calendar.MINUTE, targetCalStart.get(Calendar.MINUTE) - periodMinutes)
        targetCalStart.set(Calendar.SECOND, targetCalStart.get(Calendar.SECOND) - periodSeconds)
        targetCalEnd.set(Calendar.MINUTE, targetCalEnd.get(Calendar.MINUTE) + periodMinutes)
        targetCalEnd.set(Calendar.SECOND, targetCalEnd.get(Calendar.SECOND) + periodMinutes)

        // return if source date in the target range (inclusive of start/end range)
        return (sourceCal == targetCalStart || sourceCal == targetCalEnd
                || sourceCal.after(targetCalStart) && sourceCal.before(targetCalEnd))
    }

    // called internally before usage
    private fun parseFile(context: Context?) {
        trackingList.clear()
        // resource reference to tracking_data.txt in res/raw/ folder of your project
        // supports trailing comments with //
        try {
            Scanner(context!!.resources.openRawResource(R.raw.tracking_data)).use { scanner ->
                // match comma and 0 or more whitespace OR trailing space and newline
                scanner.useDelimiter(",\\s*|\\s*\\n+")
                while (scanner.hasNext()) {
                    val trackingInfo = TrackingInfo()
                    trackingInfo.date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).parse(scanner.next())
                    trackingInfo.trackableId = Integer.parseInt(scanner.next())
                    trackingInfo.stopTime = Integer.parseInt(scanner.next())
                    trackingInfo.latitude = java.lang.Double.parseDouble(scanner.next())
                    var next = scanner.next()
                    val commentPos: Int
                    // strip trailing comment
                    commentPos = next.indexOf("//")
                    if (commentPos >= 0)
                        next = next.substring(0, commentPos)
                    trackingInfo.longitude = java.lang.Double.parseDouble(next)
                    trackingList.add(trackingInfo)
                }
            }
        } catch (e: Resources.NotFoundException) {
            Log.i(LOG_TAG, "File Not Found Exception Caught")
        } catch (e: ParseException) {
            Log.i(LOG_TAG, "ParseException Caught (Incorrect File Format)")
        }

    }

    // singleton support
    private object LazyHolder {
        internal val INSTANCE = TrackingService()
    }

    // log contents of file (for testing/logging only)
    fun logAll() {
        log(trackingList)
    }

    // log contents of provided list (for testing/logging and example purposes only)
    fun log(trackingList: List<TrackingInfo>) {
        // we reparse file contents for latest data on every call
        parseFile(context)
        for (trackingInfo in trackingList) {
            // to prevent this logging issue https://issuetracker.google.com/issues/77305804
            try {
                Thread.sleep(1)
            } catch (e: InterruptedException) {
            }

            Log.i(LOG_TAG, trackingInfo.toString())
        }
    }

    // the main method you can call periodically to get data that matches a given date period
    // date +/- period minutes/seconds to check
    fun getTrackingInfoForTimeRange(date: Date, periodMinutes: Int, periodSeconds: Int): List<TrackingInfo> {
        // we reparse file contents for latest data on every call
        parseFile(context)
        val returnList = ArrayList<TrackingInfo>()
        for (trackingInfo in trackingList)
            if (dateInRange(trackingInfo.date, date, periodMinutes, periodSeconds))
                returnList.add(trackingInfo)
        return returnList
    }

    fun getTrackingForTrackable(trackableId: Int): List<TrackingInfo> {
        val resultList = ArrayList<TrackingInfo>()
        parseFile(context)

        for (trackingInfo in trackingList) {
            if (trackingInfo.trackableId == trackableId) {
                resultList.add(trackingInfo)
            }
        }
        return resultList
    }

    companion object {
        // PRIVATE PORTION
        private val LOG_TAG = TrackingService::class.java.name
        private var context: Context? = null

        // PUBLIC METHODS

        // singleton
        // thread safe lazy initialisation: see https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
        fun getSingletonInstance(context: Context): TrackingService {
            TrackingService.context = context
            return LazyHolder.INSTANCE
        }
    }
}
