package com.example.smalltask.learning

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WordViewModel : ViewModel(){
    val words = MutableLiveData<MutableList<Word>>(mutableListOf()) // 为啥我要套个livedata
    var number = 0
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
}