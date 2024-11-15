package com.example.smalltask.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.R
import com.example.smalltask.learning.Word

class WordAdapter(private val wordList: List<Word>):
    RecyclerView.Adapter<WordAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val wordName : TextView = view.findViewById(R.id.wordName)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_finish_word, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = wordList[position]
        holder.wordName.text = word.word
    }

    override fun getItemCount(): Int = wordList.size
}