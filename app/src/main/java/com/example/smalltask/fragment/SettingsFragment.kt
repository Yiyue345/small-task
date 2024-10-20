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
import com.example.smalltask.ResetPassword
import com.example.smalltask.Something
import com.example.smalltask.SomethingAdapter

class SettingsFragment : Fragment() {

    private val somethingList = ArrayList<Something>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
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

}
