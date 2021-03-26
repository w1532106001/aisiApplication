package com.rance.aisiapplication.ui.picturesset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rance.aisiapplication.databinding.PicturesSetFragmentBinding
import com.rance.aisiapplication.ui.base.BaseFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PicturesSetFragment : BaseFragment() {

    lateinit var binding: PicturesSetFragmentBinding

    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = requireArguments().getString("url").toString()

    }

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = PicturesSetFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = getViewModel(PicturesSetViewModel::class.java)
        viewModel?.initData(url)
        viewModel?.picturesSetLiveData?.observe(viewLifecycleOwner, {
            it?.let {
                if (it.thumbnailUrlList.isEmpty()) {
                    //开始解析
                    GlobalScope.launch {
                        val job = launch {
                            it.parsePicturesSet()
                        }
                        job.join()
                        viewModel.database.getPicturesSetDao().update(it)

                    }
                } else {
                    //解析完成初始化页面
                    binding.textView.text = it.thumbnailUrlList.size.toString()
                }
            }

        })
    }

}