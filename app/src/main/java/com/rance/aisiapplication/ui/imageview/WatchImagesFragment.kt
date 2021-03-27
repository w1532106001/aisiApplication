package com.rance.aisiapplication.ui.imageview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rance.aisiapplication.R
import com.rance.aisiapplication.common.loadImage
import com.rance.aisiapplication.databinding.WatchImagesFragmentBinding
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.ui.base.BaseFragment
import java.text.FieldPosition

class WatchImagesFragment : BaseFragment() {

    lateinit var binding: WatchImagesFragmentBinding

    lateinit var imageAdapter: ImageAdapter

    lateinit var picturesSet: PicturesSet

    var position = -1
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
        imageAdapter = ImageAdapter()
        binding.recyclerView.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            if(picturesSet.lastWatchPosition!=0){
                position = picturesSet.lastWatchPosition
            }
            if(position<0||position>picturesSet.originalImageUrlList.size){
                position = 0
            }
            scrollToPosition(position)
        }

        imageAdapter.setList(
            picturesSet.originalImageUrlList
        )
    }

    class ImageAdapter() : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_image) {
        override fun convert(holder: BaseViewHolder, item: String) {
            holder.getView<ImageView>(R.id.imageView).loadImage(item)
        }

    }

}