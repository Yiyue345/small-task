package com.example.smalltask.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Path
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smalltask.HomepageViewModel
import com.example.smalltask.activities.LearningActivity
import com.example.smalltask.activities.SearchActivity
import com.example.smalltask.databinding.HomepageFragmentBinding
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.R
import com.example.smalltask.activities.ReviewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomepageFragment : Fragment() {

    private var _binding: HomepageFragmentBinding? = null

    private val binding get() = _binding!!
    private var homepageViewModel: HomepageViewModel? = null

    private lateinit var reviewLauncher: ActivityResultLauncher<Intent>

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





        var i = 0
        val dbHelper = homepageViewModel?.let { MyDatabaseHelper(requireActivity(), "Database${it.username}.db", 1) }
        val db = dbHelper?.writableDatabase
        val userWordCursor = db!!.query("UserWord", null, null, null, null, null, null)

        reviewLauncher = registerForActivityResult( // 代替某个过时的onActivityResult
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                homepageViewModel!!.flag.value = 0
                Log.d("111", "222")
            }
        }



        if (userWordCursor.moveToFirst()) {

            do {
                val lastTime = userWordCursor.getLong(userWordCursor.getColumnIndexOrThrow("lastTime"))
                val times = userWordCursor.getInt(userWordCursor.getColumnIndexOrThrow("times"))
                val today = (System.currentTimeMillis() / 86400000L) * 86400000L
                if (today > lastTime + (times - 1) / 2 * today) { // (time-1)/2 可能还能改
                    i++
                }
            } while (userWordCursor.moveToNext())
        }
        userWordCursor.close()

        val cursor = db.query("UserInfo", null, null, null, null, null, null)
        var learnWords = 0
        if (cursor.moveToFirst()) {
            learnWords = cursor.getInt(cursor.getColumnIndexOrThrow("learnWords"))
        }
        cursor.close()
        val total = 10928
        binding.reviewNumber.text = "$i"
        binding.learningNumber.text = "${total - learnWords}"

        homepageViewModel!!.flag.observe(requireActivity()) { _ -> // 刷新数据
            val userWordCursor = db.query("UserWord", null, null, null, null, null, null)
            i = 0
            if (userWordCursor.moveToFirst()) {

                do {
                    val lastTime = userWordCursor.getLong(userWordCursor.getColumnIndexOrThrow("lastTime"))
                    val times = userWordCursor.getInt(userWordCursor.getColumnIndexOrThrow("times"))
                    val today = (System.currentTimeMillis() / 86400000L) * 86400000L
                    if (today > lastTime + (times - 1) / 2 * today) { // (time-1)/2 可能还能改
                        i++
                    }
                } while (userWordCursor.moveToNext())
            }
            userWordCursor.close()

            val cursor = db.query("UserInfo", null, null, null, null, null, null)
            var learnWords = 0
            if (cursor.moveToFirst()) {
                learnWords = cursor.getInt(cursor.getColumnIndexOrThrow("learnWords"))
            }
            cursor.close()

            binding.reviewNumber.text = "$i"
            binding.learningNumber.text = "${total - learnWords}"
        }

        binding.review.setOnClickListener {

            if (i > 0) {
                val intent = Intent(requireActivity(), ReviewActivity::class.java)
                reviewLauncher.launch(intent)
            }
            else {
                Toast.makeText(requireActivity(), getString(R.string.no_word_can_review_toast),
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.learn.setOnClickListener {
            val intent = Intent(requireContext(), LearningActivity::class.java)
            reviewLauncher.launch(intent)
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
        binding.randomWordBtn.setOnClickListener {
            val intent = Intent(requireActivity(), SearchActivity::class.java)
            intent.putExtra("word", randWord)
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun reloadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .detach(fragment)
            .attach(fragment)
            .commit()
    }
}