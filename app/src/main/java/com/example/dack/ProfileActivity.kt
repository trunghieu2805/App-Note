package com.example.dack

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.dack.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth


    class ProfileActivity : AppCompatActivity() {
        //ViewBinding
        private lateinit var binding: ActivityProfileBinding
        //ActionBar
        private lateinit var actionBar: ActionBar
        //FirebaseAtuth
        private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Configure Actionbar
        actionBar = supportActionBar!!
        actionBar.title="Thông tin"

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //hand click
        binding.logoutBtn.setOnClickListener {
            val sharedPref = getSharedPreferences("noteapp", Context.MODE_PRIVATE)
            sharedPref.edit().remove("userID").apply()
            firebaseAuth.signOut()
            checkUser()
        }
        binding.startBtn.setOnClickListener {
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }
    }

        private fun checkUser(){
            val firebaseUser = firebaseAuth.currentUser
            if(firebaseUser != null){
                val email = firebaseUser.email
                val sharedPref = getSharedPreferences("noteapp", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("userID",firebaseUser!!.uid).apply()
                //set to text view
                binding.emailTv.text = email
            }
            else{
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
        }
}
