package com.example.smalltask.learning

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WordViewModel : ViewModel(){
    val words = MutableLiveData<MutableList<Word>>(mutableListOf())
    val number = 0

    lateinit var wordLearningList: MutableLiveData<MutableList<Int>>

    fun addWord(word: Word) {
        words.value?.add(word)
    }
    fun setLearningList(number: Int) {
        wordLearningList = MutableLiveData(MutableList(number) {0})
    }
}