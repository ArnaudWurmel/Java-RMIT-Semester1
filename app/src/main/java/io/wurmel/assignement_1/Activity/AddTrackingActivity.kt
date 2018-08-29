package io.wurmel.assignement_1.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.wurmel.assignement_1.R

/**
 * Created by wurmel_a on 29/8/18.
 */
class   AddTrackingActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_tracking_activity)
        System.out.println("In Add tracking")
    }

}