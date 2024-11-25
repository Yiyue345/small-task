package com.example.smalltask.items

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.R
import com.example.smalltask.learning.Word

class LanguageAdapter(val context: Context, private val userLanguageList: List<UserLanguage>):
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val languageBtn: Button = view.findViewById(R.id.languageBtn)
            fun setLanguage(userLanguage: UserLanguage) { // 很多要做的
                val setLanguage = context.getSharedPreferences("latest", MODE_PRIVATE).edit()
                languageBtn.text = userLanguage.message
                setLanguage.apply()
            }

        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userLanguage = userLanguageList[position]

    }

    override fun getItemCount(): Int = userLanguageList.size
}