package com.example.smalltask.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.smalltask.databinding.HomepageFragmentBinding

class HomepageFragment : Fragment() {

    private var _binding: HomepageFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomepageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cannotDoAnymore.setOnClickListener{
            binding.say.visibility = View.GONE
            binding.cannotDoAnymore.visibility = View.GONE
            binding.cry.visibility = View.VISIBLE
            binding.tired.visibility = View.VISIBLE
        }

        binding.cry.setOnClickListener{
            binding.cry.visibility = View.GONE
            binding.tired.visibility = View.GONE
            binding.cannotDoAnymore.visibility = View.VISIBLE
            binding.say.visibility = View.VISIBLE
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}