package com.rance.aisiapplication.service

interface DownloadListener {
    fun onProgress(progress: Int)

    fun onFail()

    fun onExecuteComplete()
}