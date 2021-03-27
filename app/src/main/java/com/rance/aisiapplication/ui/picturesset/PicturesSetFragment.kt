package com.rance.aisiapplication.ui.picturesset

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rance.aisiapplication.R
import com.rance.aisiapplication.common.loadImage
import com.rance.aisiapplication.common.setSingleClickListener
import com.rance.aisiapplication.databinding.PicturesSetFragmentBinding
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.ui.base.BaseFragment
import com.rance.aisiapplication.ui.imageview.WatchImagesFragment
import com.smartmicky.android.data.api.ApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import retrofit2.awaitResponse

class PicturesSetFragment : BaseFragment() {

    lateinit var binding: PicturesSetFragmentBinding

    var url = ""
    lateinit var thumbnailImageAdapter: ThumbnailImageAdapter

    companion object {
        fun newInstance(url: String): PicturesSetFragment {
            return PicturesSetFragment().apply {
                arguments = Bundle().apply {
                    putString("url", url)
                }
            }
        }
    }

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
        showLoading()
        viewModel?.picturesSetLiveData?.observe(viewLifecycleOwner, {
            if(it==null){
                showError("数据异常")
            }
            it?.let {
                if (it.thumbnailUrlList.isEmpty()) {
                    //开始解析
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            viewModel.database.getPicturesSetDao()
                                .update(parsePicturesSet(it, viewModel.apiHelper))
                        } catch (e: Exception) {
                            launch(Dispatchers.Main) {
                                showError()
                                e.printStackTrace()
                            }
                        }
                    }
                } else {
                    showContent()
                    thumbnailImageAdapter = ThumbnailImageAdapter().apply {
                        setOnItemChildClickListener { _, _, position ->
                            activity?.supportFragmentManager?.beginTransaction()?.add(
                                R.id.layout_content,
                                WatchImagesFragment.newInstance(it, position)
                            )?.addToBackStack(null)?.commit()
                        }
                    }
                    //解析完成初始化页面
                    binding.apply {
                        nameTextView.text = it.name
                        if (it.associationName.isBlank()) {
                            associationNameLayout.visibility = View.GONE
                        } else {
                            associationNameLayout.visibility = View.VISIBLE
                            associationNameTextView.text = it.associationName
                            associationNameTextView.setSingleClickListener {
                                //todo 跳转社团页
                            }
                        }
                        if (it.modelName.isBlank()) {
                            modelNameLayout.visibility = View.GONE
                        } else {
                            modelNameLayout.visibility = View.VISIBLE
                            modelNameTextView.text = it.modelName
                            modelNameTextView.setSingleClickListener {
                                //todo 跳转模特页
                            }
                        }
                        fileSizeTextView.text = it.fileSize
                        releaseTimeTextView.text = it.releaseTime
                        val url = it.cover.replace("imgs", "mbig").replace("m24mnorg", "24mnorg")
                        roundImageView.loadImage(url)
                        thumbnailRecyclerView.apply {
                            layoutManager = GridLayoutManager(context, 4)
                            adapter = thumbnailImageAdapter
                        }
                        thumbnailImageAdapter.addChildClickViewIds(R.id.thumbnailItemLayout)
                        thumbnailImageAdapter.setList(it.thumbnailUrlList)

                        downloadButton.setSingleClickListener {

                        }
                    }
                }
            }

        })
    }

    class ThumbnailImageAdapter :
        BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_thumbnail_image) {
        override fun convert(holder: BaseViewHolder, item: String) {
            holder.getView<ImageView>(R.id.imageView).loadImage(item)
        }

    }

    private suspend fun parsePicturesSet(
        picturesSet: PicturesSet,
        apiHelper: ApiHelper
    ): PicturesSet {
        val currentTime = System.currentTimeMillis()
        //收集第一页数据
        var totalPage = 1
        val url = "https://www.24tupian.org$url"
        try {
            val documentResponse = apiHelper.getHtml(url).awaitResponse()
            if (documentResponse.isSuccessful) {
                val document = Jsoup.parse(documentResponse.body().toString())
                val hd3 = document.getElementsByClass("hd3")[0]
                val gr = hd3.getElementsByClass("gr")

                if (gr.size > 0 && gr[0].text().indexOf("的所有") != -1) {
                    val grText = gr[0].text()
                    picturesSet.modelName = grText.substring(0, grText.indexOf("的所有") - 3);
                    picturesSet.modelUrl = gr[0].attr("href")
                }

                val time = hd3.text().substring(hd3.text().indexOf("发布时间：") + 5).trim();
                if (time.isNotEmpty()) {
                    picturesSet.releaseTime = time;
                }
                picturesSet.thumbnailUrlList.clear();
                picturesSet.originalImageUrlList.clear();

                //保存当前第一页的图片
                val lis = document.select("div.gtps.fl > ul > li")
                lis.forEach { element ->
                    picturesSet.thumbnailUrlList.add(element.select("img").attr("src"))
                }

                val aArray = document.getElementsByClass("page ps").select("a")
                totalPage = aArray[aArray.size - 3].text().trim().toInt()

                //等待第一页完成处理采集每页任务
                if (totalPage != 1) {
                    val pageJob = GlobalScope.launch {
                        repeat(totalPage) {
                            Log.v("whc","采集第${it}页时间：${System.currentTimeMillis()}")
                            val pageUrl = url.replace(".html", "") + '_' + it.toString() + ".html"
                            val pageDocumentResponse = apiHelper.getHtml(pageUrl).awaitResponse()
                            if (pageDocumentResponse.isSuccessful) {
                                val pageDocument =
                                    Jsoup.parse(pageDocumentResponse.body().toString())
                                val lis = pageDocument.select("div.gtps.fl > ul > li")
                                lis.forEach { element ->
                                    picturesSet.thumbnailUrlList.add(
                                        element.select("img").attr("src")
                                    )
                                }
                            } else {
                                throw Exception("套图分页采集异常")
                            }
                        }
                    }
                    pageJob.join()
                    picturesSet.thumbnailUrlList.forEach { element ->
                        val lastIndex = element.lastIndexOf("/")
                        val originalImageUrl = element.replaceRange(lastIndex, lastIndex + 2, "/")
                        picturesSet.originalImageUrlList.add(
                            originalImageUrl.replace(
                                "imgs",
                                "mbig"
                            )
                        )
                    }
                }
            } else {
                throw Exception("套图分页采集异常")
            }


        } catch (e: Exception) {
            Log.v("whc", "套图分页采集异常$e")
            throw Exception("套图分页采集异常")
        }
        Log.v("whc", "$url 分页采集时间 ${System.currentTimeMillis() - currentTime}")
        return picturesSet
    }

    /**
     * 下载文件
     */
    private fun downloadPicturesSet(picturesSet: PicturesSet){

    }
}