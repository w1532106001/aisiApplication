package com.rance.aisiapplication.service

import android.content.Context
import android.util.Log
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.model.DownType
import com.rance.aisiapplication.ui.downloadlist.DownloadListFragment
import com.smartmicky.android.data.api.ApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.*
import java.io.*
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class DownloadTask(
    val context: Context,
    private val apiHelper: ApiHelper,
    val database: AppDatabase,
    private val adapter: DownloadListFragment.DownPicturesSetAdapter,
    private val position: Int
) : DownloadTaskService {
    lateinit var threadPool: ExecutorService
    lateinit var downloadTaskController: DownloadTaskController
    private val lock = Object()
    val picturesSet = adapter.data[position]

    var lastUpdateTime = 0L

    var speedPerSecond = AtomicLong(0)

    var lastUpdateSpeedTime = AtomicLong(0)

    //这个好像没必要
    private val failCount = AtomicInteger(0)
    fun download() {
        picturesSet.downloading = true
        picturesSet.waiting = false
        updateView()
        //检测是不是下载完成 完成检测目录文件 未完成下载
        GlobalScope.launch(Dispatchers.IO) {
            failCount.set(0)
            threadPool = Executors.newFixedThreadPool(3)
            picturesSet.originalImageUrlList.forEach {
                if (!picturesSet.fileMap.keys.contains(it)) {
                    threadPool.execute {
                        downFile(it)
                    }
                }
            }
            threadPool.shutdown()
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)
            executeComplete()
        }
    }

    fun cancel() {
        picturesSet.downloading = false
        picturesSet.waiting = false
        updateView()
        if (this::threadPool.isInitialized) {
            threadPool.shutdownNow()
        }
    }

    /**
     * 写入文件到硬盘 上层try catch
     * @param dstFile File
     * @param body ResponseBody
     */
    private fun writeResponseBodyToDisk(
        dstFile: File,
        body: ResponseBody,
    ) {
        val fos = FileOutputStream(dstFile)
        val bis = BufferedInputStream(body.byteStream())
        val buffer = ByteArray(4096)
        var len: Int
        while (((bis.read(buffer)).also { len = it }) != -1) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdateSpeedTime.get() > 1000) {
                lastUpdateSpeedTime.set(currentTime)
                println("更新速度${speedPerSecond.get()}")
                updateSpeedPerSecond(formatSpeed(speedPerSecond.get()))
                speedPerSecond.set(0)
            }
            speedPerSecond.addAndGet(len.toLong())
            fos.write(buffer, 0, len)
        }
        fos.close()
        bis.close()
        body.close()
    }

    private fun downFile(url: String) {
        val file =
            File(
                context.filesDir.absolutePath + File.separator + url.substring(url.lastIndexOf("/") + 1)
                    .replace(".jpg", "").replace(".png", "") + ".jpg"
            )
        try {
            val response = apiHelper.downloadFileWithDynamicUrlSync(
                url.replace(
                    "162.253.155.134:8085",
                    "162.253.155.134:8086"
                )
            ).execute()
            if (response.isSuccessful) {
                writeResponseBodyToDisk(file, response.body()!!)
                picturesSet.fileMap.put(url,file.absolutePath)
                this@DownloadTask.updatePicturesSet()
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUpdateTime > 200) {
                    lastUpdateTime = currentTime
                    updateProgress()
                }
                println("id:${Thread.currentThread().id}name:${Thread.currentThread().name}" + url)
            } else {
                failCount.incrementAndGet()
            }
        } catch (e: Exception) {
            failCount.incrementAndGet()
            e.printStackTrace()
        }
    }

    private fun updatePicturesSet() {
        database.getPicturesSetDao().update(picturesSet)
    }

    /**
     * 更新进度 200毫秒更新一次
     */
    override fun updateProgress() {
        updateView()
    }

    /**
     * 更新速度 每秒更新一次
     */
    override fun updateSpeedPerSecond(speedPerSecond: String) {
        adapter.speedPerSecondText = speedPerSecond
        updateView()
    }

    /**
     * 下载完成
     */
    override fun executeComplete() {
        println("下载线程执行结束 mapSize:${picturesSet.fileMap.size}")
        if (picturesSet.originalImageUrlList.size == (picturesSet.fileMap.size + failCount.get())) {
            if (failCount.get() == 0) {
                picturesSet.downType = DownType.SUCCESS
                downloadTaskController.onExecuteComplete(true)
            } else {
                picturesSet.downType = DownType.FAIL
                downloadTaskController.onExecuteComplete(false)
            }
            picturesSet.downloading = false
            updatePicturesSet()
            cancel()
        }
        Log.v("whc", "下载线程执行结束")
    }

    /**
     * 格式化速度
     */
    private fun formatSpeed(bytes: Long): String {
        if (bytes <= 0) {
            return "0 B/s"
        }
        return when {
            bytes >= 1024 * 1024 -> {
                String.format(Locale.US, "%.2f MB/s", bytes * 1.0 / 1024 / 1024)
            }
            bytes >= 1024 -> {
                String.format(Locale.US, "%.1f KB/s", bytes * 1.0 / 1024)
            }
            else -> {
                String.format(Locale.US, "%d B/s", bytes.toInt())
            }
        }
    }

    private fun updateView() {
        GlobalScope.launch(Dispatchers.Main) {
            adapter.notifyItemChanged(position)
        }
    }
}