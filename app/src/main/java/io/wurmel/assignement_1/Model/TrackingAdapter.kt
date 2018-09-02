package io.wurmel.assignement_1.Model

import android.content.Intent
import android.media.Image
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import io.wurmel.assignement_1.Activity.AddTrackingActivity
import io.wurmel.assignement_1.R
import kotlinx.android.synthetic.main.trackable_list_layout.view.*
import org.w3c.dom.Text
import java.net.URI

/**
 * Created by Belal on 6/19/2017.
 */

class TrackingAdapter(val trackings: ArrayList<Tracking>) : RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tracking_list_layout, parent, false)

        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: TrackingAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnLongClickListener {
            trackings.removeAt(position)
            this.notifyItemRemoved(position)
            return@setOnLongClickListener true
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AddTrackingActivity::class.java)
            intent.putExtra("trackingId", trackings[position].getId())
            holder.itemView.context.startActivity(intent)
        }
        holder.bindItems(trackings[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return trackings.size
    }

    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(tracking: Tracking) {
            val monthYearTextView = itemView.findViewById<TextView>(R.id.month_year)
            val timeTextView = itemView.findViewById<TextView>(R.id.timeText)
            val positionTextView = itemView.findViewById<TextView>(R.id.position)
            val titleTextView = itemView.findViewById<TextView>(R.id.title)

            monthYearTextView.text = (tracking.getMeetTime().month + 1).toString() + "/" + (tracking.getMeetTime().year + 1900).toString()
            timeTextView.text = (tracking.getMeetTime().hours).toString() + ":" + tracking.getMeetTime().minutes.toString()
            positionTextView.text = tracking.getTargetLocation()
            titleTextView.text = tracking.getTitle()
        }
    }
}