package com.rance.aisiapplication.ui.imageview

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rance.aisiapplication.R
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.common.loadImage
import com.rance.aisiapplication.databinding.WatchImagesFragmentBinding
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.ui.base.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.FieldPosition
import javax.inject.Inject

class WatchImagesFragment : BaseFragment() {

    lateinit var binding: WatchImagesFragmentBinding

    lateinit var imageAdapter: ImageAdapter

    lateinit var picturesSet: PicturesSet

    @Inject
    lateinit var database: AppDatabase

    var position = -1

    var watchPosition = 0
    companion object{
        fun newInstance(picturesSet:PicturesSet,position: Int): WatchImagesFragment {
            return WatchImagesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("picturesSet", picturesSet)
                    putInt("position", position)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        picturesSet = requireArguments().getSerializable("picturesSet") as PicturesSet
        position = requireArguments().getInt("position")

    }

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = WatchImagesFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageAdapter = ImageAdapter(picturesSet)
        binding.recyclerView.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            if(position==-1){
                position = picturesSet.lastWatchPosition
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == SCROLL_STATE_IDLE || newState == SCROLL_STATE_DRAGGING) {
                        // DES: 找出当前可视Item位置
                        if (layoutManager is LinearLayoutManager) {
                            val mFirstVisiblePosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition();
                            val mLastVisiblePosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition();
                            watchPosition = (mLastVisiblePosition+mFirstVisiblePosition)/2
                        }
                    }
                }
            })

            scrollToPosition(position)

        }

        imageAdapter.setList(
            picturesSet.originalImageUrlList
        )



    }

    override fun onDestroyView() {
        picturesSet.lastWatchPosition = watchPosition
        GlobalScope.launch(Dispatchers.IO) {
            database.getPicturesSetDao().update(picturesSet)
        }
        super.onDestroyView()
    }

    class ImageAdapter(val picturesSet: PicturesSet) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_image) {

        override fun convert(holder: BaseViewHolder, item: String) {
            if(picturesSet.fileMap.size>0){
                if(picturesSet.fileMap.containsKey(item)){
                    holder.getView<ImageView>(R.id.imageView).setImageURI(Uri.fromFile(File(picturesSet.fileMap[item].toString())))
                }else{
                    holder.getView<ImageView>(R.id.imageView).loadImage(item)
                }
            }else{
                holder.getView<ImageView>(R.id.imageView).loadImage(item)
            }
        }

    }

}