package com.shpakovskiy.flickrbrowser.dataloader

interface OnDownloadComplete {
    fun onDownloadComplete(data: String, downloadStatus: DownloadStatus)
}