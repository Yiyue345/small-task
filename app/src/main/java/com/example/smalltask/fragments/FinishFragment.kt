package com.example.smalltask.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.R
import com.example.smalltask.databinding.FragmentFinishBinding
import com.example.smalltask.items.WordAdapter
import com.example.smalltask.learning.WordViewModel

class FinishFragment : Fragment() {

    private var _binding: FragmentFinishBinding? = null

    private val binding get() = _binding!!
    private lateinit var wordViewModel: WordViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wordViewModel = ViewModelProvider(requireActivity())[WordViewModel::class.java]

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = wordViewModel.words.value?.let { WordAdapter(it) }

        binding.continueBtn.setOnClickListener {
            wordViewModel.finish.value = true
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}