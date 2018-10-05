package io.wurmel.assignement_1.Model

import java.util.Date
import java.util.UUID

/**
 *  Created by Jean Gabriel Greco 06/08/18
 */

class Tracking {
    private var id = ""
    private var title: String
    private var trackableId: Int
    private var targetStartTime = Date()
    private var targetEndTime = Date()
    private var meetTime = Date()
    private var currentLocation = ""
    private var meetLocation = ""

    constructor(trackableId: Int, title: String) {
        id = UUID.randomUUID().toString()
        this.trackableId = trackableId
        this.title = title
    }

    constructor(id: String, trackableId: Int, title: String) {
        this.id = id
        this.trackableId = trackableId
        this.title = title
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

    fun setMeetLocation(location: String) {
        meetLocation = location
    }

    fun getId(): String = this.id

    fun getTargetEndTime(): Date = this.targetEndTime

    fun getTargetStartDate(): Date = this.targetStartTime

    fun getMeetTime(): Date = this.meetTime

    fun getTargetLocation(): String = this.meetLocation

    fun getTrackableId(): Int = this.trackableId

    fun getMeetLocation(): String = this.meetLocation

}