package com.rance.aisiapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.model.PicturesSet
import com.smartmicky.android.data.api.ApiHelper
import javax.inject.Inject

class HomeViewModel @Inject constructor(val database: AppDatabase,val apiHelper: ApiHelper) : ViewModel() {
//    val flow = Pager(
//        // Configure how data is loaded by passing additional properties to
//        // PagingConfig, such as prefetchDistance.
//        PagingConfig(pageSize = 10)
//    ) {
//        PicturesSetPagingSource(this)
//    }.flow
//        .cachedIn(viewModelScope)
//
//    fun savePicturesSet(picturesSetList: List<PicturesSet>) {
//        picturesSetList.forEach {
//            database.getPicturesSetDao().insert(it)
//        }
//    }
}