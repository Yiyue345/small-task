package com.example.smalltask.learning

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileOutputStream

class WordViewModel : ViewModel(){
    var username = ""

    val words = MutableLiveData<MutableList<Word>>(mutableListOf()) // 为啥我要套个livedata
    var number = 0 // 在学第几个
    var counts = 0 // 本轮学完了几个
    var mode = "learning" // 切换学习与复习模式

    var startId = 0
    var endId = 0

    var startTime = 0L
    var endTime = 0L

    var finish = MutableLiveData<Boolean>(false)

    lateinit var wordLearningList: MutableLiveData<MutableList<Int>>
    lateinit var wordReviewList: MutableLiveData<MutableList<Int>>
    lateinit var wordReviewTimesList: MutableLiveData<MutableList<Int>> // 认识一分，模糊两分，不认识三分

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

    fun setReviewList(number: Int) {
        wordReviewList = MutableLiveData(MutableList(number) {0})
        wordReviewTimesList = MutableLiveData(MutableList(number) {0})
    }

    private suspend fun downloadAndPlayAudio(wordName: String, context: Context) {
        val audioFile = java.io.File(context.cacheDir, "${wordName}_audio.mp3".replace(Regex("[ /\\\\:*?\"<>|]+"), "_"))
        val url = "http://dict.youdao.com/dictvoice?audio=$wordName"

        val isDownloaded = downloadAudio(url, audioFile)
        if (isDownloaded) {
            playAudio(audioFile)
        } else {
            Log.e("AudioDownload", "Failed to download audio")
        }
    }

    private suspend fun downloadAudio(url: String, file: java.io.File): Boolean { // 下载
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
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

    private fun playAudio(file: java.io.File) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(file.absolutePath)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
        }
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun playWordAudio(context: Context) {
        val word = words.value?.get(number)
        word?.let { it1 ->
            if (it1.word != "Ciallo"){
                viewModelScope.launch(Dispatchers.IO) {
                    val audioFile = word.let { java.io.File(context.cacheDir, "${it.word}_audio.mp3".replace(Regex("[ /\\\\:*?\"<>|]+"), "_")) }
                    audioFile.let {
                        if (it.exists()) {
                            playAudio(audioFile)
                        }
                        else {
                            downloadAndPlayAudio(word.word, context)
                        }
                    }
                }
            }
            else {
                val audioFile = word.let { java.io.File(context.cacheDir, "${it.word}_audio.mp3".replace(Regex("[ /\\\\:*?\"<>|]+"), "_")) }
                playAudio(audioFile)
            }

        }
    }

    fun refreshEndTime() {
        endTime = System.currentTimeMillis()
    }


}