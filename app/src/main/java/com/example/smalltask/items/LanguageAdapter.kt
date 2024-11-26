package com.example.smalltask.items

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.EndAll
import com.example.smalltask.MainActivity
import com.example.smalltask.R
import java.util.Locale

class LanguageAdapter(val context: Context, private val userLanguageList: List<UserLanguage>):
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val languageBtn: Button = view.findViewById(R.id.languageBtn)
            fun setLanguage(userLanguage: UserLanguage) { // 很多要做的
                languageBtn.text = userLanguage.message
                languageBtn.setOnClickListener {
                    val currentLocale = context.resources.configuration.locales[0]
                    val selectedLocale = Locale(userLanguage.language, userLanguage.region)
                    if (selectedLocale == currentLocale) { // 首选语言
                        Toast.makeText(context, context.getString(R.string.same_language_hint, userLanguage.name),
                            Toast.LENGTH_SHORT).show()
                    }
                    else {
                        AlertDialog.Builder(context).apply {
                            setTitle(R.string.switch_language_title)
                            setMessage(context.getString(R.string.switch_language_message, userLanguage.name))
                            setPositiveButton(R.string.yes) { dialog, which ->
                                val setLanguage = context.getSharedPreferences("latest", MODE_PRIVATE).edit()
                                setLanguage.putString("language", userLanguage.language)
                                setLanguage.putString("region", userLanguage.region)
                                setLanguage.apply()
                                val intent = Intent(context, EndAll::class.java)
                                intent.putExtra("clean", false)
                                val start = Intent(context, MainActivity::class.java)
                                context.startActivity(start)
                                context.startActivity(intent)

                            }
                            setNegativeButton(R.string.no) { dialog, which ->

                            }
                            show()
                        }

                    }

                }

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
        holder.setLanguage(userLanguage)
    }

    override fun getItemCount(): Int = userLanguageList.size
}