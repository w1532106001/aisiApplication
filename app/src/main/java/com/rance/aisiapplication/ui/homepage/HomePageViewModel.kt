package com.rance.aisiapplication.ui.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.ui.home.PicturesSetPagingSource
import com.smartmicky.android.data.api.ApiHelper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomePageViewModel @Inject constructor(val database: AppDatabase, val apiHelper: ApiHelper) : ViewModel() {

    fun getFlow(homePageType: HomePageType): Flow<PagingData<PicturesSet>> {
        return Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(pageSize = 10)
        ) {
            PicturesSetPagingSource(this, homePageType)
        }.flow
            .cachedIn(viewModelScope)
    }

    fun savePicturesSet(picturesSetList: List<PicturesSet>) {
        picturesSetList.forEach {
            database.getPicturesSetDao().insert(it)
        }
    }
}