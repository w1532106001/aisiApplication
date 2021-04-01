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
import com.rance.aisiapplication.ui.base.BaseFragment
import com.rance.aisiapplication.ui.downloadlist.DownloadListFragment
import com.rance.aisiapplication.ui.imageview.WatchImagesFragment
import com.smartmicky.android.data.api.ApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardFragment : BaseFragment() {

    @Inject
    lateinit var apiHelper: ApiHelper

    @Inject
    lateinit var database: AppDatabase

    lateinit var binding: FragmentDashboardBinding
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

        binding.jumpButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.add(
                R.id.layout_content,
                DownloadListFragment()
            )?.addToBackStack(null)?.commit()
        }






    }
}