package com.example.smalltask.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        word?.let { it1 ->
            if (it1.word != "Ciallo"){
                lifecycleScope.launch(Dispatchers.IO) {
                    val audioFile = word.let { java.io.File(requireActivity().cacheDir, "${it.word}_audio.mp3") }
                    audioFile.let {
                        if (it.exists()) {
                            wordViewModel.playAudio(audioFile)
                        }
                        else {
                            wordViewModel.downloadAndPlayAudio(word.word, requireActivity())
                        }
                    }
                }
            }
            else {
                // 还得加东西
            }

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
            word?.let { it1 ->
                if (it1.word != "Ciallo"){
                    lifecycleScope.launch(Dispatchers.IO) {
                        val audioFile = word.let { java.io.File(requireActivity().cacheDir, "${it.word}_audio.mp3") }
                        audioFile.let {
                            if (it.exists()) {
                                wordViewModel.playAudio(audioFile)
                            }
                            else {
                                wordViewModel.downloadAndPlayAudio(word.word, requireActivity())
                            }
                        }
                    }
                }
                else {
                    // 还得加东西
                }

            }
        }

    }

}