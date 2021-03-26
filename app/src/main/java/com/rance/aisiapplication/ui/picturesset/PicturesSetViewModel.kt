package com.rance.aisiapplication.ui.picturesset

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.model.PicturesSet
import javax.inject.Inject

class PicturesSetViewModel @Inject constructor(val database: AppDatabase) : ViewModel() {
    //    var url = ""
    //查询数据库中item
    lateinit var picturesSetLiveData: LiveData<PicturesSet>

//    val picturesSetLiveData = database.getPicturesSetDao().findPicturesSetLiveDataByUrl(url)
//    fun getPicturesSetLiveData(url:String):LiveData<PicturesSet>{
//        return database.getPicturesSetDao().findPicturesSetLiveDataByUrl(url)
//    }

    fun initData(url: String) {
        picturesSetLiveData = database.getPicturesSetDao().findPicturesSetLiveDataByUrl(url)
    }
}