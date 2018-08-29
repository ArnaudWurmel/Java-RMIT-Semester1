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

    fun getId(): String = this.id
}