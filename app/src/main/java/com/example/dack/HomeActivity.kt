package com.example.dack

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dack.Adapter.NoteAdapter
import com.example.dack.Model.Note
import com.example.dack.databinding.ActivityHomeBinding
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {
    //Database
    private lateinit var database: DatabaseReference
    private lateinit var uid: String
    //RecylerView
    private lateinit var noteRecylerview:RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var NoteArraylist:ArrayList<Note>
    //ActionBar
    private lateinit var actionBar: ActionBar

    private lateinit var binding: ActivityHomeBinding




    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("noteapp", Context.MODE_PRIVATE)
        uid = sharedPref.getString("userID", null).toString()



        //configure Actionbar
        actionBar = supportActionBar!!
        actionBar.title="App Note"
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        NoteArraylist = ArrayList()
        noteRecylerview = findViewById(R.id.noteRecyclerView)
        noteAdapter =  NoteAdapter(this,NoteArraylist)
        noteRecylerview.adapter = noteAdapter
        noteRecylerview.layoutManager= LinearLayoutManager(this)
        noteRecylerview.visibility = View.VISIBLE
        noteRecylerview.setHasFixedSize(true)

        binding.addBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, CreateNoteActivity::class.java))
        }
        getNoteList()

        binding.layoutSearch.setOnQueryTextListener ( object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0 != "") {
                    searchNote(p0)
                } else {
                    getNoteList()
                }
                return false
            }

        })

    }

    private fun searchNote(title: String?) {
        val sharedPref = getSharedPreferences("notesapp", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", null).toString()
        NoteArraylist.clear()
        database = FirebaseDatabase.getInstance().getReference("notes")
        val query = database.orderByChild("title")
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    if(note!!.title.contains(title.toString()) && note!!.userId == userId) {
                        NoteArraylist.add(note)
                    }
                }
                noteAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun getNoteList(){
        database = FirebaseDatabase.getInstance().getReference("notes")
        val query =database.orderByChild("userId").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                NoteArraylist.clear()
                if(snapshot.exists()){
                    for (noteSnapshot in snapshot.children){
                        val notte = noteSnapshot.getValue(Note::class.java)
                        NoteArraylist.add(notte!!)
                    }
                    noteAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
