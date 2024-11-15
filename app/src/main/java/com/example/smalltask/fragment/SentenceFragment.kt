package com.example.smalltask.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smalltask.databinding.FragmentSentenceBinding
import com.example.smalltask.learning.WordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SentenceFragment : Fragment() {
    private var _binding: FragmentSentenceBinding? = null
    private val binding get() = _binding!!

    private lateinit var wordViewModel: WordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSentenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wordViewModel = ViewModelProvider(requireActivity())[WordViewModel::class.java]

        val word = wordViewModel.words.value?.get(wordViewModel.number)

        lifecycleScope.launch(Dispatchers.IO) {
            wordViewModel.playWordAudio(requireActivity())
        }

        word?.let { binding.wordName.text = it.word }
        word?.let { binding.accentTextView.text = it.accent }
        word?.let { binding.sentenceTextView.text = it.sentence }
        word?.let { binding.sentenceTransTextView.text = it.sentenceTrans }

        binding.showHintBtn.setOnClickListener {
            binding.sentenceTransTextView.visibility = View.VISIBLE
            binding.showHintBtn.visibility = View.GONE
        }

        binding.yesBtn.setOnClickListener {
            wordViewModel.makeProgress(wordViewModel.number)
            wordViewModel.wordLearningList.value = wordViewModel.wordLearningList.value
        }

        binding.noBtn.setOnClickListener { // 两行懒得写函数了
            wordViewModel.wrongAnswer(wordViewModel.number)
            wordViewModel.wordLearningList.value = wordViewModel.wordLearningList.value
        }

        binding.accentBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                wordViewModel.playWordAudio(requireActivity())
            }
        }

    }




}