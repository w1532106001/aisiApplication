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
import javax.inject.Inject

class HomeViewModel @Inject constructor(val database: AppDatabase) : ViewModel() {
    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 20)
    ) {
        PicturesSetPagingSource(this)
    }.flow
        .cachedIn(viewModelScope)

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun savePicturesSet(picturesSetList: List<PicturesSet>) {
        picturesSetList.forEach {
            database.getPicturesSetDao().insert(it)
        }
    }
}