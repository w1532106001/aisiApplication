package com.rance.aisiapplication.service

import android.util.Log

/**
 * 暂停item下载下一个
 * @property downloadTaskList MutableList<DownloadTask>
 * @property isDowning Boolean
 * @property position Int
 */
class DownloadTaskController : DownloadTaskControllerService {
    private val downloadTaskList = mutableListOf<DownloadTask>()
    var isDowning = false
    var position = 0
    var currentTask: DownloadTask? = null

    fun addDownloadTask(downloadTask: DownloadTask) {
        downloadTaskList.add(downloadTask)
        if (!isDowning) {
            down()
        }
    }

    fun removeDownloadTask(downloadTask: DownloadTask) {
        downloadTaskList.find { downloadTask.picturesSet.url == it.picturesSet.url }?.let {
            downloadTaskList.remove(it)
        }
    }

    /**
     * 执行完成删除自身
     */
    override fun onExecuteComplete() {
        if (position >= downloadTaskList.size) {
            Log.v("whc", "下载队列执行完成")
            return
        }
        down()
    }

    /**
     * 下载 跳过暂停的item
     */
    private fun down() {
        isDowning = false
        downloadTaskList.find { !it.pause }?.let {
            isDowning = true
            currentTask = it
            it.download()
        }
    }

    /**
     * 暂停item执行下一个下载
     * @param url String
     */
    fun pauseTask(url: String) {
        downloadTaskList.find { it.picturesSet.url == url }?.let {
            it.pause = true
            it.cancel()
        }
    }

    /**
     * 恢复item
     * @param url String
     */
    fun resumeTask(url: String) {
        downloadTaskList.find { it.picturesSet.url == url }?.let {
            it.pause = false
        }
    }

    /**
     * 暂停所有任务
     */
    fun pauseAllTask() {
        currentTask?.picturesSet?.url?.let {
            pauseTask(it)
        }
        downloadTaskList.forEach {
            it.pause = true
        }
    }

    /**
     * 恢复所有任务
     */
    fun resumeAllTask() {
        downloadTaskList.forEach {
            it.pause = false
        }
        down()
    }
}