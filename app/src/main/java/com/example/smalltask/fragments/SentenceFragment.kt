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
import com.example.smalltask.databinding.FragmentSentenceBinding
import com.example.smalltask.learning.WordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SentenceFragment : Fragment() { // learning限定
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

    override fun onStop() {
        super.onStop()
        wordViewModel.stopAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}