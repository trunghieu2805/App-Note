package com.example.dack

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.dack.Model.User
import com.example.dack.databinding.ActivitySignupBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    //Firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private var email =""
    private var pass =""
    private var pass2 =""
    //Database
    private lateinit var database: DatabaseReference
    //ViewBind
    private lateinit var binding: ActivitySignupBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
       binding = ActivitySignupBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //configure Actionbar
        actionBar = supportActionBar!!
        actionBar.title="Đăng ký"
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)


        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Xin chờ một xí ")
        progressDialog.setMessage("Đang tạo tài khoản ...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //handclick
        binding.signupBtn.setOnClickListener{
            validateData()
        }

        binding.haveAcc.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }
    private fun  validateData(){
        //getData
        email = binding.emailEt.text.toString().trim()
        pass = binding.passEt.text.toString().trim()
        pass2 = binding.passEt2.text.toString().trim()

        //validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.error = "Không đúng cú pháp email"
        }
        else if(TextUtils.isEmpty(pass)){
            binding.passEt.error ="Nhập mật khẩu"
        }
        else if(pass.length <6){
            binding.passEt.error="Mật khẩu phải dài hơn 6 ký tự"
        }
        else if(pass != pass2){
            binding.passEt2.error="Mật khẩu chưa chính xác"
        }
        else{
            firebaseSignUp()
        }
    }

    private fun  firebaseSignUp(){

        //show progress
        progressDialog.show()
        database = FirebaseDatabase.getInstance().getReference("users")
        //create acc
        firebaseAuth.createUserWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                progressDialog.dismiss()

                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                val fullname = binding.fullNameEt.text.toString()
                val key = firebaseUser.uid
                database.child(key).setValue(User(fullname = binding.fullNameEt.text.toString(),email = binding.emailEt.text.toString(), pass =binding.passEt.text.toString() ))
                Toast.makeText(this,"Đăng ký với email $email",Toast.LENGTH_SHORT).show()

                startActivity(Intent(this,ProfileActivity::class.java))
                finish()

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Đăng ký thất bại ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}