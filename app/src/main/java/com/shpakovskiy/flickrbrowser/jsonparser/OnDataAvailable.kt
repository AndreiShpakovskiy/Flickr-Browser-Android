package com.shpakovskiy.flickrbrowser.jsonparser

import com.shpakovskiy.flickrbrowser.photo.Photo
import java.lang.Exception

interface OnDataAvailable {
    fun onDataAvailable(data: List<Photo>)
    fun onError(exception: Exception)
}