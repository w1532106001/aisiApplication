package com.rance.aisiapplication.ui.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rance.aisiapplication.model.PicturesSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class PicturesSetPagingSource(val homeViewModel: HomeViewModel) : PagingSource<Int, PicturesSet>() {
    override fun getRefreshKey(state: PagingState<Int, PicturesSet>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PicturesSet> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1

            val response = homeViewModel.apiHelper.getHtml("https://www.24tupian.org/tuimo_$nextPageNumber.html").awaitResponse()
            return if(response.isSuccessful){
                val picturesSetList = PicturesSet.htmlToPicturesSetList(response.body()!!) as ArrayList<PicturesSet>
                GlobalScope.launch(Dispatchers.IO) {
                    homeViewModel.savePicturesSet(picturesSetList)
                }
                LoadResult.Page(
                    data = picturesSetList,
                    prevKey = null, // Only paging forward.
                    nextKey = nextPageNumber + 1
                )
            }else{
                LoadResult.Error(Throwable("获取本页异常"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
        }
        return LoadResult.Error(Throwable("获取本页异常"))
    }

}