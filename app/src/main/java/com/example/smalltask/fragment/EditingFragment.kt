package com.example.smalltask.fragment

import android.content.ContentValues
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.smalltask.databinding.FragmentEditingBinding
import com.example.smalltask.notes.NoteDatabase
import com.example.smalltask.notes.NoteDatabaseHelper
import com.example.smalltask.notes.NoteRepository
import com.example.smalltask.notes.NoteViewModel
import com.example.smalltask.notes.NoteViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditingFragment : Fragment() {

    private var _binding: FragmentEditingBinding? = null
    private val binding get() = _binding!!

    private var isUpdatingText = false
    private lateinit var textWatcher: TextWatcher



    private val viewModel: NoteViewModel by activityViewModels {
        val database = NoteDatabase.getDatabase(requireContext())
        val noteDao = database.noteDao()
        NoteViewModelFactory(NoteRepository(noteDao))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEditingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHelper = context?.let { NoteDatabaseHelper(it, "Notes.db", 1) }


//        textWatcher = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (!isUpdatingText) {
//                    viewModel.note.value?.let {
//                        // 将当前内容添加到删除列表，以备撤销
//                        viewModel.deletedNotes.value?.add(it)
//                        // 更新笔记内容
//                        viewModel.note.value = Note(
//                            title = binding.titleBox.text.toString(),
//                            content = binding.editBox.text.toString()
//                        )
//                        Log.d("test", "111")
//                    }
//                }
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//
//
//
//            }
//        }


        binding.redoBtn.setOnClickListener {
//            viewModel.redo()

        }

        binding.undoBtn.setOnClickListener {
//            viewModel.undo()

        }

        lifecycleScope.launch {
            var oldTitle = ""
            var oldNote = ""
            while (true) {
                delay(2000)
                if (oldNote != binding.editBox.text.toString() || oldTitle != binding.titleBox.text.toString()){
                    val db = dbHelper!!.writableDatabase
                    oldNote = binding.editBox.text.toString()
                    oldTitle = binding.titleBox.text.toString()

                    val newNote = ContentValues().apply {
//                        put("realId", re)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }




    private fun saveCurrentContent() {
        viewModel.saveNoteContent(binding.editBox.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
