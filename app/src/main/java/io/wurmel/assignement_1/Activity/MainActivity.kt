package io.wurmel.assignement_1.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.wurmel.assignement_1.R
import io.wurmel.assignement_1.Service.TrackableService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TrackableService.getTrackables(applicationContext)

    }
}
