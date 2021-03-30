package com.rance.aisiapplication.service

import android.content.Context
import com.rance.aisiapplication.model.PicturesSet
import com.smartmicky.android.data.api.ApiHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class DownloadTask(
    val picturesSet: PicturesSet,
    private val apiHelper: ApiHelper,
    val downloadListener: DownloadListener,
    val context: Context
) {
    lateinit var threadPool: ExecutorService
    private val isPause = AtomicBoolean(false)
    private val lock = Object()
    private val downMap = mutableMapOf<String, String>()

    fun download() {
        threadPool = Executors.newFixedThreadPool(6)
        picturesSet.originalImageUrlList.forEach {
            if (!picturesSet.fileMap.keys.contains(it)) {
                threadPool.execute {
                    if (isPause.get()) {
                        synchronized(lock) {
                            lock.wait()
                        }
                    }
                    Thread.sleep(3000)
                    downFile(it)
                }
            }
        }
        // 所有任务执行完成且等待队列中也无任务关闭线程池
        threadPool.shutdown()
        // 阻塞主线程, 直至线程池关闭
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            println("执行完成")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun pause() {
        isPause.set(true)
    }

    fun resume() {
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
                        try {
                            val body = response.body()
                            val bodyLength = body?.contentLength() ?: -1
                            writeResponseBodyToDisk(file, body!!) { loaded, total ->
                                if (loaded == total) {
                                    downMap.put(url, file.absolutePath)
                                    downloadListener.onProgress(downMap.size)
                                }
                            }
                            //下载
                            println("id:${Thread.currentThread().id}name:${Thread.currentThread().name}" + url)
                        } catch (e: Exception) {
                            downloadListener.onFail()
                        }
                    } else {
                        downloadListener.onFail()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                    downloadListener.onFail()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            downloadListener.onFail()
        }

    }
}