package io.wurmel.assignement_1.Service

import android.content.Context
import android.util.Log

import io.wurmel.assignement_1.Model.Trackable
import io.wurmel.assignement_1.R
import java.io.FileReader


/**
 * Created by wurmel_a on 19/7/18.
 */

class   TrackableService(context: Context) {

    private var context: Context = context

    companion object {
        fun getTrackables(context: Context): ArrayList<Trackable> {
            val result = arrayListOf<Trackable>()
            val trackableService = TrackableService(context)
            val dataList = trackableService.loadResourceFile()
            for (data in dataList) {
                val trackable: Trackable? = trackableService.createATrackableObjectFromString(data)

                if (trackable != null) {
                    result.add(trackable)
                }
            }
            return result
        }
    }

    private fun loadResourceFile(): List<String> {
        val fileContent = context.assets.open(context.getString(R.string.trackable_file)).bufferedReader().use {
            it.readText()
        }

        return fileContent.split("\n")
    }

    private fun createATrackableObjectFromString(string: String): Trackable? {
        if (string.isNotEmpty()) {
        }
        return null
    }

}