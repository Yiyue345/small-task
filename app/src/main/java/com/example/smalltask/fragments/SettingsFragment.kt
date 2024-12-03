package com.example.smalltask.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.EndAll
import com.example.smalltask.R
import com.example.smalltask.activities.LanguageActivity
import com.example.smalltask.activities.ResetPassword
import com.example.smalltask.activities.StudySettingsActivity
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
        val endAllIntent = Intent(requireActivity(), EndAll::class.java)
        val resetPasswordIntent = Intent(requireActivity(), ResetPassword::class.java)
        val userSettingsIntent = Intent(requireActivity(), StudySettingsActivity::class.java)
        val languageIntent = Intent(requireActivity(), LanguageActivity::class.java)
        somethingList.add(Something(getString(R.string.study_settings), R.drawable.settings, userSettingsIntent))
        somethingList.add(Something(getString(R.string.change_password_text), R.drawable.user, resetPasswordIntent))
        somethingList.add(Something(getString(R.string.choose_language_text), R.drawable.language, languageIntent))
        somethingList.add(Something(getString(R.string.quit_text), R.drawable.leave, endAllIntent))

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
