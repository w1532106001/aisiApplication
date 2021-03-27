package com.rance.aisiapplication.api.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Streaming
    @GET
    fun downloadFileWithDynamicUrlSync(@Url fileUrl: String): Call<ResponseBody>

    @GET
    fun getHtml(@Url url: String): Call<String>

}