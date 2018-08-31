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
import java.net.URI

/**
 * Created by Belal on 6/19/2017.
 */

class TrackableAdapter(val Trackables: ArrayList<Trackable>) : RecyclerView.Adapter<TrackableAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackableAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.trackable_list_layout, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: TrackableAdapter.ViewHolder, position: Int) {
        holder.bindItems(Trackables[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return Trackables.size
    }

    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(trackable: Trackable) {
            val textViewName = itemView.findViewById<TextView>(R.id.textViewUsername)
            val textViewDescritpion  = itemView.findViewById<TextView>(R.id.textViewDrescription)
            val textViewCategory = itemView.findViewById<TextView>(R.id.textViewCategory)
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            textViewName.text = trackable.getName()
            textViewDescritpion.text = trackable.getDescription()
            textViewCategory.text = trackable.getCategory()

            if (trackable.isPictureProvided()) {
                Glide.with(itemView.context).load(trackable.getPictureUrl()).into(imageView)
                imageView.visibility = View.VISIBLE
                imageView.scaleType = ImageView.ScaleType.FIT_XY
            }
            else {
                imageView.visibility = View.GONE
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, AddTrackingActivity::class.java)
                intent.putExtra("trackableId", trackable.getId())
                itemView.context.startActivity(intent)
            }
        }
    }
}