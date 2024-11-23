package com.example.smalltask.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smalltask.HomepageViewModel
import com.example.smalltask.activities.LearningActivity
import com.example.smalltask.activities.SearchActivity
import com.example.smalltask.databinding.HomepageFragmentBinding
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.R
import com.example.smalltask.activities.ReviewActivity
import com.example.smalltask.learning.Word

class HomepageFragment : Fragment() {

    private var _binding: HomepageFragmentBinding? = null

    private val binding get() = _binding!!
    private var homepageViewModel: HomepageViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomepageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homepageViewModel = ViewModelProvider(requireActivity())[HomepageViewModel::class.java]

        binding.learn.setOnClickListener {
            val intent = Intent(requireContext(), LearningActivity::class.java)
            requireContext().startActivity(intent)
        }



        binding.review.setOnClickListener {
            val dbHelper = homepageViewModel?.let { MyDatabaseHelper(requireActivity(), "Database${it.username}.db", 1) }
            val db = dbHelper?.writableDatabase
            val userWordCursor = db!!.query("UserWord", null, null, null, null, null, null)
            var i = 0
            if (userWordCursor.moveToFirst()) { // word列表初始化

                do {
                    val lastTime = userWordCursor.getLong(userWordCursor.getColumnIndexOrThrow("lastTime"))
                    val times = userWordCursor.getInt(userWordCursor.getColumnIndexOrThrow("times"))
                    val today = (System.currentTimeMillis() / 86400000L) * 86400000L
                    if (today > lastTime + (times - 1) * (times - 1) * today) { // (time-1)^2 还要修改
                        i++
                    }
                } while (userWordCursor.moveToNext())
            }
            userWordCursor.close()
            if (i > 0) {
                val intent = Intent(requireActivity(), ReviewActivity::class.java)
                startActivity(intent)
            }
            else {
                Toast.makeText(requireActivity(), getString(R.string.no_word_can_review_toast),
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.searchWord.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val intent = Intent(requireActivity(), SearchActivity::class.java)
                    intent.putExtra("word", query)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(requireActivity(), getString(R.string.search_empty), Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        val wordDbHelper = MyDatabaseHelper(requireActivity(), "words.db", 1)
        val wordDb = wordDbHelper.readableDatabase
        val randInt = (0..10927).random()

        val wordCursor = wordDb.query("Word",
            null,
            "id = ?",
            arrayOf("$randInt"),
            null, null, null)
        wordCursor.moveToFirst()
        val randWord = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word"))
        wordCursor.close()

        binding.randomWord.text = randWord
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}