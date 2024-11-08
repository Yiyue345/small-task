package com.example.smalltask.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Paint
import androidx.navigation.fragment.findNavController
import com.example.smalltask.R
import com.example.smalltask.databinding.FragmentChooseBinding


class ChooseFragment : Fragment() {

    private var _binding: FragmentChooseBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.showAnsBtn.setOnClickListener {

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}