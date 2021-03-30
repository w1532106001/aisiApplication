package com.rance.aisiapplication.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rance.aisiapplication.R
import com.rance.aisiapplication.common.toast
import com.rance.aisiapplication.databinding.FragmentDashboardBinding
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.service.DownloadListener
import com.rance.aisiapplication.service.DownloadTask
import com.rance.aisiapplication.ui.base.BaseFragment
import com.smartmicky.android.data.api.ApiHelper
import javax.inject.Inject

class DashboardFragment : BaseFragment(), DownloadListener {

    private lateinit var dashboardViewModel: DashboardViewModel

    @Inject
    lateinit var apiHelper: ApiHelper

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

        val downloadTask = DownloadTask(PicturesSet(), apiHelper, this, requireContext())

        binding.startButton.setOnClickListener {
            downloadTask.download()
        }
        binding.pauseButton.setOnClickListener {
            downloadTask.pause()
        }
        binding.resumeButton.setOnClickListener {
            downloadTask.resume()
        }
        binding.cancelButton.setOnClickListener {
            downloadTask.cancel()
        }
    }

    override fun onProgress(progress: Int) {
        binding.progressTextView.text = "$progress/${list.size}"
    }

    override fun onExecuteComplete() {
        val count = picturesSet.fileMap.size
        val total = picturesSet.originalImageUrlList.size
        toast(
            "执行完成" + if (count == total) {
                "下载完成"
            } else {
                "下载失败 $count / $total"
            }
        )
    }

    override fun onFail() {
        toast("下载失败")
    }


    class itemAdapter : BaseQuickAdapter<PicturesSet, BaseViewHolder>(R.layout.item_download) {
        override fun convert(holder: BaseViewHolder, item: PicturesSet) {
            TODO("Not yet implemented")
        }

    }
}