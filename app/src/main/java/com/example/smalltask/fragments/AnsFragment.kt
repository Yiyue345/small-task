package com.example.smalltask.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smalltask.databinding.FragmentAnsBinding
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.WordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.smalltask.R

class AnsFragment : Fragment() {

    private var _binding: FragmentAnsBinding? = null
    private val binding get() = _binding!!

    private lateinit var wordViewModel: WordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
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

        word?.let {
            if (it.word == "Ciallo") {
                binding.cialloView.loadUrl("https://ciallo.cc/")
                binding.cialloView.setOnTouchListener { _, _ -> true } // 禁用一切触摸事件
                binding.cialloView.visibility = View.VISIBLE
                binding.nextBtn.setTextColor(requireActivity().getColor(R.color.black))
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            wordViewModel.playWordAudio(requireActivity())
        }


        word?.let { binding.wordName.text = it.word }
        word?.let { binding.accentTextView.text = it.accent }
        word?.let { binding.meaningTextView.text = it.meanCn.replace("；  ", "\n") }
        word?.let { binding.meaningEnTextView.text = it.meanEn }
        word?.let { binding.sentenceTextView.text = it.sentence }
        word?.let { binding.sentenceTransTextView.text = it.sentenceTrans }

        binding.nextBtn.setOnClickListener {

            if (wordViewModel.mode == "learning") {
                wordViewModel.wordLearningList.value?.let { it1 ->
                    if (it1[wordViewModel.number] == 3) {
                        val dbHelper = MyDatabaseHelper(
                            requireActivity(),
                            "Database${wordViewModel.username}.db",
                            1
                        )
                        val db = dbHelper.writableDatabase
                        val values = ContentValues().apply { // 这也太长了……
                            wordViewModel.words.value?.get(wordViewModel.number)?.let { it ->
                                put("word", it.word) }
                            put("lastTime", (System.currentTimeMillis() / 86400000L) * 86400000L)
                        }
                        db.insertWithOnConflict("UserWord", null, values, SQLiteDatabase.CONFLICT_IGNORE) // 忽略相同词

                    }
                }

                wordViewModel.wordLearningList.value = wordViewModel.wordLearningList.value
            }
            else if (wordViewModel.mode == "review") {
                wordViewModel.wordReviewList.value?.let {
                    if (it[wordViewModel.number] > 7) {

                        val dbHelper = MyDatabaseHelper(
                            requireActivity(),
                            "Database${wordViewModel.username}.db",
                            1
                        )

                        val db = dbHelper.writableDatabase

                        var times = (9 - (wordViewModel.wordReviewTimesList.value?.get(wordViewModel.counts)
                            ?: 0)) / 3
                        if (times < 0) {
                            times = 0
                        }

                        db.execSQL("UPDATE UserWord SET lastTime = ?, times = times + ? WHERE word = ?",
                            arrayOf(wordViewModel.today, times, wordViewModel.words.value?.get(wordViewModel.number)?.word)

                        )
                        wordViewModel.counts++
                    }
                }
                wordViewModel.wordReviewList.value = wordViewModel.wordReviewList.value
            }

        }

        binding.accentBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                wordViewModel.playWordAudio(requireActivity())
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}