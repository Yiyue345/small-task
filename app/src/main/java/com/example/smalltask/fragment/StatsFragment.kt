package com.example.smalltask.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.HomepageViewModel
import com.example.smalltask.R
import com.example.smalltask.databinding.FragmentStatsBinding
import com.example.smalltask.items.WordAdapter
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.Word
import java.util.concurrent.TimeUnit

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null

    private val binding get() = _binding!!
    private lateinit var homepageViewModel: HomepageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homepageViewModel = ViewModelProvider(requireActivity())[HomepageViewModel::class.java]
        val dbHelper = MyDatabaseHelper(requireActivity(), "words.db", 1)
        val db = dbHelper.readableDatabase

        val counts = homepageViewModel.getTodayCounts(requireActivity())
        val todayTime = homepageViewModel.getTodayTime(requireActivity())
        val allWords = homepageViewModel.getAllWords(requireActivity())
        val allTime = homepageViewModel.getAllTime(requireActivity())

        binding.todayCountsValues.text = "${counts}个"
        binding.todayTimeValues.text = "${TimeUnit.MILLISECONDS.toMinutes(todayTime)}分"
        binding.allCountsValues.text = "${allWords}个"
        binding.allTimeValues.text = "${TimeUnit.MILLISECONDS.toMinutes(allTime)}分" // 你别急

        var wordList = ArrayList<Word>()
        val cursor = db.query("Word",
            null,
            "id <= ?",
            arrayOf("${homepageViewModel.getAllWords(requireActivity())}"),
            null,
            null,
            null)

        if (cursor.moveToFirst()){ // 真麻烦
            do {
                val word = cursor.getString(cursor.getColumnIndexOrThrow("word"))
                val accent = cursor.getString(cursor.getColumnIndexOrThrow("accent"))
                val meanCn = cursor.getString(cursor.getColumnIndexOrThrow("meanCn"))
                val meanEn = cursor.getString(cursor.getColumnIndexOrThrow("meanEn"))
                val sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence"))
                val sentenceTrans = cursor.getString(cursor.getColumnIndexOrThrow("sentenceTrans"))
                val wordObject = Word(word, accent, meanCn, meanEn, sentence, sentenceTrans)
                wordList.add(wordObject)
            } while (cursor.moveToNext())
        }
        cursor.close()

        val recyclerView: RecyclerView = view.findViewById(R.id.allWordsList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = WordAdapter(wordList)

        var showList = false
        binding.showAllWordsBtn.setOnClickListener {
            showList = !showList
            if (showList) {
                binding.allWordsList.visibility = View.VISIBLE
                binding.showAllWordsBtn.text = "收起所有学习过的词汇"
            }
            else {
                binding.allWordsList.visibility = View.GONE
                binding.showAllWordsBtn.text = "展示所有学习过的词汇" // 得改
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}