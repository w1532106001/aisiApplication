package com.rance.aisiapplication.ui.downloadlist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.makeramen.roundedimageview.RoundedImageView
import com.rance.aisiapplication.R
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.common.loadImage
import com.rance.aisiapplication.databinding.DownloadListFragmentBinding
import com.rance.aisiapplication.model.DownType
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.service.DownloadTask
import com.rance.aisiapplication.service.DownloadTaskController
import com.rance.aisiapplication.ui.base.BaseFragment
import com.rance.aisiapplication.ui.imageview.WatchImagesFragment
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

    private val picturesSetList = mutableListOf<PicturesSet>()

    private lateinit var downPicturesSetAdapter:DownPicturesSetAdapter

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DownloadListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val downloadTaskController = DownloadTaskController()
        baseBinding.toolbarBase.apply {
            navigationIcon = context.getDrawable(R.drawable.ic_action_back)
            setNavigationOnClickListener {
                activity?.onBackPressed()
            }
            visibility = View.VISIBLE
            title = "????????????"
            inflateMenu(R.menu.down_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.downloadAllButton -> {
//                        downloadTaskController.startAllTask()
                    }
                    R.id.cancelAllButton -> {
                        downloadTaskController.cancelAllTask()
                    }
                }
                true
            }

        }
//        val downListViewModel = getViewModel(DownloadListViewModel::class.java)
        downPicturesSetAdapter = DownPicturesSetAdapter().apply {
            setOnItemChildClickListener { _, view, position ->
                val item = data[position]
                when (view.id) {
                    R.id.startButton -> {
                        if (item.waiting) {
                            downloadTaskController?.cancelTask(item.url)
                        } else {
                            downloadTaskController?.addDownloadTask(
                                DownloadTask(
                                    requireContext(),
                                    apiHelper,
                                    database,
                                    this,
                                    position
                                )
                            )
                            data[position].waiting = true
                            notifyItemChanged(position, DownloadPayloadsEnum.WAITE_STATUS_CHANGE)
                        }
                    }
                    R.id.cancelButton -> {
                        downloadTaskController?.cancelTask(item.url)
                    }
                    R.id.itemLayout -> {
                        activity?.supportFragmentManager?.beginTransaction()?.add(
                            R.id.layout_content,
                            WatchImagesFragment.newInstance(item, -1)
                        )?.addToBackStack(null)?.commit()
                    }
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                val item = data[position]
                when (view.id) {
                    R.id.itemLayout -> {
                        AlertDialog.Builder(requireContext()).setTitle("????????????")
                            .setMessage(item.name).setPositiveButton("??????") { _, _ ->
                                GlobalScope.launch(Dispatchers.IO) {
                                    database.getPicturesSetDao().deleteByUrl(item.url)
                                }
                                adapter.removeAt(position)
                            }
                            .setNegativeButton("??????", null).show()
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }
        downPicturesSetAdapter.addChildClickViewIds(R.id.cancelButton)
        downPicturesSetAdapter.addChildClickViewIds(R.id.startButton)
        downPicturesSetAdapter.addChildClickViewIds(R.id.itemLayout)
        downPicturesSetAdapter.addChildLongClickViewIds(R.id.itemLayout)
        downPicturesSetAdapter.setDiffCallback(DiffPicturesSetCallback())
        downloadTaskController?.downPicturesSetAdapter = downPicturesSetAdapter
        binding.recyclerView.apply {
            adapter = downPicturesSetAdapter
            layoutManager = LinearLayoutManager(context)
        }

        initData()

    }

    fun initData(){
        GlobalScope.launch(Dispatchers.IO) {
            database.getPicturesSetDao().getAllPicturesSet2().apply {
                launch(Dispatchers.Main) {
                    picturesSetList.clear()
                    picturesSetList.addAll(this@apply.filter { it.originalImageUrlList.size > 0 && it.downloadType != DownType.NONE })
                    downPicturesSetAdapter.setList(picturesSetList)
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
            val url = item.cover.replace("imgs", "mbig").replace("m24mnorg", "24mnorg")
            holder.getView<RoundedImageView>(R.id.roundImageView).loadImage(url)
            holder.setText(R.id.nameTextView, item.name)
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
                if (item.waiting) {
                    holder.setText(R.id.statusTextView, context.getString(R.string.waiting))
                    holder.setImageResource(R.id.startButton, R.drawable.ic_baseline_pause_24)
                } else {
                    holder.setImageResource(R.id.startButton, R.drawable.ic_baseline_play_arrow_24)
                    when (item.downloadType) {
                        DownType.FAIL -> {
                            holder.setText(R.id.statusTextView, "?????? $count / $total")
                        }
                        DownType.SUCCESS -> {
                            holder.setText(
                                R.id.statusTextView,
                                context.getString(R.string.completed)
                            )
                        }
                        else -> {
                            holder.setText(
                                R.id.statusTextView,
                                context.getString(R.string.not_initiated)
                            )
                        }
                    }
                }
            }
        }

        override fun convert(holder: BaseViewHolder, item: PicturesSet, payloads: List<Any>) {
            val count = item.fileMap.size
            val total = item.originalImageUrlList.size
            payloads.forEach {
                it as DownloadPayloadsEnum
                when (it) {
                    DownloadPayloadsEnum.DOWNLOAD_STATUS_CHANGE -> {
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
                            if (item.waiting) {
                                holder.setText(
                                    R.id.statusTextView,
                                    context.getString(R.string.waiting)
                                )
                                holder.setImageResource(
                                    R.id.startButton,
                                    R.drawable.ic_baseline_pause_24
                                )
                            } else {
                                holder.setImageResource(
                                    R.id.startButton,
                                    R.drawable.ic_baseline_play_arrow_24
                                )
                                when (item.downloadType) {
                                    DownType.FAIL -> {
                                        holder.setText(R.id.statusTextView, "?????? $count / $total")
                                    }
                                    DownType.SUCCESS -> {
                                        holder.setText(
                                            R.id.statusTextView,
                                            context.getString(R.string.completed)
                                        )
                                    }
                                    else -> {
                                        holder.setText(
                                            R.id.statusTextView,
                                            context.getString(R.string.not_initiated)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    DownloadPayloadsEnum.WAITE_STATUS_CHANGE -> {
                        if (item.waiting) {
                            holder.setText(R.id.statusTextView, context.getString(R.string.waiting))
                            holder.setImageResource(
                                R.id.startButton,
                                R.drawable.ic_baseline_pause_24
                            )
                        } else {
                            holder.setImageResource(
                                R.id.startButton,
                                R.drawable.ic_baseline_play_arrow_24
                            )
                            when (item.downloadType) {
                                DownType.FAIL -> {
                                    holder.setText(R.id.statusTextView, "?????? $count / $total")
                                }
                                DownType.SUCCESS -> {
                                    holder.setText(
                                        R.id.statusTextView,
                                        context.getString(R.string.completed)
                                    )
                                }
                                else -> {
                                    holder.setText(
                                        R.id.statusTextView,
                                        context.getString(R.string.not_initiated)
                                    )
                                }
                            }
                        }
                    }
                    DownloadPayloadsEnum.DOWNLOAD_TYPE_CHANGE -> {
                        when (item.downloadType) {
                            DownType.FAIL -> {
                                holder.setText(R.id.statusTextView, "?????? $count / $total")
                            }
                            DownType.SUCCESS -> {
                                holder.setText(
                                    R.id.statusTextView,
                                    context.getString(R.string.completed)
                                )
                            }
                            else -> {
                                holder.setText(
                                    R.id.statusTextView,
                                    context.getString(R.string.not_initiated)
                                )
                            }
                        }
                    }
                    DownloadPayloadsEnum.PROGRESS_CHANGE -> {
                        holder.setText(R.id.progressTextView, "$count / $total")
                        holder.getView<ProgressBar>(R.id.downloadProgressBar).apply {
                            max = total
                            progress = count
                        }
                    }
                    DownloadPayloadsEnum.SPEED_CHANGE -> {
                        holder.setText(R.id.speedPerSecondTextView, speedPerSecondText)
                    }
                }
            }
        }
    }

    class DiffPicturesSetCallback : DiffUtil.ItemCallback<PicturesSet>() {
        /**
         * ????????????????????????item
         *
         * @param oldItem New data
         * @param newItem old Data
         * @return
         */
        override fun areItemsTheSame(oldItem: PicturesSet, newItem: PicturesSet): Boolean {
            return oldItem.url == newItem.url
        }

        /**
         * ???????????????item???????????????????????????????????????
         *
         * @param oldItem New data
         * @param newItem old Data
         * @return
         */
        override fun areContentsTheSame(oldItem: PicturesSet, newItem: PicturesSet): Boolean {
            return oldItem.fileMap == newItem.fileMap &&
                    oldItem.downloadType == newItem.downloadType
                    && oldItem.downloading == newItem.downloading &&
                    oldItem.waiting == newItem.waiting
        }

        /**
         * ????????????
         * ?????????????????????????????????view????????????????????????????????????
         * ???????????????????????????????????????null???????????????????????????item???
         *
         * @param oldItem Old data
         * @param newItem New data
         * @return Payload info. if return null, the entire item will be refreshed.
         */
        override fun getChangePayload(oldItem: PicturesSet, newItem: PicturesSet): Any? {
            return super.getChangePayload(oldItem, newItem)
        }
    }
}