package com.example.smalltask.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.EndAll
import com.example.smalltask.R
import com.example.smalltask.activities.ResetPassword
import com.example.smalltask.items.Something
import com.example.smalltask.items.SomethingAdapter
import com.example.smalltask.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment() {

    private val somethingList = ArrayList<Something>()

    private var _binding: SettingsFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSomething()
        val recyclerView : RecyclerView = view.findViewById(R.id.settingsList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SomethingAdapter(view.context, somethingList)
    }

    private fun initSomething(){
        somethingList.add(Something("修改密码", R.drawable.user, ResetPassword::class.java))
        somethingList.add(Something("关掉", R.drawable.leave, EndAll::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
