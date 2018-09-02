package io.wurmel.assignement_1.Activity

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import io.wurmel.assignement_1.Model.Trackable
import io.wurmel.assignement_1.R
import io.wurmel.assignement_1.Service.TrackableService
import android.widget.ArrayAdapter
import android.widget.EditText
import io.wurmel.assignement_1.Model.Tracking
import io.wurmel.assignement_1.Service.TrackingService
import java.text.DateFormat
import android.widget.TimePicker
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.support.design.widget.FloatingActionButton
import kotlinx.android.synthetic.main.add_tracking_activity.*
import java.util.*


/**
 * Created by wurmel_a on 29/8/18.
 */
class   AddTrackingActivity: AppCompatActivity() {

    private var trackable: Trackable? = null
    private var trackingInfos = ArrayList<TrackingService.TrackingInfo>()

    private var stopSpinner: Spinner? = null
    private var timePickerField: EditText? = null
    private var fabButton: FloatingActionButton? = null

    private var mHour: Int = 0
    private var mMinute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_tracking_activity)
        stopSpinner = findViewById<Spinner>(R.id.trackableStop)
        timePickerField = findViewById<EditText>(R.id.timePicker)
        trackable = TrackableService.getTrackableFromId(this, intent.getIntExtra("trackableId", -1))
        configureSpinner()
        timePickerField!!.setOnClickListener {
            displayTimePicker()
        }
        fabButton = findViewById<FloatingActionButton>(R.id.fab)
        fabButton!!.setOnClickListener {
            addNewTracking()
        }
    }

    private fun configureSpinner() {
        val trackingInfos = TrackingService.getSingletonInstance(this).getTrackingForTrackable(trackable!!.getId())
        val elements = ArrayList<String>()
        elements.add("Select one stopping time")
        for (trackingInfo in trackingInfos) {
            if (trackingInfo.stopTime > 0) {
                var element = trackingInfo.stopTime.toString() + "mn " +  trackingInfo.date!!.toLocaleString() + " / "
                element = element + trackingInfo.latitude.toString() + " " + trackingInfo.longitude.toString()
                elements.add(element)
                this.trackingInfos.add(trackingInfo)
            }
        }
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, elements)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stopSpinner!!.adapter = arrayAdapter
    }

    private fun displayTimePicker() {
        val c = Calendar.getInstance()
        mHour = c.get(Calendar.HOUR_OF_DAY)
        mMinute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    mHour = hourOfDay
                    mMinute = minute
                    timePickerField!!.setText(hourOfDay.toString() + ":" + minute)
                }, mHour, mMinute, true)

        timePickerDialog.show()
    }

    private fun addNewTracking() {
        if (titleTextField.text.isEmpty()) {
            displayErrorDialog("You must provide a title")
            return
        }
        val newTracking = Tracking(trackable!!.getId(), titleTextField.text.toString())
        val idx = stopSpinner!!.selectedItemPosition
        if (idx == 0) {
            displayErrorDialog("You must choose a position for the Trackable")
            return
        }
        newTracking.setMeetLocation(trackingInfos[idx - 1].latitude, trackingInfos[idx - 1].longitude)
        newTracking.setTargetStartEndTime(trackingInfos[idx - 1].date!!, trackingInfos[idx - 1].stopTime)
        val meetDate = Date(trackingInfos[idx - 1].date!!.time)

        meetDate.minutes = mMinute
        meetDate.hours = mHour
        newTracking.setMeetTime(meetDate)
        if (meetDate >= newTracking.getTargetStartDate() && meetDate <= newTracking.getTargetEndTime()) {
            TrackableService.addTracking(newTracking)
            finish()
            return
        }
        displayErrorDialog("Date must be between start and end date")
    }

    private fun displayErrorDialog(error: String) {
        val dialog: AlertDialog

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
        builder.setMessage(error)
        val dialogClickListener = DialogInterface.OnClickListener{ _, _ ->
        }
        builder.setNeutralButton("Ok",dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }
}