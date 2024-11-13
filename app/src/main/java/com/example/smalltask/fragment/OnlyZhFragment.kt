package com.example.smalltask.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.smalltask.R
import com.example.smalltask.databinding.FragmentOnlyEnBinding
import com.example.smalltask.databinding.FragmentOnlyZhBinding
import com.example.smalltask.items.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.FileOutputStream


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