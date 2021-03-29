package com.rance.aisiapplication.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rance.aisiapplication.common.toast
import com.rance.aisiapplication.databinding.FragmentDashboardBinding
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

    val list = arrayListOf<String>(
        "https://dss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3153405721,1524067674&fm=26&gp=0.jpg",
        "https://dss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3355464299,584008140&fm=26&gp=0.jpg"
    )

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentDashboardBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val downloadTask = DownloadTask(list, apiHelper, this)

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

    override fun onSuccess() {
        toast("下载完成")
    }

    override fun onFail() {
        toast("下载失败")
    }


}