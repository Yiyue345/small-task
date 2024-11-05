package com.example.smalltask.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.EditingActivity
import com.example.smalltask.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(private val context: Context, private val noteList: List<Note>) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val fileTextview: TextView = view.findViewById(R.id.fileTextview)
            val editTime: TextView = view.findViewById(R.id.editTime)
            val fileButton: Button = view.findViewById(R.id.fileButton)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = noteList[position]
        val date = Date(note.editTime)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(date)
        holder.editTime.text = formattedDate
        holder.fileTextview.text = note.title
        holder.fileButton.setOnClickListener {
            val intent = Intent(context, EditingActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = noteList.size

}