package com.example.smalltask.items

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.R

class SomethingAdapter(private val context: Context, private val somethingList: List<Something>) :
    RecyclerView.Adapter<SomethingAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val somethingImage : ImageView = view.findViewById(R.id.somethingImage)
            val somethingTextView : TextView = view.findViewById(R.id.somethingTextview)
            val somethingButton : Button = view.findViewById(R.id.somethingButton)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_something, parent, false)
        val viewHolder = ViewHolder(view)
//        viewHolder.itemView.setOnClickListener{
//            val position = viewHolder.adapterPosition
//            val something = somethingList[position]
//
//        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val something = somethingList[position]
        holder.somethingImage.setImageResource(something.imageId)
        val colorStateList = ContextCompat.getColorStateList(context, R.color.iconColor)
        holder.somethingImage.imageTintList = colorStateList
        holder.somethingTextView.text = something.name

        holder.somethingButton.setOnClickListener{
            context.startActivity(something.jumpTo)
        }

    }

    override fun getItemCount() = somethingList.size

}