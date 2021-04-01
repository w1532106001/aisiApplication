package com.rance.aisiapplication.service

import android.util.Log
import com.rance.aisiapplication.ui.downloadlist.DownloadListFragment

/**
 * 下载管理器
 * @property downloadTaskList MutableList<DownloadTask>
 * @property position Int
 */
class DownloadTaskController : DownloadTaskControllerService {
    var downPicturesSetAdapter: DownloadListFragment.DownPicturesSetAdapter?=null
    private val downloadTaskList = mutableListOf<DownloadTask>()
    var currentTask: DownloadTask? = null

    //添加任务 任务列表没有任务可直接启动下载 任务列表中存在任务进行派对
    fun addDownloadTask(downloadTask: DownloadTask) {
        downloadTaskList.add(downloadTask)
        if (currentTask==null) {
            down()
        }
    }

    /**
     * 执行完成删除自身
     */
    override fun onExecuteComplete(isSuccess:Boolean) {
        downloadTaskList.removeFirst()
        currentTask = null
        if(downloadTaskList.size==0){
            Log.v("whc", "下载队列执行完成")
            return
        }
        down()
    }

    /**
     * 下载 跳过暂停的item
     */
    private fun down() {
        currentTask = downloadTaskList.firstOrNull()
        currentTask?.downloadTaskController = this
        currentTask?.download()
    }

    /**
     * 取消相关url任务
     * @param url String
     */
    fun cancelTask(url: String) {
        //判断取消任务是不是当前下载任务 如果是删除取消当前任务执行下一项
        if(url==currentTask?.picturesSet?.url){
            currentTask?.cancel()
            downloadTaskList.removeFirst()
        }else{
            findTaskByUrl(url)?.apply {
                downloadTaskList.remove(this)
                cancel()
            }
        }
        down()
    }

    /**
     * 暂停所有任务
     */
    fun cancelAllTask() {
        currentTask?.cancel()
        downloadTaskList.clear()
    }

    /**
     * 启动所有下载任务
     */
    fun startAllTask(downloadTaskList:MutableList<DownloadTask>) {
        this.downloadTaskList.clear()
        this.downloadTaskList.addAll(downloadTaskList)
        down()
    }

    private fun findTaskByUrl(url: String): DownloadTask? {
        return downloadTaskList.find { it.picturesSet.url==url }
    }


//    private fun findCurrentInAdapterPosition(): Int {
//        var position = -1
//        downPicturesSetAdapter?.data?.indexOfFirst { it.url==currentTask?.picturesSet?.url  }?.let {
//            position = it
//        }
//        return position
//
//    }
//
//    private fun modifyCurrentTaskViewAsDownloadType(isDownloading:Boolean){
//        val position = findCurrentInAdapterPosition()
//        if(position!=-1){
//            downPicturesSetAdapter?.apply {
//                data[position].let {
//                    it.downloading = isDownloading
//                }
//                notifyItemChanged(position)
//            }
//        }
//    }
}