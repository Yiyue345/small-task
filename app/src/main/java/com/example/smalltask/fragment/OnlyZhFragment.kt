package com.example.smalltask.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smalltask.R
import com.example.smalltask.databinding.FragmentOnlyEnBinding
import com.example.smalltask.databinding.FragmentOnlyZhBinding


class OnlyZhFragment : Fragment() {
    private var _binding: FragmentOnlyZhBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnlyZhBinding.inflate(inflater, container, false)
        return binding.root
    }

}