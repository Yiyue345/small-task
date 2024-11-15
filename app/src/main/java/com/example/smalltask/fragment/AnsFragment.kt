package com.example.smalltask.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smalltask.databinding.FragmentAnsBinding
import com.example.smalltask.items.File
import com.example.smalltask.learning.WordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wordViewModel = ViewModelProvider(requireActivity())[WordViewModel::class.java]


        val word = wordViewModel.words.value?.get(wordViewModel.number)

        word?.let {
            if (it.word == "Ciallo") {
//                binding.cialloView.settings.javaScriptEnabled = true
                binding.cialloView.loadUrl("https://ciallo.cc/")
                binding.cialloView.setOnTouchListener { _, _ -> true } // 禁用一切触摸事件
                binding.cialloView.visibility = View.VISIBLE
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            wordViewModel.playWordAudio(requireActivity())
        }


        word?.let { binding.wordName.text = it.word }
        word?.let { binding.accentTextView.text = it.accent }
        word?.let { binding.meaningTextView.text = it.meanCn }
        word?.let { binding.meaningEnTextView.text = it.meanEn }
        word?.let { binding.sentenceTextView.text = it.sentence }
        word?.let { binding.sentenceTransTextView.text = it.sentenceTrans }

        binding.nextBtn.setOnClickListener {
            wordViewModel.wordLearningList.value = wordViewModel.wordLearningList.value
        }

        binding.accentBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                wordViewModel.playWordAudio(requireActivity())
            }
        }

    }

}