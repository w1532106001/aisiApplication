package com.rance.aisiapplication.api

import com.smartmicky.android.data.api.ApiHelper
import com.rance.aisiapplication.api.service.ApiService
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppApiHelper @Inject constructor(private val apiService: ApiService) : ApiHelper {
    override fun downloadFileWithDynamicUrlSync(fileUrl: String) = apiService.downloadFileWithDynamicUrlSync(fileUrl)

    override fun getHtml(url: String) = apiService.getHtml(url)
}

