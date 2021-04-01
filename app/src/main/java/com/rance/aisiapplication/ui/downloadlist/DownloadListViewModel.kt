package com.rance.aisiapplication.ui.downloadlist

import androidx.lifecycle.ViewModel
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.service.DownloadTaskController
import com.smartmicky.android.data.api.ApiHelper
import javax.inject.Inject

class DownloadListViewModel @Inject constructor(val database: AppDatabase, val apiHelper: ApiHelper) : ViewModel() {
    val downloadTaskController  = DownloadTaskController()
//    val picturesSetListLiveData = database.getPicturesSetDao().getAllPicturesSet()
}