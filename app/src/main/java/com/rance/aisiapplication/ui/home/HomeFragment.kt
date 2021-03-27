package com.rance.aisiapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rance.aisiapplication.databinding.FragmentHomeBinding
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.ui.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment() {

    lateinit var binding: FragmentHomeBinding
    private val picturesSetAdapter =
        PicturesSetAdapter(PicturesSetAdapter.Companion.PicturesSetComparator, this)

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeViewModel = getViewModel(HomeViewModel::class.java)

        val layoutManager  = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE;
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager.invalidateSpanAssignments(); //防止第一行到顶部有空白区域
            }
        });

        binding.recyclerView.apply {
            adapter = picturesSetAdapter
            this.layoutManager = layoutManager
        }


        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel?.flow?.collectLatest { pagingData ->
                picturesSetAdapter.submitData(pagingData)
            }
        }

//
//        val job = GlobalScope.launch {
//            showLoading()
//            try {
//                val time = System.currentTimeMillis()
////                https://www.24tupian.org/meinv_2.html
//                val document = Jsoup.connect("https://www.24tupian.org/meinv").ignoreContentType(false)
//                    .method(Connection.Method.GET)
//                    .execute().body()
//                picturesSetList = PicturesSet.htmlToPicturesSetList(document) as ArrayList<PicturesSet>
//                picturesSetList.forEach {
//                    it.parsePicturesSet()
//                }
//                println("总时长${System.currentTimeMillis() - time}")
//                GlobalScope.launch(Dispatchers.Main) {
//                    showContent()
//                    this@HomeFragment.toast("采集完成")
//                }
//            }catch (e:Exception){
//                showError()
//                Log.e("whc","采集出错")
//            }
//        }
//        baseBinding.layoutError.root.setSingleClickListener {
//            job.start()
//        }
    }
}