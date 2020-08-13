package com.shpakovskiy.flickrbrowser.dataloader

import android.os.AsyncTask
import android.util.Log
import com.shpakovskiy.flickrbrowser.MainActivity
import java.io.IOException
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.*

enum class DownloadStatus {
    OK, IDLE, NOT_INITIALISED, FAILED_OR_EMPTY, PERMISSIONS_ERROR, ERROR
}

class JsonDataLoader(private val listener: OnDownloadComplete) : AsyncTask<String, Void, String>() {

    private val TAG = "JsonDataLoader"
    private var downloadStatus = DownloadStatus.IDLE

    override fun doInBackground(vararg params: String?): String {
        if (params[0] == null) {
            downloadStatus = DownloadStatus.NOT_INITIALISED
            return "No URL specified"
        }

        try {
            downloadStatus = DownloadStatus.OK
            return URL(params[0]).readText()
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is MalformedURLException -> {
                    downloadStatus = DownloadStatus.NOT_INITIALISED
                    "doInBackground: Invalid URL ${e.message}"
                }
                is IOException -> {
                    downloadStatus = DownloadStatus.FAILED_OR_EMPTY
                    "doInBackground: IO Exception reading data: ${e.message}"
                }
                is SecurityException -> {
                    downloadStatus = DownloadStatus.PERMISSIONS_ERROR
                    "doInBackground: SecurityException: Needs permission? ${e.message}"
                } else -> {
                    downloadStatus = DownloadStatus.ERROR
                    "doInBackground: Unknown error: ${e.message}"
                }
            }
            Log.e(TAG, errorMessage)
            return errorMessage
        }
    }

    override fun onPostExecute(result: String) {
        //Log.d(TAG, "onPostExecute, result: $result")
        listener.onDownloadComplete(result, downloadStatus)
    }
}