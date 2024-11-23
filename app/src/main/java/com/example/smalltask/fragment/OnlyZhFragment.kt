package com.example.smalltask.fragment

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smalltask.databinding.FragmentOnlyZhBinding
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.WordViewModel


class OnlyZhFragment : Fragment() { // review特供
    private var _binding: FragmentOnlyZhBinding? = null

    private val binding get() = _binding!!
    private lateinit var wordViewModel: WordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnlyZhBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wordViewModel = ViewModelProvider(requireActivity())[WordViewModel::class.java]
        val word = wordViewModel.words.value?.get(wordViewModel.number)

        word?.let { binding.meaningTextView2.text = it.meanCn }

        binding.yesBtn.setOnClickListener {
            wordViewModel.makeReviewProgress(wordViewModel.number, 1)
            wordViewModel.wordReviewList.value = wordViewModel.wordReviewList.value
        }
        binding.fuzzyBtn.setOnClickListener {
            wordViewModel.makeReviewProgress(wordViewModel.number, 2)
            wordViewModel.wordReviewList.value = wordViewModel.wordReviewList.value
        }
        binding.noBtn.setOnClickListener {
            wordViewModel.makeReviewProgress(wordViewModel.number, 3)
            wordViewModel.wordReviewList.value = wordViewModel.wordReviewList.value
        }

    }

    override fun onStop() { // 停止音频播放
        super.onStop()
        wordViewModel.stopAudio()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}