package com.rance.aisiapplication.ui.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rance.aisiapplication.databinding.HomePageFragmentBinding
import com.rance.aisiapplication.ui.base.BaseFragment
import com.rance.aisiapplication.ui.home.PicturesSetAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomePageFragment : BaseFragment() {
    private lateinit var viewModel: HomePageViewModel
    lateinit var binding: HomePageFragmentBinding
    var homePageType = HomePageType.STOCKINGS_BEAUTIFUL_LEGS
    private val picturesSetAdapter =
        PicturesSetAdapter(PicturesSetAdapter.Companion.PicturesSetComparator, this)

    companion object {

        fun newInstance(homePageType: HomePageType): HomePageFragment {
            return HomePageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("homePageType", homePageType)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homePageType = requireArguments().getSerializable("homePageType") as HomePageType
    }

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = HomePageFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeViewModel = getViewModel(HomePageViewModel::class.java)

        val layoutManager  = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

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
            homeViewModel?.getFlow(homePageType)?.collectLatest { pagingData ->
                picturesSetAdapter.submitData(pagingData)
            }
        }
    }

}