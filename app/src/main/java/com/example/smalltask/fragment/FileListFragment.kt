package com.example.smalltask.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smalltask.BaseActivity
import com.example.smalltask.activities.EditingActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.FragmentFileListBinding
import com.example.smalltask.notes.Note
import com.example.smalltask.notes.NoteAdapter
import com.example.smalltask.notes.NoteDatabaseHelper
import kotlinx.coroutines.launch

class FileListFragment : Fragment() {

    private val noteList = ArrayList<Note>()
    private var _binding: FragmentFileListBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteAdapter: NoteAdapter

    private val startEditingActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            refreshList()
            noteAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFileListBinding.inflate(inflater, container, false)

        val toolbar: Toolbar = binding.toolbar3

        (activity as? BaseActivity)?.setSupportActionBar(toolbar)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.file_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.deleteAll -> {
                        deleteAllNotes()
                        createNoteTable()
                        createRecordTable()
                        refreshList()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        noteAdapter = NoteAdapter(requireContext(), noteList) { note ->
            val intent = Intent(requireContext(), EditingActivity::class.java)
            intent.putExtra("noteId", note.id) // 传递笔记的 ID（如果需要）
            startEditingActivity.launch(intent)
        }
        binding.fileList.layoutManager = LinearLayoutManager(context)
        binding.fileList.adapter = noteAdapter

        binding.newNoteBtn.setOnClickListener {
            val intent = Intent(requireContext(), EditingActivity::class.java)
            startEditingActivity.launch(intent) // 使用 ActivityResultContracts 启动活动
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val dbHelper = context?.let { NoteDatabaseHelper(it, "Notes.db", 1) }
//        val db = dbHelper!!.writableDatabase
//        val cursor = db.query("Note", null, null, null, null, null, null) //
//        createNoteTable()
//        createRecordTable()



//        initNotes()
//        val recyclerView: RecyclerView = binding.fileList
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = NoteAdapter(requireContext(), noteList)



    }

    override fun onResume() {
        super.onResume()
        initNotes()

//        val recyclerView: RecyclerView = binding.fileList
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = NoteAdapter(requireContext(), noteList)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    @SuppressLint("Range")
    private fun initNotes() {
        val dbHelper = NoteDatabaseHelper(requireView().context, "Notes.db", 1)
        val db = dbHelper.writableDatabase

        lifecycleScope.launch {
            noteList.clear()
            val cursor = db.query("Note", null, null, null, null, null, null)
            if (cursor.moveToFirst()) {
                do {
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val content = cursor.getString(cursor.getColumnIndex("content"))
                    val editTime = cursor.getLong(cursor.getColumnIndex("editTime"))
                    noteList.add(Note(title = title, content = content, editTime = editTime))
                } while (cursor.moveToNext())
            }
            cursor.close()

            noteAdapter.notifyDataSetChanged()
        }

    }

    private fun deleteAllNotes() {
        val dbHelper = NoteDatabaseHelper(requireContext(), "Notes.db", 1)
        val db = dbHelper.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS Note")
        db.execSQL("DROP TABLE IF EXISTS Record")
    }

    private fun createNoteTable() {
        val dbHelper = NoteDatabaseHelper(requireContext(), "Notes.db", 1)
        val db = dbHelper.writableDatabase
        db.execSQL("""create table if not exists Note (
                    id integer primary key autoincrement,
                    title text,
                    content text,
                    editTime)""")
    }

    private fun createRecordTable() {
        val dbHelper = NoteDatabaseHelper(requireContext(), "Notes.db", 1)
        val db = dbHelper.writableDatabase
        db.execSQL("""create table if not exists Record (
                    id integer primary key autoincrement,
                    realId integer,
                    title text,
                    content text,
                    isDeleted integer)""")
    }

    private fun refreshList() {
        initNotes()
    }

}