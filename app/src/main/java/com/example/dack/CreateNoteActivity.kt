package com.example.dack

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.dack.Model.Note
import com.example.dack.Model.User
import com.example.dack.databinding.ActivityCreateNoteBinding
import com.example.dack.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class CreateNoteActivity : AppCompatActivity() {
    //Firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private var title =""
    private var content =""
    private lateinit var uid: String
    //Viewbinding
    private lateinit var binding: ActivityCreateNoteBinding
    //Database
    private lateinit var database: DatabaseReference
    //ActionBar
    private lateinit var actionBar: ActionBar
    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //configure Actionbar
        actionBar = supportActionBar!!
        actionBar.title="Ghi chú"
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Xin chờ một xíu")
        progressDialog.setMessage("Đang tạo ghi chú")
        progressDialog.setCanceledOnTouchOutside(false)




        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        binding.insertBTn.setOnClickListener {
            validateData()
        }


    }
    private fun  validateData(){
        //getData
        title = binding.inputNoteTitle.text.toString().trim()
        content= binding.inputNote.text.toString().trim()


        //validate data
        if(TextUtils.isEmpty(title)){
            binding.inputNoteTitle.error ="Chưa nhập tiêu đề"
        }
        else if(TextUtils.isEmpty(content)){
            binding.inputNote.error="Chưa nhập ghi chú"
        }
        else{
            AddNote()
        }
    }
    private fun  AddNote(){
        val upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowerAlphabet = "abcdefghiiklmnopgrstuvwxyz"
        val numbers = "0123456789"
        val alphaNumeric = upperAlphabet + lowerAlphabet + numbers
        val strBuil = StringBuilder ()
        val random = Random()
        val length = 5
        for (i in 0 until length) {

            val indexRan = random.nextInt (alphaNumeric.length)

            val randomChar = alphaNumeric [indexRan]

            strBuil.append (randomChar)

        }
        val sharedPref = getSharedPreferences("noteapp", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userID",null).toString()
        val idNote = strBuil.toString()
        //show progress
        progressDialog.show()
        database = FirebaseDatabase.getInstance().getReference("notes")
        //create note

        database.child(idNote).setValue(Note(idnt = idNote,title = binding.inputNoteTitle.text.toString(),content = binding.inputNote.text.toString(), userId = uid ))
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Thêm thành công",Toast.LENGTH_SHORT).show()

                startActivity(Intent(this,HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Thêm thất bại",Toast.LENGTH_SHORT).show()
            }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}