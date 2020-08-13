package com.shpakovskiy.flickrbrowser.viewadapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shpakovskiy.flickrbrowser.R
import com.shpakovskiy.flickrbrowser.photo.Photo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.browse_layout.view.*
import kotlinx.android.synthetic.main.content_main.view.*

class FlickrImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val photo: ImageView = view.photo_view
    val title: TextView = view.pictire_title
    
}

class FlickrRecyclerViewAdapter(private var photosList: List<Photo>) : RecyclerView.Adapter<FlickrImageViewHolder>() {

    private val TAG = "FlickrRVAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrImageViewHolder {
        Log.d(TAG, ".onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.browse_layout, parent, false)
        return FlickrImageViewHolder(view)
    }

    fun loadNewData(newPhotos: List<Photo>) {
        photosList = newPhotos
        notifyDataSetChanged()
    }

    fun getPhoto(position: Int): Photo? {
        return if (photosList.isNotEmpty()) photosList[position] else null
    }

    override fun getItemCount(): Int {
        return if (photosList.isNotEmpty()) photosList.size else 0
    }

    override fun onBindViewHolder(holder: FlickrImageViewHolder, position: Int) {
        if (photosList.isEmpty()) {
            holder.photo.setImageResource(R.drawable.default_placeholder_image)
            holder.title.text = "No photos matching criteria"
        } else {
            val photoItem = photosList[position]
            Log.d(TAG, ".onBindViewHolder: ${photoItem.title} -> $position")

            Picasso.with(holder.photo.context).load(photoItem.image)
                .error(R.drawable.default_placeholder_image)
                .placeholder(R.drawable.default_placeholder_image)
                .into(holder.photo)

            holder.title.text = photoItem.title
        }
    }
}