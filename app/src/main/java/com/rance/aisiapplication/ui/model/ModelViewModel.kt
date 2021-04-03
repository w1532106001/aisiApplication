package com.rance.aisiapplication.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.model.Model
import com.smartmicky.android.data.api.ApiHelper
import javax.inject.Inject

class ModelViewModel @Inject constructor(val database: AppDatabase, val apiHelper: ApiHelper) : ViewModel() {
    //    var url = ""
    //查询数据库中item
    lateinit var modelLiveData: LiveData<Model>

//    val picturesSetLiveData = database.getPicturesSetDao().findPicturesSetLiveDataByUrl(url)
//    fun getPicturesSetLiveData(url:String):LiveData<PicturesSet>{
//        return database.getPicturesSetDao().findPicturesSetLiveDataByUrl(url)
//    }

    fun initData(url: String) {
        modelLiveData = database.getModelDao().findModelLiveDataByUrl(url)
    }
}