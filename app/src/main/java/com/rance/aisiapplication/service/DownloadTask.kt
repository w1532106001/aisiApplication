package com.rance.aisiapplication.service

import android.content.Context
import android.util.Log
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.model.DownType
import com.rance.aisiapplication.model.PicturesSet
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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class DownloadTask(
    val context: Context,
    val picturesSet: PicturesSet,
    private val apiHelper: ApiHelper,
    val database: AppDatabase,
    val downloadListener: DownloadListener,

    ) {
    lateinit var threadPool: ExecutorService
    private val isPause = AtomicBoolean(false)
    private val lock = Object()

    var pause = false
    var lastUpdateTime = 0L
    private val failCount = AtomicInteger(0)
    fun download() {
        GlobalScope.launch(Dispatchers.IO) {
            failCount.set(0)
            threadPool = Executors.newFixedThreadPool(6)
            picturesSet.originalImageUrlList.forEach {
                if (!picturesSet.fileMap.keys.contains(it)) {
                    threadPool.execute {
                        if (isPause.get()) {
                            synchronized(lock) {
                                lock.wait()
                            }
                        }
                        downFile(it)
                    }
                }
            }
            threadPool.shutdown()
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)

            if (picturesSet.originalImageUrlList.size == (picturesSet.fileMap.size + failCount.get())) {
                if (failCount.get() == 0) {
                    picturesSet.downType = DownType.SUCCESS
                } else {
                    picturesSet.downType = DownType.FAIL
                }
                updatePicturesSet()
                downloadListener.onExecuteComplete()
            }
            Log.v("whc", "下载线程执行结束")
        }
    }

    fun pause() {
        picturesSet.downType = DownType.PAUSE
        updatePicturesSet()
        isPause.set(true)
    }

    fun resume() {
        picturesSet.downType = DownType.DOWNING
        updatePicturesSet()
        isPause.set(false)
        synchronized(lock) {
            lock.notifyAll()
        }
    }

    fun cancel() {
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
            val response = apiHelper.downloadFileWithDynamicUrlSync(url).execute()
            if (response.isSuccessful) {
                writeResponseBodyToDisk(file, response.body()!!)
                synchronized(lock) {
                    picturesSet.fileMap.put(url, file.absolutePath)
                    picturesSet.downType = DownType.DOWNING
                    this@DownloadTask.updatePicturesSet()
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastUpdateTime > 200) {
                        lastUpdateTime = currentTime
                        downloadListener.onProgress(picturesSet.fileMap.size)
                    }
                }
                println("id:${Thread.currentThread().id}name:${Thread.currentThread().name}" + url)
            } else {
                failCount.incrementAndGet()
            }
        } catch (e: Exception) {
            failCount.incrementAndGet()
        }
    }

    private fun updatePicturesSet() {
        database.getPicturesSetDao().update(picturesSet)
    }
}