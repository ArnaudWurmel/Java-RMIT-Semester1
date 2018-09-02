package io.wurmel.assignement_1.Model

import io.wurmel.assignement_1.Service.TrackingService
import java.util.*

/**
 *  Created by Jean Gabriel Greco 06/08/18
 */

class Tracking(trackableId: Int, title: String) {
    private var id = ""
    private var title = title
    private var trackableId = trackableId
    private var targetStartTime = Date()
    private var targetEndTime = Date()
    private var meetTime = Date()
    private var currentLocation = ""
    private var meetLocation = ""

    init {
        id = UUID.randomUUID().toString()
    }

    fun getTitle(): String = this.title

    fun setTitle(title: String) {
        this.title = title
    }

    fun setTargetStartEndTime(startTime: Date, stopTime: Int) {
        targetStartTime = startTime
        targetEndTime = Date(startTime.time)
        targetEndTime.minutes += stopTime
    }

    fun setMeetTime(date: Date) {
        meetTime = date
    }

    fun setMeetLocation(latitude: Double, longitude: Double) {
        meetLocation = latitude.toString() + "," + longitude.toString()
    }

    fun getId(): String = this.id

    fun getTargetEndTime(): Date = this.targetEndTime

    fun getTargetStartDate(): Date = this.targetStartTime
}