package com.example.smalltask.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smalltask.HomepageViewModel
import com.example.smalltask.R
import com.example.smalltask.activities.LearnedWordsActivity
import com.example.smalltask.databinding.FragmentStatsBinding
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.Word
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Locale
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

        val userDbHelper  = MyDatabaseHelper(requireActivity(), "Database${homepageViewModel.username}.db", 1)
        val userDb = userDbHelper.readableDatabase
        val today = System.currentTimeMillis() / 86400000L * 86400000L
        val oneDay = 86400000L

        val counts = homepageViewModel.getTodayCounts(requireActivity(), today)
        val todayTime = homepageViewModel.getTodayTime(requireActivity())
        val allWords = homepageViewModel.getAllWords(requireActivity())
        val allTime = homepageViewModel.getAllTime(requireActivity())

        binding.todayCountsValues.text = getString(R.string.words_amount, counts)
        binding.todayTimeValues.text = getString(R.string.time_amount, TimeUnit.MILLISECONDS.toMinutes(todayTime))
        binding.allCountsValues.text = getString(R.string.words_amount, allWords)
        binding.allTimeValues.text = getString(R.string.time_amount, TimeUnit.MILLISECONDS.toMinutes(allTime))

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

        val learningDaysCursor = userDb.query("LearningRecords",
            null,
            null,
            null,
            null,
            null,
            "id DESC")

        var i = 0
        if (learningDaysCursor.moveToFirst()) {
            do {
                val date = learningDaysCursor.getLong(learningDaysCursor.getColumnIndexOrThrow("date"))
                if (date == today - oneDay * i) {
                    i++
                }
                else if (date > today - oneDay * i) {
                    continue
                }
                else {
                    break
                }
            } while (learningDaysCursor.moveToNext())
        }
        learningDaysCursor.close()

        if (i > 0) {
            binding.daysLearnInRow.text = getString(R.string.how_many_days_learn, i)
        }
        else {
            binding.daysLearnInRow.text = getString(R.string.no_study_today)
        }

        binding.showAllWordsBtn.setOnClickListener {
            val intent = Intent(requireActivity(), LearnedWordsActivity::class.java)
            startActivity(intent)
        }

        val date = MutableList<String>(7) {""}
        for (i in 0 until 7) {
            date[i] = SimpleDateFormat("MM-dd", Locale.getDefault()).format(today - (6 - i) * oneDay)
        }

        val sevenDaysCount = homepageViewModel.getSevenDaysCounts(requireActivity())
        val entries = ArrayList<BarEntry>()
        for (i in 0 until 7) {
            entries.add(BarEntry(i.toFloat(), sevenDaysCount[i].toFloat()))
        }

        val dataSet = BarDataSet(entries, "")
        val data = BarData(dataSet)

        binding.everyDayLearns.data = data

        binding.everyDayLearns.xAxis.setDrawGridLines(false) // 网格线
        binding.everyDayLearns.xAxis.setDrawAxisLine(false)
        binding.everyDayLearns.axisLeft.isEnabled = false // 左边的Y轴
        binding.everyDayLearns.axisRight.isEnabled = false // 右边的Y轴



        val valueFormatter = object : ValueFormatter() { // 顶部显示整数
            override fun getFormattedValue(value: Float): String? {
                return String.format("%.0f", value)
            }
        }

        dataSet.valueFormatter = valueFormatter
        dataSet.setDrawValues(true)
        dataSet.valueTextSize = 10f // 柱子顶部字体大小

//        binding.everyDayLearns.description.text = getString(R.string.stats)
        binding.everyDayLearns.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return date[value.toInt()]
            }
        }
        binding.everyDayLearns.setScaleEnabled(false) // 缩放
        binding.everyDayLearns.barData.barWidth = 0.5f // 宽度
        binding.everyDayLearns.legend.isEnabled = false // 图例
        binding.everyDayLearns.description.isEnabled = false // 介绍
        binding.everyDayLearns.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.everyDayLearns.xAxis.setDrawLabels(true) // 柱子底下的标签
//        binding.everyDayLearns.setTouchEnabled(false) // 触摸
        binding.everyDayLearns.animateY(1000) // Y轴动画
        binding.everyDayLearns.invalidate() // 刷新
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}