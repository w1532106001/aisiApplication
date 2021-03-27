package com.rance.aisiapplication.model

import android.util.Log
import org.jsoup.Jsoup

class Model {
    var cover: String? = null
    var name: String? = null
    var clickNum: String? = null
    var downNum: String? = null
    var url: String? = null
    var introduction: String? = null
    var page = 0
    var picturesSetList: ArrayList<PicturesSet>? = null

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

    fun htmlToModel(data: String): Model {
        val model: Model = Model()
        try {
            val html = Jsoup.parse(data)
            val hd1 = html.getElementsByClass("hd1")[0]
            model.cover = hd1
                .getElementsByClass("mphoto")[0]
                .select("img")
                .attr("src")
            val mmsg = hd1.getElementsByClass("mmsg")[0]
            model.name = mmsg.child(2).text().trim()
            val mmsgText = mmsg.text().trim();
            model.name =
                mmsgText.substring(mmsgText.indexOf("模特姓名：") + 5, mmsgText.indexOf("模特介绍：")).trim();
            model.introduction =
                mmsgText.substring(mmsgText.indexOf("模特介绍：") + 5, mmsgText.indexOf("模特热度：")).trim();
            model.clickNum =
                mmsgText.substring(mmsgText.indexOf("模特热度：") + 5, mmsgText.indexOf("点击")).trim();
            model.downNum =
                mmsgText.substring(mmsgText.indexOf("总共下载：") + 5).replace("次", "").trim();
            val pageElement = html.select("div.page.ps").text();
            val r = Regex("共\\w+页");
            val pageString = r
                .findAll(pageElement)
                .toList()[0]
            model.page =
                pageString.groups[0].toString().replace("共", "").replace("页", "").trim().toInt()

            val lis =
                html.getElementsByClass("paihan fl")[0].select("li");
            model.picturesSetList = arrayListOf()
            var picturesSet: PicturesSet
            lis.forEach { element ->
                picturesSet = PicturesSet()
                picturesSet.cover = element.select("img").attr("src")
                val aElement = element.select("a")[1]
                picturesSet.name = aElement.text()
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
                        element.text().lastIndexOf("次")
                    )
                    .trim().toInt()
                picturesSet.releaseTime = element.text()
                    .substring(element.text().lastIndexOf("更新时间：") + 5)
                    .trim();
                model.picturesSetList!!.add(picturesSet);
            };
        } catch (e: Exception) {
            Log.e("whc", "htmlToModel解析异常$e")

        }
        return model;
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
}