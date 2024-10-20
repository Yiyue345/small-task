package com.example.smalltask.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.smalltask.R
import org.w3c.dom.Text

class HomepageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.homepage_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cannotDoAnymore : ImageButton = view.findViewById(R.id.cannotDoAnymore)
        val tired : TextView = view.findViewById(R.id.tired)
        val cry : ImageButton = view.findViewById(R.id.cry)
        val say : TextView = view.findViewById(R.id.say)
        cannotDoAnymore.setOnClickListener{
            say.visibility = View.GONE
            cannotDoAnymore.visibility = View.GONE
            cry.visibility = View.VISIBLE
            tired.visibility = View.VISIBLE
        }

        cry.setOnClickListener{
            cry.visibility = View.GONE
            tired.visibility = View.GONE
            cannotDoAnymore.visibility = View.VISIBLE
            say.visibility = View.VISIBLE
        }



    }
}