package com.example.smalltask.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.R
import com.example.smalltask.learning.Word

class WordAdapter(private val wordList: List<Word>):
    RecyclerView.Adapter<WordAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val wordName: TextView = view.findViewById(R.id.wordName)
            val cardBtn: Button = view.findViewById(R.id.cardBtn)
            val wordCard: ConstraintLayout = view.findViewById(R.id.wordCard)
            val meanCn: TextView = view.findViewById(R.id.meanCn)
            val accent: TextView = view.findViewById(R.id.accent)

            fun bind(word: Word) {
                // 就是防止变量被重叠复用
                wordName.text = word.word
                accent.text = word.accent
                meanCn.text = word.meanCn.replace("；  ", "\n")

                if (word.showDetails == true) {
                    wordCard.visibility = View.VISIBLE
                    accent.visibility = View.VISIBLE
//                    line2.visibility = View.VISIBLE
                } else {
                    wordCard.visibility = View.GONE
                    accent.visibility = View.GONE
//                    line2.visibility = View.INVISIBLE
                }

                // 设置按钮点击事件
                cardBtn.setOnClickListener {
                    word.showDetails = !word.showDetails!!
                    // 更新当前项的 UI
                    notifyItemChanged(adapterPosition)
                }
            }
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
        holder.bind(word) // 调用绑定方法
    }

    override fun getItemCount(): Int = wordList.size
}