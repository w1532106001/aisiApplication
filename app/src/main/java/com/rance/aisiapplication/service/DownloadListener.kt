package com.rance.aisiapplication.service

interface DownloadListener {
    fun onProgress(progress: Int)

    fun onSuccess()

    fun onFail()

    fun onPaused()

    fun onCanceled()
}