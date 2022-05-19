package com.example.dack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dack.databinding.ActivityEditNoteBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_note.*


class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var database :DatabaseReference
    //ActionBar
    private lateinit var actionBar: ActionBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //configure Actionbar
        actionBar = supportActionBar!!
        actionBar.title="Sửa ghi chú"
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)


        val idNt = intent.getStringExtra("idnt")
        val titleEdit = intent.getStringExtra("title")
        val contentEdit = intent.getStringExtra("content")
        binding.inputNoteTitleEt.setText(titleEdit)
        binding.inputNoteEt.setText(contentEdit)


        binding.EditBtn.setOnClickListener{
            val currentTitle = binding.inputNoteTitleEt.text.toString()
            val currentContent = binding.inputNoteEt.text.toString()
            database =FirebaseDatabase.getInstance().getReference("notes")
            val noteEdit = mapOf<String,String>(
                "content" to currentContent,
                "title" to currentTitle
            )
            if (idNt != null) {
                database.child(idNt).updateChildren(noteEdit).addOnSuccessListener {
                    val intent = Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this,"Sửa thành công",Toast.LENGTH_SHORT).show()
                }

            }
        }


    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}