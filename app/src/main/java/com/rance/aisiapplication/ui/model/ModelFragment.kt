package com.rance.aisiapplication.ui.model

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
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.common.loadImage
import com.rance.aisiapplication.common.setSingleClickListener
import com.rance.aisiapplication.databinding.ModelFragmentBinding
import com.rance.aisiapplication.model.DownType
import com.rance.aisiapplication.model.Model
import com.rance.aisiapplication.model.PicturesSet
import com.rance.aisiapplication.ui.base.BaseFragment
import com.rance.aisiapplication.ui.imageview.WatchImagesFragment
import com.rance.aisiapplication.ui.picturesset.PicturesSetFragment
import com.smartmicky.android.data.api.ApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.awaitResponse
import javax.inject.Inject

class ModelFragment : BaseFragment() {

    lateinit var binding: ModelFragmentBinding

    var url = ""
    lateinit var thumbnailImageAdapter: ThumbnailImageAdapter

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var apiHelper: ApiHelper

    companion object {
        fun newInstance(url: String): ModelFragment {
            return ModelFragment().apply {
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
        binding = ModelFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = getViewModel(ModelViewModel::class.java)
        viewModel?.initData(url)
        showLoading()
        viewModel?.modelLiveData?.observe(viewLifecycleOwner, {
            it?.let {
                if (it.picturesSetList?.isNullOrEmpty() != false) {
                    //????????????
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val model = htmlToModel(it)
                            viewModel.database.getModelDao()
                                .update(model)
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
                                PicturesSetFragment.newInstance(data[position].url,data[position].cover)
                            )?.addToBackStack(null)?.commit()
                        }
                    }
                    //???????????????????????????
                    binding.apply {
                        nameTextView.text = it.name

                        roundImageView.loadImage(it.cover)
                        thumbnailRecyclerView.apply {
                            layoutManager = GridLayoutManager(context, 4)
                            adapter = thumbnailImageAdapter
                        }
                        thumbnailImageAdapter.addChildClickViewIds(R.id.thumbnailItemLayout)
                        thumbnailImageAdapter.setList(it.picturesSetList)

                    }
                }
            }

        })
    }

    class ThumbnailImageAdapter :
        BaseQuickAdapter<PicturesSet, BaseViewHolder>(R.layout.item_thumbnail_image) {
        override fun convert(holder: BaseViewHolder, item: PicturesSet) {
            holder.getView<ImageView>(R.id.imageView).loadImage(item.cover)
        }

    }

    suspend fun htmlToModel(model: Model): Model {
        try {
            val documentResponse = apiHelper.getHtml("http://www.24tupian.org${model.url}").awaitResponse()
            if (documentResponse.isSuccessful) {
                val html = Jsoup.parse(documentResponse.body())
                val hd1 = html.getElementsByClass("hd1")[0]
                model.cover = hd1
                    .getElementsByClass("mphoto")[0]
                    .select("img")
                    .attr("src")
                val mmsg = hd1.getElementsByClass("mmsg")[0]
                model.name = mmsg.child(2).text().trim()
                val mmsgText = mmsg.text().trim();
                model.name =
                    mmsgText.substring(mmsgText.indexOf("???????????????") + 5, mmsgText.indexOf("???????????????")).trim();
                model.introduction =
                    mmsgText.substring(mmsgText.indexOf("???????????????") + 5, mmsgText.indexOf("???????????????")).trim();
                model.clickNum =
                    mmsgText.substring(mmsgText.indexOf("???????????????") + 5, mmsgText.indexOf("??????")).trim();
                model.downNum =
                    mmsgText.substring(mmsgText.indexOf("???????????????") + 5).replace("???", "").trim();
                val pageElement = html.select("div.page.ps").text();
                val r = Regex("???\\w+???");
                val pageString = r
                    .findAll(pageElement)
                    .toList()[0]
                model.page =
                    pageString.groups[0]?.value?.replace("???", "")?.replace("???", "")?.trim()?.toInt()?:1
                model.picturesSetList = arrayListOf()

                parsePicturesSet(model, html)

                //?????????????????????????????????????????????
                if (model.page > 1) {
                    GlobalScope.launch {
                        repeat(model.page - 1) {
                            val pageUrl =
                                model.url.replace(".html", "") + '_' + (it + 1).toString() + ".html"
                            val pageDocumentResponse = apiHelper.getHtml("http://www.24tupian.org$pageUrl").awaitResponse()
                            if (pageDocumentResponse.isSuccessful) {
                                val pageDocument =
                                    Jsoup.parse(pageDocumentResponse.body().toString())
                               parsePicturesSet(model, pageDocument)
                            } else {
                                throw Exception("????????????????????????")
                            }
                            println("???$it ????????????")
                        }
                    }
                }
                println("$url ????????????")
            }else{
                Log.e("whc", "htmlToModel????????????")

            }




        } catch (e: Exception) {
            Log.e("whc", "htmlToModel????????????$e")

        }
        return model;
    }


    private fun parsePicturesSet(model: Model, html:Document){
        val lis =
            html.getElementsByClass("paihan fl")[0].select("li");
        var picturesSet: PicturesSet
        lis.forEach { element ->
            picturesSet = PicturesSet()
            picturesSet.cover = element.select("img").attr("src")
            val aElement = element.select("a")[1]
            picturesSet.name = aElement.text()
            picturesSet.url = aElement.attr("href")
            picturesSet.clickNum = element.text()
                .substring(
                    element.text().indexOf("?????????") + 3, element.text().lastIndexOf("??????")
                )
                .replace("???", "")
                .trim().toInt()
            picturesSet.downNum = element.text()
                .substring(
                    element.text().lastIndexOf("??????") + 2,
                    element.text().lastIndexOf("???")
                )
                .trim().toInt()
            picturesSet.releaseTime = element.text()
                .substring(element.text().lastIndexOf("???????????????") + 5)
                .trim();
            model.picturesSetList!!.add(picturesSet);
        };
    }
}