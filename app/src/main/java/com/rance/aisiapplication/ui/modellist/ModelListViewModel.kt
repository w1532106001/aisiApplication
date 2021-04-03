package com.rance.aisiapplication.ui.modellist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.model.Model
import com.smartmicky.android.data.api.ApiHelper
import javax.inject.Inject

class ModelListViewModel  @Inject constructor(val database: AppDatabase, val apiHelper: ApiHelper) : ViewModel() {
    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 10)
    ) {
        ModelPagingSource(this)
    }.flow
        .cachedIn(viewModelScope)


    fun saveModelList(modelList: List<Model>) {
        modelList.forEach {
            database.getModelDao().insert(it)
        }
    }
}