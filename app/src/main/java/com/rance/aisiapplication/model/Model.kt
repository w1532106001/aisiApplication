package com.rance.aisiapplication.model

import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.jsoup.Jsoup
import java.io.Serializable

@Entity
class Model : Serializable {
    @PrimaryKey
    var url: String = ""
    var cover: String? = null
    var name: String? = null
    var clickNum: String? = null
    var downNum: String? = null
    var introduction: String? = null
    var page = 0
    var picturesSetList: ArrayList<PicturesSet>? = null

    companion object{
        fun htmlToModelList(data: String): List<Model> {
            val modelList = arrayListOf<Model>()
            var model: Model
            try {
                val html = Jsoup.parse(data)
                val lis = html.getElementsByClass("model")[0].select("li")
                lis.forEach { element ->
                    model = Model()
                    val aList = element.select("a")
                    val cover = aList[0].select("img").attr("src")
                    model.cover = cover;
                    model.name = aList[1].text();
                    model.url = aList[1].attr("href")
                    val text = element.text();
                    model.clickNum = text
                        .substring(text.indexOf("热度：") + 3, text.indexOf("下载") - 2)
                        .trim();
                    model.downNum = text.substring(text.indexOf("下载") + 3).trim();
                    modelList.add(model);
                };
            } catch (e: Exception) {
                Log.e("whc", "modelList解析异常$e")
            }
            return modelList;
        }

    }


    fun parsePicturesSetList(data: String): List<PicturesSet> {
        val picturesSetList = mutableListOf<PicturesSet>()
        try {
            val html = Jsoup.parse(data)
            val lis =
                html.getElementsByClass("paihan fl")[0].select("li")
            var picturesSet: PicturesSet
            lis.forEach { element ->
                picturesSet = PicturesSet()
                picturesSet.cover = element.select("img").attr("src");
                val aElement = element.select("a")[1];
                picturesSet.name = aElement.text();
                picturesSet.url = aElement.attr("href")
                picturesSet.clickNum = element.text()
                    .substring(
                        element.text().indexOf("浏览：") + 3, element.text().lastIndexOf("下载")
                    )
                    .replace("次", "")
                    .trim().toInt()
                picturesSet.downNum = element.text()
                    .substring(
                        element.text().lastIndexOf("下载") + 2,
                        element.text().lastIndexOf('次')
                    )
                    .trim().toInt()
                picturesSet.releaseTime = element.text()
                    .substring(element.text().lastIndexOf("更新时间：") + 5)
                    .trim()
                picturesSetList.add(picturesSet);
            };
        } catch (e: Exception) {
            Log.e("whc", "parsePicturesSetList解析异常$e")
        }
        return picturesSetList
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Model

        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        return url?.hashCode() ?: 0
    }


}