package com.rance.aisiapplication.service

import com.smartmicky.android.data.api.ApiHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class DownloadTask(
    val list: MutableList<String>,
    private val apiHelper: ApiHelper,
    val downloadListener: DownloadListener
) {
    private val threadPool: ExecutorService = Executors.newFixedThreadPool(6)
    private val isPause = AtomicBoolean(false)

    var progress = 0

    fun download() {
        list.forEach {
            threadPool.execute {
//                if (!Thread.interrupted()) {

                if (isPause.get()) {
                    var isRunTask = false
                    while (!isRunTask) {
                        isRunTask = true
                        downFile(it)
                    }
                } else {
                    downFile(it)
                }

//                }

            }
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


    fun pause() {
        isPause.set(true)
    }

    fun resume() {
        isPause.set(false)
    }

    fun cancel() {
        threadPool.shutdownNow()
    }


    private fun downFile(url: String) {
        apiHelper.downloadFileWithDynamicUrlSync(url)?.enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    try {
                        val body = response.body()
                        val bodyLength = body?.contentLength() ?: -1
                        writeResponseBodyToDisk(File(""), body!!) { loaded, total ->
                            val singleProgress: Float =
                                loaded.toFloat() * 100f / total.toFloat()
                            if (singleProgress == 1.0f) {
                                progress += 1
                                downloadListener.onProgress(progress)
                            }
                        }
                        //下载
                        println("id:${Thread.currentThread().id}name:${Thread.currentThread().name}" + url)
                        if (progress == list.size) {
                            downloadListener.onSuccess()
                            println("下载完成")
                        }
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
    }
}