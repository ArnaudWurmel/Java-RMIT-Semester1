package io.wurmel.assignement_1.Service

import android.app.AlertDialog
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import io.wurmel.assignement_1.Activity.MainActivity
import io.wurmel.assignement_1.Model.Tracking
import io.wurmel.assignement_1.R.string.trackable
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GMatrixService(delegate: AsyncResponse, tracking: TrackingService.TrackingInfo) : AsyncTask<String, String, String>() {

    private val apiKey = "AIzaSyB53F80b_RJpHszOLVspQgbutvNtHSCs94"
    val CONNECTON_TIMEOUT_MILLISECONDS = 60000
    var tracking: TrackingService.TrackingInfo? = null

    override fun onPreExecute() {
        // Before doInBackground
    }

    interface AsyncResponse {
        fun processFinish(output: TrackingService.TrackingInfo?)
    }

    var delegate: AsyncResponse? = null

    init {
        this.delegate = delegate
        this.tracking = tracking
    }

    override fun doInBackground(vararg urls: String?): String {
        var urlConnection: HttpURLConnection? = null

        try {

            val url = URL(urls[0])

            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = CONNECTON_TIMEOUT_MILLISECONDS
            urlConnection.readTimeout = CONNECTON_TIMEOUT_MILLISECONDS

            var inString = streamToString(urlConnection.inputStream)

            publishProgress(inString)
            return inString
        } catch (ex: Exception) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect()
            }
        }
        return "toto"
    }

    override fun onProgressUpdate(vararg values: String?) {

    }

    override fun onPostExecute(result: String?) {
        if (result != "toto"){
            try {
                var json = JSONObject(result)

                val rows = json.getJSONArray("rows")
                val ele = rows.getJSONObject(0).getJSONArray("elements")
                val dis = ele.getJSONObject(0).getJSONObject("distance")
                val dur = ele.getJSONObject(0).getJSONObject("duration")
                tracking?.distanceText = dis.getString("text")
                tracking?.distanceValue = dis.getString("value").toInt()
                tracking?.durationText = dur.getString("text")
                tracking?.durationValue = dur.getString("value").toInt()
                delegate?.processFinish(tracking)
            } catch (ex: Exception) {
            }
        }
        else {
            Log.d("erreur api", "is toto")
        }
    }

    fun streamToString(inputStream: InputStream): String {

        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var result = ""

        try {
            do {
                line = bufferReader.readLine()
                if (line != null) {
                    result += line
                }
            } while (line != null)
            inputStream.close()
        } catch (ex: Exception) {

        }

        return result
    }
}