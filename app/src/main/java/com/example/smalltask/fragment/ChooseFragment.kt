package com.example.smalltask.fragment

import android.R.attr.value
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.smalltask.databinding.FragmentChooseBinding
import com.example.smalltask.learning.WordViewModel


class ChooseFragment : Fragment() {

    private var _binding: FragmentChooseBinding? = null
    private val binding get() = _binding!!

    private lateinit var wordViewModel: WordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wordViewModel = ViewModelProvider(requireActivity())[WordViewModel::class.java]
//        Log.d("123", wordViewModel.words.value.toString())
        val word = wordViewModel.words.value?.get(wordViewModel.number)

        word?.let { binding.wordName.text = it.word }
        word?.let { binding.phoneticTextView.text = it.accent }

        binding.showAnsBtn.setOnClickListener {

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}