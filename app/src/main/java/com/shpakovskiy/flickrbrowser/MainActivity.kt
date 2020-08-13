package com.shpakovskiy.flickrbrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.shpakovskiy.flickrbrowser.dataloader.DownloadStatus
import com.shpakovskiy.flickrbrowser.dataloader.JsonDataLoader
import com.shpakovskiy.flickrbrowser.dataloader.OnDownloadComplete
import com.shpakovskiy.flickrbrowser.jsonparser.FlickrJsonParser
import com.shpakovskiy.flickrbrowser.jsonparser.OnDataAvailable
import com.shpakovskiy.flickrbrowser.photo.Photo
import com.shpakovskiy.flickrbrowser.viewadapter.FlickrRecyclerViewAdapter
import com.shpakovskiy.flickrbrowser.viewadapter.OnRecyclerClickListener
import com.shpakovskiy.flickrbrowser.viewadapter.RecyclerItemClickListener

import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception

import androidx.preference.PreferenceManager

class MainActivity : BaseActivity(),
    OnDownloadComplete,
    OnDataAvailable,
    OnRecyclerClickListener {

    private val TAG = "MainActivity"
    private val flickrRecyclerViewAdapter = FlickrRecyclerViewAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activateToolbar(false)

        pictures_recycler_view.layoutManager = LinearLayoutManager(this)
        pictures_recycler_view.addOnItemTouchListener(RecyclerItemClickListener(this, pictures_recycler_view, this))
        pictures_recycler_view.adapter = flickrRecyclerViewAdapter

        val url = createUri("https://api.flickr.com/services/feeds/photos_public.gne",
        "car,tesla", "en-us", true)

        val jsonDataLoader = JsonDataLoader(this)
        jsonDataLoader.execute(url)
    }

    private fun createUri(baseUrl: String, searchCriteria: String, lang: String, matchAll: Boolean): String {
        Log.d(TAG, ".createUri called")

        return Uri.parse(baseUrl).buildUpon()
            .appendQueryParameter("tags", searchCriteria)
            .appendQueryParameter("tagmode", if (matchAll) "ALL" else "ANY")
            .appendQueryParameter("lang", lang)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .build()
            .toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDownloadComplete(data: String, downloadStatus: DownloadStatus) {
        if (downloadStatus == DownloadStatus.OK) {
            Log.d(TAG, "onDownloadComplete called")

            val flickrJsonParser = FlickrJsonParser(this)
            flickrJsonParser.execute(data)

        } else {
            Log.d(TAG, "onDownloadComplete failed with status: $downloadStatus. Error message: $data")
        }
    }

    override fun onDataAvailable(data: List<Photo>) {
        Log.d(TAG, ".onDataAvailable called")

        flickrRecyclerViewAdapter.loadNewData(data)

        Log.d(TAG, ".onDataAvailable ends")
    }

    override fun onError(exception: Exception) {
        Log.e(TAG, ".onError called with ${exception.message}")
    }

    override fun onItemClick(view: View, position: Int) {
        Log.d(TAG, ".onItemClick: starts")

        val photo = flickrRecyclerViewAdapter.getPhoto(position)
        if (photo != null) {
            val intent = Intent(this, PhotoDetailsActivity::class.java)
            intent.putExtra(PHOTO_TRANSFER, photo)
            startActivity(intent)
        }
    }

    override fun onItemLongClick(view: View, position: Int) {
        Log.d(TAG, ".onItemLongClick: starts")
        Toast.makeText(this, "Long tap $position", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val queryResult = sharedPreferences.getString(FLICKR_QUERY, "")

        if (queryResult != null && queryResult.isNotEmpty()) {
            val url = createUri("https://api.flickr.com/services/feeds/photos_public.gne",
                queryResult, "en-us", true)

            val jsonDataLoader = JsonDataLoader(this)
            jsonDataLoader.execute(url)
        }
    }
}