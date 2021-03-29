package com.rance.aisiapplication.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rance.aisiapplication.R
import com.rance.aisiapplication.common.loadImage
import com.rance.aisiapplication.databinding.ItemHomePicturesSetBinding
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.ui.picturesset.PicturesSetFragment

class PicturesSetAdapter(differCallback: PicturesSetComparator, val homeFragment: HomeFragment) :
    PagingDataAdapter<PicturesSet, PicturesSetViewHolder>(differCallback) {
    companion object {
        object PicturesSetComparator : DiffUtil.ItemCallback<PicturesSet>() {

            override fun areItemsTheSame(oldItem: PicturesSet, newItem: PicturesSet): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: PicturesSet, newItem: PicturesSet): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesSetViewHolder {
        val binding =
            ItemHomePicturesSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PicturesSetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PicturesSetViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            item?.let {
                val url = it.cover.replace("imgs","mbig").replace("m24mnorg","24mnorg")
                imageView.loadImage("https://dss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3153405721,1524067674&fm=26&gp=0.jpg")
                nameTextView.text = it.name
                itemLayout.setOnClickListener {
                    homeFragment.activity?.supportFragmentManager?.beginTransaction()?.add(R.id.layout_content,
                        PicturesSetFragment.newInstance(item.url))?.addToBackStack(null)?.commit()
                }
            }
        }
    }

}