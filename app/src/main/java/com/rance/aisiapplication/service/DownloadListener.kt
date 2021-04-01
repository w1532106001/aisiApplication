package com.rance.aisiapplication.service

interface DownloadTaskService {
    fun updateProgress()

    fun updateSpeedPerSecond(speedPerSecond:String)

    fun executeComplete()
}