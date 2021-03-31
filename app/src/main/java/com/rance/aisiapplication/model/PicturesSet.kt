package com.rance.aisiapplication.model

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jsoup.Jsoup
import java.io.Serializable
import java.util.*

@Entity
class PicturesSet : Serializable {
    @PrimaryKey
    var url = ""
    var cover = ""
    var name = ""

    /**
     * 多少张
     */
    var quantity = 0
    var fileSize = ""
    var releaseTime = ""
    var clickNum = 0
    var downNum = 0

    /**
     * 社团名
     */
    var associationName = ""

    /**
     * 社团地址
     */
    var associationUrl = ""
    var modelName = ""
    var modelUrl = ""

    /**
     * 缩略
     */
    var thumbnailUrlList: MutableList<String> = arrayListOf()

    /**
     * 原图
     */
    var originalImageUrlList: MutableList<String> = arrayListOf()

    /**
     * 解析时间
     */
    var createTime = Date()
    var updateTime = Date()

    var lastWatchTime = Date()

    var lastWatchPosition = 0

    var watchNum = 0

    var downType = DownType.NONE

    var fileMap = mutableMapOf<String, String>()

    companion object {
        fun htmlToPicturesSetList(data: String): List<PicturesSet> {
            val picturesSetList = arrayListOf<PicturesSet>()
            var picturesSet: PicturesSet
            try {
                val html = Jsoup.parse(data)
                val cells = html.getElementsByClass("lbl")
                cells.forEach { element ->
                    picturesSet = PicturesSet()
                    val cover = element.select("div.ll > a > img").attr("src")
                    picturesSet.cover = cover
                    val rightBox = element.getElementsByClass("lr")[0]
                    val h4a = rightBox.select("h4 > strong > a")
                    picturesSet.url = h4a.attr("href")
                    picturesSet.name = h4a.text()
                    val pList = element.select("p");
                    pList.forEach { listElement ->
                        val listElementText = listElement.text()
                        if (listElement.html().contains("张")) {
                            val quantity = listElementText.substring(
                                listElementText.indexOf("共有") + 2,
                                listElementText.indexOf("张")
                            )
                            val fileSize =
                                listElementText.substring(listElementText.indexOf("大小") + 2);
                            picturesSet.quantity = quantity.trim().toInt()
                            picturesSet.fileSize = fileSize.trim();
                        }
                        if (listElement.html().contains("更新时间")) {
                            val releaseTime = listElement.select("span").text();
                            picturesSet.releaseTime = releaseTime.trim();
                        }
                        if (listElement.html().contains("次")) {
                            val spans = listElement.select("span");
                            val clickNum = spans[0].text().trim().replace("次", "")
                            val downNum = spans[1].text().trim().replace("次", "")
                            picturesSet.clickNum = clickNum.trim().toInt()
                            picturesSet.downNum = downNum.trim().toInt()
                        }
                        if (listElement.html().contains("隶属")) {
                            val a = element.select("a")
                            val associationName = a[0].text()
                            val associationUrl = a[0].attr("href");
                            picturesSet.associationName = associationName.trim();
                            picturesSet.associationUrl = associationUrl.trim();
                            if (a.size > 1) {
                                picturesSet.modelName = a[1].text().trim();
                                picturesSet.modelUrl = a[1].attr("href").trim();
                            }
                        }
                    }
                    picturesSetList.add(picturesSet);
                }
            } catch (e: Exception) {
                Log.v("whc", "套图解析异常$e")
            }
            return picturesSetList;
        }


    }



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PicturesSet

        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    override fun toString(): String {
        return "PicturesSet(url='$url', cover='$cover', name='$name', quantity=$quantity, fileSize='$fileSize', updateTime='$updateTime', clickNum=$clickNum, downNum=$downNum, associationName='$associationName', associationUrl='$associationUrl', modelName='$modelName', modelUrl='$modelUrl', thumbnailUrlList=$thumbnailUrlList, originalImageUrlList=$originalImageUrlList, createTime=$createTime, lastWatchTime=$lastWatchTime, lastWatchPosition=$lastWatchPosition, watchNum=$watchNum)"
    }


}