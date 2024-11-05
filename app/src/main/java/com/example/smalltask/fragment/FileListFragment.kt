package com.example.smalltask.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.R
import com.example.smalltask.databinding.FragmentFileListBinding
import com.example.smalltask.notes.Note
import com.example.smalltask.notes.NoteAdapter

class FileListFragment : Fragment() {

    private val noteList = ArrayList<Note>()

    private var _binding: FragmentFileListBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFileListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val dbHelper = context?.let { NoteDatabaseHelper(it, "Notes.db", 1) }
//        val db = dbHelper!!.writableDatabase
//        val cursor = db.query("Note", null, null, null, null, null, null) //
        initNotes()
        val recyclerView: RecyclerView = view.findViewById(R.id.fileList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = NoteAdapter(view.context, noteList)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initNotes() {
        noteList.add(Note(title = "test", content = "test", editTime = 0))
        noteList.add(Note(title = "test", content = "test", editTime = 0))
        noteList.add(Note(title = "test", content = "test", editTime = 0))
    }

}