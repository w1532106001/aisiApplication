package com.rance.aisiapplication.ui.modellist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rance.aisiapplication.R
import com.rance.aisiapplication.common.loadImage
import com.rance.aisiapplication.databinding.ItemModelSimpleBinding
import com.rance.aisiapplication.model.Model
import com.rance.aisiapplication.ui.model.ModelFragment

class ModelAdapter(differCallback: ModelComparator, val modelListFragment: ModelListFragment) :
    PagingDataAdapter<Model, ModelViewHolder>(differCallback) {
    companion object {
        object ModelComparator : DiffUtil.ItemCallback<Model>() {

            override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val binding =
            ItemModelSimpleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            item?.let {
                imageView.loadImage(it.cover)
                nameTextView.text = item.name
                itemLayout.setOnClickListener {
                    modelListFragment.activity?.supportFragmentManager?.beginTransaction()?.add(
                        R.id.layout_content,
                        ModelFragment.newInstance(item.url))?.addToBackStack(null)?.commit()
                }
            }
        }
    }

}