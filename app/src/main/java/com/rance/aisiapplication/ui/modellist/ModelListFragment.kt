package com.rance.aisiapplication.ui.modellist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.databinding.FragmentModelListBinding
import com.rance.aisiapplication.ui.base.BaseFragment
import com.smartmicky.android.data.api.ApiHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ModelListFragment : BaseFragment() {

    @Inject
    lateinit var apiHelper: ApiHelper

    @Inject
    lateinit var database: AppDatabase

    lateinit var binding: FragmentModelListBinding

    var modelAdapter = ModelAdapter(ModelAdapter.Companion.ModelComparator, this)

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentModelListBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modelListViewModel = getViewModel(ModelListViewModel::class.java)

        val layoutManager  = GridLayoutManager(context, 4)

        binding.recyclerView.apply {
            adapter = modelAdapter
            this.layoutManager = layoutManager
        }


        viewLifecycleOwner.lifecycleScope.launch {
            modelListViewModel?.flow?.collectLatest { pagingData ->
                modelAdapter.submitData(pagingData)
            }
        }
    }
}