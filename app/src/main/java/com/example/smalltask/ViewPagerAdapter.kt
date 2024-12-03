package com.example.smalltask

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.smalltask.fragments.HomepageFragment
import com.example.smalltask.fragments.SettingsFragment
import com.example.smalltask.fragments.StatsFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        HomepageFragment(),
        StatsFragment(),
        SettingsFragment()
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}