package com.example.smalltask.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smalltask.activities.LearningActivity
import com.example.smalltask.activities.SearchActivity
import com.example.smalltask.databinding.HomepageFragmentBinding
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.R

class HomepageFragment : Fragment() {

    private var _binding: HomepageFragmentBinding? = null

    private val binding get() = _binding!!

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

        binding.learn.setOnClickListener {
            val intent = Intent(requireContext(), LearningActivity::class.java)
            requireContext().startActivity(intent)
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