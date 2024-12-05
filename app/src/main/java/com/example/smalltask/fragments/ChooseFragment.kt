package com.example.smalltask.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smalltask.R
import com.example.smalltask.databinding.FragmentChooseBinding
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.WordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChooseFragment : Fragment() {

    private var _binding: FragmentChooseBinding? = null
    private val binding get() = _binding!!

    private lateinit var wordViewModel: WordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val ans = (1..4).random()
    private var choose = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wordViewModel = ViewModelProvider(requireActivity())[WordViewModel::class.java]

        if (wordViewModel.mode == "review") {
            binding.circle1.visibility = View.GONE
            binding.circle2.visibility = View.GONE
            binding.circle3.visibility = View.GONE
        }
        else {
            val score = wordViewModel.wordLearningList.value?.get(wordViewModel.number)
            score?.let {
                val colorStateList = ContextCompat.getColorStateList(requireActivity(), R.color.green)
                if (it >= 1) {
                    binding.circle1.imageTintList = colorStateList
                }
                if (it >= 2) {
                    binding.circle2.imageTintList = colorStateList
                }
                if (it >= 3) {
                    binding.circle3.imageTintList = colorStateList
                }
            }
        }

        val word = wordViewModel.words.value?.get(wordViewModel.number)
        var i = 0
        val wrongAnsList = ArrayList<Int>()
        while (i < 4){
            val randInt = (0..10927).random()
            if (randInt >= wordViewModel.startId && randInt <= wordViewModel.endId) {
                continue
            }
            else {
                wrongAnsList.add(randInt)
                i++
            }

        }
        lifecycleScope.launch(Dispatchers.IO) {
            wordViewModel.playWordAudio(requireActivity())
        }


        word?.let { binding.wordName.text = it.word }
        word?.let { binding.accentTextView.text = it.accent }

        val options = listOf(binding.option1, binding.option2, binding.option3, binding.option4)
        word?.let { options[ans - 1].text = it.meanCn }
        for (i in 0..3) {
            if (i == ans - 1) {
                continue
            }
            else {
                options[i].text = getOneWordMeaningInZh(wrongAnsList[i])
            }
        }

        binding.option1.setOnClickListener {
            choose = 1
            judge()
        }
        binding.option2.setOnClickListener {
            choose = 2
            judge()
        }
        binding.option3.setOnClickListener {
            choose = 3
            judge()
        }
        binding.option4.setOnClickListener {
            choose = 4
            judge()
        }
        binding.showAnsBtn.setOnClickListener {
            judge()
        }

        binding.accentBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                wordViewModel.playWordAudio(requireActivity())
            }
        }
    }

    override fun onStop() {
        super.onStop()
        wordViewModel.stopAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun judge() { // 判断
        if (wordViewModel.mode == "learning") {
            if (choose == ans) {
                wordViewModel.makeProgress(wordViewModel.number) // 对应进度++
            }
            else {
                wordViewModel.wrongAnswer(wordViewModel.number) // 错了就重来
            }
            wordViewModel.wordLearningList.value = wordViewModel.wordLearningList.value
        }

        else if (wordViewModel.mode == "review") {
            if (choose == ans) {
                wordViewModel.wordReviewList.value?.let { it[wordViewModel.number] = 6 }
                wordViewModel.wordReviewTimesList.value?.let { it[wordViewModel.number]++ }
            }
            else {
                wordViewModel.wordReviewTimesList.value?.let { it[wordViewModel.number] += 3 } // 进度不变
            }
            wordViewModel.wordReviewList.value = wordViewModel.wordReviewList.value
        }



    }

    private fun getOneWordMeaningInZh(index: Int): String { // 丑陋，但能跑就行
        val wordDbHelper = MyDatabaseHelper(requireActivity(), "words.db", 1)
        val wordDb = wordDbHelper.readableDatabase

        val wordCursor = wordDb.query("Word",
            null,
            "id = ?",
            arrayOf("$index"),
            null, null, null)
        wordCursor.moveToFirst()
        val meanCn = wordCursor.getString(wordCursor.getColumnIndexOrThrow("meanCn"))
        wordCursor.close()
        return meanCn
    }

}