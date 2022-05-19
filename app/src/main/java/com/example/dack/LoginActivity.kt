package com.example.dack

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.dack.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    //FirebaseAtuth
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient
    private var email =""
    private var pass = ""
    //Database

    //ViewBinding
    private lateinit var binding: ActivityLoginBinding
    //ActionBar
    private lateinit var actionBar:ActionBar
    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("656260936539-svurj43mnv639su1lfq5spinsfq2ictd.apps.googleusercontent.com")
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this,gso)

        actionBar = supportActionBar!!
        actionBar.title="Login"

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()



        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging In..")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.signInBtn.setOnClickListener{
            signIn()
        }
        binding.register.setOnClickListener{
           signup()
        }
        binding.loginBtn.setOnClickListener {
           validateData()
        }


    }
    private  fun checkUser(){
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            startActivity(Intent(this,ProfileActivity::class.java))
            finish()
        }


    }
    private fun  validateData(){
        //get data
        email = binding.emailEt.text.toString().trim()
        pass = binding.passEt.text.toString().trim()

        //validateData
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.setError("ko thuộc dạng email")
        }
        else if(TextUtils.isEmpty(pass)){
            binding.passEt.error="Nhập mật khẩu"
        }
        else{
            firebaseLogin()
        }


    }
    private  fun firebaseLogin(){
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"Đăng nhập bằng  $email",Toast.LENGTH_SHORT).show()

                //open profile
                startActivity(Intent(this,ProfileActivity::class.java))
                finish()

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Đăng nhập thất bại ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }
    private  fun signup(){
        val intent = Intent(this,SignupActivity::class.java)
        startActivity(intent)
    }
    private fun signIn() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, 100)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val account = task.getResult(ApiException::class.java)!!
                Toast.makeText(this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show()
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,"Đăng nhập thất bại",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.putExtra("luonG", user.displayName)
            intent.putExtra("luonA", user.photoUrl.toString())
            startActivity(intent)
        }
    }
}