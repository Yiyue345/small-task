package com.example.smalltask.learning

import android.R.id.input
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smalltask.items.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.FileOutputStream
import java.io.Closeable
import java.io.OutputStream

class WordViewModel : ViewModel(){
    val words = MutableLiveData<MutableList<Word>>(mutableListOf()) // 为啥我要套个livedata
    var number = 0 // 在学第几个
    var counts = 0 // 本轮学完了几个
    var mode = "learning" // 切换学习与复习模式

    var startId = 0
    var endId = 0

    lateinit var wordLearningList: MutableLiveData<MutableList<Int>>

    fun addWord(word: Word) {
        words.value?.add(word)
    }
    fun setLearningList(number: Int) {
        wordLearningList = MutableLiveData(MutableList(number) {0})
    }
    fun makeProgress(index: Int) {
        wordLearningList.value?.let { it[index]++ }
    }
    fun wrongAnswer(index: Int) {
        wordLearningList.value?.let { it[index] = 0 }
    }

    suspend fun downloadAndPlayAudio(wordName: String, context: Context) {
        val audioFile = java.io.File(context.cacheDir, "${wordName}_audio.mp3")
        val url = "http://dict.youdao.com/dictvoice?audio=$wordName"

        val isDownloaded = downloadAudio(context, url, audioFile)
        if (isDownloaded) {
            playAudio(audioFile)
        } else {
            Log.e("AudioDownload", "Failed to download audio")
        }
    }

    private suspend fun downloadAudio(context: Context, url: String, file: java.io.File): Boolean { // 下载
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                Log.d("download", "good")
                response.body?.let { responseBody ->
                    val inputStream = responseBody.byteStream()
                    val outputStream = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.close()
                    inputStream.close()
                    true
                } == true
            } else {
                false
            }
        }
    }

    fun playAudio(file: java.io.File) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(file.absolutePath)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
        }
        mediaPlayer.prepare()
        mediaPlayer.start()
    }


}