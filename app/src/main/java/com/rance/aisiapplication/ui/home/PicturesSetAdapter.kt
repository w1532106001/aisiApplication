package com.rance.aisiapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rance.aisiapplication.R
import com.rance.aisiapplication.databinding.ItemHomePicturesSetBinding
import com.rance.aisiapplication.model.PicturesSet

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
                nameTextView.text = it.name.substring(0, 1)
                itemLayout.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putSerializable("url", item.url)
                    homeFragment.findNavController()
                        .navigate(R.id.action_navigation_home_to_watchImagesFragment, bundle)
                }
            }
        }
    }

}