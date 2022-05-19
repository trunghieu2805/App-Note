package com.example.dack.Adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.dack.EditNoteActivity
import com.example.dack.Model.Note
import com.example.dack.R
import com.google.firebase.database.FirebaseDatabase



class NoteAdapter(var c:Context , private val NoteList : ArrayList<Note> ):
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val titleS = itemView.findViewById<TextView>(R.id.titleShow)
        val contentS = itemView.findViewById<TextView>(R.id.contentShow)
        val delelte = itemView.findViewById<Button>(R.id.deleteBTN)
        val edit = itemView.findViewById<Button>(R.id.EditBTN)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_note_items,parent,false)
            return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val note = NoteList[position]
        holder.titleS.text = note.title
        holder.contentS.text = note.content

        holder.delelte.setOnClickListener{
            val id = note.idnt
            val database = FirebaseDatabase.getInstance().getReference("notes")
            database.child(id).removeValue()


        }
        holder.edit.setOnClickListener {
            val titleEdit = note.title
            val contentEdit = note.content
            val id = note.idnt
            val idUser = note.userId
            val intent = Intent(c,EditNoteActivity::class.java)
            intent.putExtra("title",titleEdit)
            intent.putExtra("content",contentEdit)
            intent.putExtra("idnt",id)
            c.startActivity(intent)


        }


    }




    override fun getItemCount(): Int {
      return NoteList.size
    }

}