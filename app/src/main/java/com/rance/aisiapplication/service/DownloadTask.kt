package com.rance.aisiapplication.service

import android.content.Context
import com.rance.aisiapplication.common.AppDatabase
import com.rance.aisiapplication.model.DownType
import com.rance.aisiapplication.model.PicturesSet
import com.smartmicky.android.data.api.ApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

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
    private val downMap = mutableMapOf<String, String>()
    var failCount = 0
    fun download() {
        failCount = 0
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

    fun writeResponseBodyToDisk(
        dstFile: File,
        body: ResponseBody,
        objects: (loaded: Long, total: Long) -> Unit
    ): Boolean {
        try {
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(dstFile)

                while (true) {
                    val read = inputStream!!.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    objects(fileSizeDownloaded, fileSize)
                }
                outputStream.flush()
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    private fun downFile(url: String) {
        var isfail = false
        try {
            val file =
                File(context.filesDir.absolutePath + File.separator + UUID.randomUUID() + ".jpg")
            apiHelper.downloadFileWithDynamicUrlSync(url).enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val bodyLength = body?.contentLength() ?: -1
                        GlobalScope.launch(Dispatchers.IO) {
                            writeResponseBodyToDisk(file, body!!) { loaded, total ->
                                if (loaded == total) {
                                    downMap.put(url, file.absolutePath)
                                    updatePicturesSet()
                                    downloadListener.onProgress(downMap.size)
                                    if (picturesSet.originalImageUrlList.size == (picturesSet.fileMap.size + failCount)) {
                                        if (failCount == 0) {
                                            picturesSet.downType = DownType.SUCCESS
                                        } else {
                                            picturesSet.downType = DownType.FAIL
                                        }
                                        updatePicturesSet()
                                        downloadListener.onExecuteComplete()
                                    }
                                }
                            }
                        }
                        //下载
                        println("id:${Thread.currentThread().id}name:${Thread.currentThread().name}" + url)
                    } else {
                        isfail = true
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                    isfail = true
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            isfail = true
        }
        if (isfail) {
            failCount++
            if (picturesSet.originalImageUrlList.size == (picturesSet.fileMap.size + failCount)) {
                if (failCount == 0) {
                    picturesSet.downType = DownType.SUCCESS
                } else {
                    picturesSet.downType = DownType.FAIL
                }
                updatePicturesSet()
                downloadListener.onExecuteComplete()
            }
        }

    }

    fun updatePicturesSet() {
        GlobalScope.launch(Dispatchers.IO) {
            database.getPicturesSetDao().update(picturesSet)
        }
    }
}