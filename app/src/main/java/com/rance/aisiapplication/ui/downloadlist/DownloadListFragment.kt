package com.rance.aisiapplication.ui.downloadlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rance.aisiapplication.R
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.databinding.DownloadListFragmentBinding
import com.rance.aisiapplication.model.DownType
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.service.DownloadTask
import com.rance.aisiapplication.service.DownloadTaskController
import com.rance.aisiapplication.ui.base.BaseFragment
import com.smartmicky.android.data.api.ApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class DownloadListFragment : BaseFragment() {

    @Inject
    lateinit var apiHelper: ApiHelper

    @Inject
    lateinit var database: AppDatabase

    lateinit var binding: DownloadListFragmentBinding

    val picturesSet = PicturesSet()

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DownloadListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseBinding.toolbarBase.apply {
            visibility = View.VISIBLE
            title = "下载列表"
        }
        val downloadTaskController  = DownloadTaskController()
//        val downListViewModel = getViewModel(DownloadListViewModel::class.java)
        val downPicturesSetAdapter = DownPicturesSetAdapter().apply {
            setOnItemChildClickListener { _, view, position ->
                val item = data[position]
                when (view.id) {
                    R.id.startButton -> {
                        if(item.waiting){
                            downloadTaskController?.cancelTask(item.url)
                        }else{
                            downloadTaskController?.addDownloadTask(DownloadTask(
                                requireContext(),
                                apiHelper,
                                database,
                                this,
                                position))
                            data[position].waiting = true
                            notifyItemChanged(position)
                        }
                    }
                    R.id.cancelButton ->{
                        downloadTaskController?.cancelTask(item.url)
                    }
                }


            }
        }
        downPicturesSetAdapter.addChildClickViewIds(R.id.cancelButton)
        downPicturesSetAdapter.addChildClickViewIds(R.id.startButton)
        downloadTaskController?.downPicturesSetAdapter = downPicturesSetAdapter
        binding.recyclerView.apply {
            adapter = downPicturesSetAdapter
            layoutManager = LinearLayoutManager(context)
        }
        GlobalScope.launch(Dispatchers.IO) {
            database.getPicturesSetDao().getAllPicturesSet2().apply {
                launch(Dispatchers.Main) {
                    downPicturesSetAdapter.setList(this@apply.filter { it.originalImageUrlList.size>0 })
                }
            }
        }



    }

    class DownPicturesSetAdapter :
        BaseQuickAdapter<PicturesSet, BaseViewHolder>(R.layout.item_download_pictures_set) {
        var speedPerSecondText = ""
        override fun convert(holder: BaseViewHolder, item: PicturesSet) {
            val count = item.fileMap.size
            val total = item.originalImageUrlList.size

            if (item.downloading) {
                holder.setVisible(R.id.downloadingLayout, true)
                holder.setGone(R.id.notDownloadingLayout, true)
                holder.setText(R.id.speedPerSecondTextView, speedPerSecondText)
                holder.setText(R.id.progressTextView, "$count / $total")
                holder.getView<ProgressBar>(R.id.downloadProgressBar).apply {
                    max = total
                    progress = count
                }
            } else {
                holder.setVisible(R.id.notDownloadingLayout, true)
                holder.setGone(R.id.downloadingLayout, true)
                if(item.waiting){
                    holder.setText(R.id.statusTextView, context.getString(R.string.waiting))
                    holder.setImageResource(R.id.startButton,R.drawable.ic_baseline_pause_24)
                }else{
                    holder.setImageResource(R.id.startButton,R.drawable.ic_baseline_play_arrow_24)
                    when (item.downType) {
                        DownType.FAIL -> {
                            holder.setText(R.id.statusTextView, "失败 $count / $total")
                        }
                        DownType.SUCCESS -> {
                            holder.setText(R.id.statusTextView, context.getString(R.string.completed))
                        }
                        else -> {
                            holder.setText(R.id.statusTextView, context.getString(R.string.not_initiated))
                        }
                    }
                }


            }
        }

    }

}