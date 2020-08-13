package com.shpakovskiy.flickrbrowser.jsonparser

import android.os.AsyncTask
import android.util.Log
import com.shpakovskiy.flickrbrowser.photo.Photo
import org.json.JSONException
import org.json.JSONObject

class FlickrJsonParser(private val listener: OnDataAvailable) : AsyncTask<String, Void, List<Photo>>() {

    private val TAG = "GetFlickrJsonData"

    override fun doInBackground(vararg params: String?): List<Photo> {
        Log.d(TAG, "doInBackground starts")

        val photosList = ArrayList<Photo>()

        try {
            val jsonObject = JSONObject(params[0])
            val itemsArray = jsonObject.getJSONArray("items")

            for (i in 0 until itemsArray.length()) {
                val jsonPhoto = itemsArray.getJSONObject(i)
                val title = jsonPhoto.getString("title")
                val author = jsonPhoto.getString("author")
                val authorId = jsonPhoto.getString("author_id")
                val tags = jsonPhoto.getString("tags")

                val jsonMedia = jsonPhoto.getJSONObject("media")
                val photoUrl = jsonMedia.getString("m")
                val link = photoUrl.replaceFirst("_m.jpg", "_b.jpg")

                val photo = Photo(
                    title = title,
                    author = author,
                    authorId = authorId,
                    link = link,
                    tags = tags,
                    image = photoUrl
                )

                photosList.add(photo)

                Log.d(TAG, ".doInBackground $photo")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, ".doInBackground: Error processing JSON data. Message: ${e.message}")
            cancel(true)
            listener.onError(e)
        }

        return photosList
    }

    override fun onPostExecute(result: List<Photo>) {
        Log.d(TAG, "onPostExecute starts")
        super.onPostExecute(result)
        listener.onDataAvailable(result)
        Log.d(TAG, ".onPostExecute ends")
    }
}