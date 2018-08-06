package io.wurmel.assignement_1.Model

import io.wurmel.assignement_1.Service.TrackingService
import java.util.*

/**
 *  Created by Jean Gabriel Greco 06/08/18
 */

class Tracking(private var TrackableId: Int, private var Title: String) {
    private var TrackingId = ""
    private var TargetStartTime = ""
    private var TargetEndTime = ""
    private var MeetTime = ""
    private var CurrentLocation = ""
    private var MeetLocation = ""

    init {
        TrackingId = UUID.randomUUID().toString()
    }
}