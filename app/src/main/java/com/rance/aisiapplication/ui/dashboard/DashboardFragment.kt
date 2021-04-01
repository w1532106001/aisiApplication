package com.rance.aisiapplication.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rance.aisiapplication.R
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.common.runOnUiThread
import com.rance.aisiapplication.databinding.FragmentDashboardBinding
import com.rance.aisiapplication.model.DownType
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.service.DownloadListener
import com.rance.aisiapplication.service.DownloadTask
import com.rance.aisiapplication.ui.base.BaseFragment
import com.smartmicky.android.data.api.ApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardFragment : BaseFragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    @Inject
    lateinit var apiHelper: ApiHelper

    @Inject
    lateinit var database: AppDatabase

    lateinit var binding: FragmentDashboardBinding
    val picturesSet = PicturesSet()
    val list = arrayListOf<String>(
        "https://dss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3153405721,1524067674&fm=26&gp=0.jpg",
        "https://dss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3355464299,584008140&fm=26&gp=0.jpg",
        "https://dss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3228549874,2173006364&fm=26&gp=0.jpg",
        "https://dss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3225163326,3627210682&fm=26&gp=0.jpg",
        "https://dss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1999921673,816131569&fm=26&gp=0.jpg",
        "https://dss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2151136234,3513236673&fm=26&gp=0.jpg",
        "https://dss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1280325423,1024589167&fm=26&gp=0.jpg",
        "https://dss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1999921673,816131569&fm=26&gp=0.jpg",
        "https://car3.autoimg.cn/cardfs/product/g30/M00/F5/17/240x180_0_q95_c42_autohomecar__ChsEf1_N1TuALc9FACZ2exDVIkk809.jpg",
        "https://car2.autoimg.cn/cardfs/product/g1/M04/0B/F0/240x180_0_q95_c42_autohomecar__ChwFqV8YG-aACch8AAkAdoJoSYM874.jpg"
    )

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentDashboardBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val itemAdapter = ItemAdapter().apply {
            setOnItemChildClickListener { adapter, view, position ->
                var downloadTask: DownloadTask? = null
                val item = data[position]
                when (view.id) {
                    R.id.button -> {
                        when (item.downType) {
                            DownType.NONE -> {
                                downloadTask = DownloadTask(
                                    requireContext(),
                                    item,
                                    apiHelper,
                                    database,
                                    object : DownloadListener {
                                        override fun onProgress(progress: Int) {
                                            runOnUiThread {
                                                notifyItemChanged(position)
                                            }
                                        }

                                        override fun onExecuteComplete() {
                                            runOnUiThread {
                                                notifyItemChanged(position)
                                            }
                                        }
                                    })
                                downloadTask!!.download()
                            }
                            DownType.PAUSE -> {
                                downloadTask?.resume()
                            }
                            DownType.FAIL -> {
                                downloadTask = DownloadTask(
                                    requireContext(),
                                    item,
                                    apiHelper,
                                    database,
                                    object : DownloadListener {
                                        override fun onProgress(progress: Int) {
                                            GlobalScope.launch(Dispatchers.Main) {
                                                notifyItemChanged(position)
                                            }
                                        }

//                                    override fun onFail() {
//                                        notifyItemChanged(position)
//                                    }

                                        override fun onExecuteComplete() {
                                            GlobalScope.launch(Dispatchers.Main) {
                                                notifyItemChanged(position)
                                            }
                                        }
                                    })
                                downloadTask?.download()
                            }
                            DownType.DOWNING -> {
                                downloadTask?.pause()
                            }
                            DownType.SUCCESS -> {

                            }
                        }
                    }
                }


            }
        }
        itemAdapter.addChildClickViewIds(R.id.button)
        binding.recyclerView.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(context)
        }





        GlobalScope.launch(Dispatchers.IO) {
            val picturesSetList = database.getPicturesSetDao().getAllPicturesSet2()
            launch(Dispatchers.Main) {
                itemAdapter.setList(picturesSetList?.take(10))
            }
        }
    }


    class ItemAdapter : BaseQuickAdapter<PicturesSet, BaseViewHolder>(R.layout.item_download) {
        override fun convert(holder: BaseViewHolder, item: PicturesSet) {
            holder.setText(R.id.nameTextView, holder.bindingAdapterPosition.toString())
            val count = item.fileMap.size
            val total = item.originalImageUrlList.size
            when (item.downType) {
                DownType.NONE -> {
                    holder.setText(R.id.nameTextView, "未开始")
                    holder.setText(R.id.button, "下载")
                }
                DownType.DOWNING -> {
                    holder.setText(R.id.nameTextView, "下载中 $count / $total")
                    holder.setText(R.id.button, "暂停")
                }
                DownType.PAUSE -> {
                    holder.setText(R.id.nameTextView, "暂停 $count / $total")
                    holder.setText(R.id.button, "恢复")
                }

                DownType.FAIL -> {
                    holder.setText(R.id.nameTextView, "失败 $count / $total")
                    holder.setText(R.id.button, "继续下载")
                }
                DownType.SUCCESS -> {
                    holder.setText(R.id.nameTextView, "下载完成")
                    holder.setGone(R.id.button, true)
                }
            }
        }

    }
}