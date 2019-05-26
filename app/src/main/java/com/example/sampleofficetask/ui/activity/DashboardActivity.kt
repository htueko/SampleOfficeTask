package com.example.sampleofficetask.ui.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleofficetask.adapter.ListViewHolder
import com.example.sampleofficetask.model.Data
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.input_form.view.*
import java.text.DateFormat
import java.util.*


class DashboardActivity : AppCompatActivity() {

    // fields
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.sampleofficetask.R.layout.activity_dashboard)

        // init
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        val mUser = mAuth.currentUser
        val uId = mUser!!.uid
        mReference = mDatabase.reference.child("NoteTask").child(uId)
        mReference.keepSynced(true)

        // recycler view
        recyclerView = findViewById(com.example.sampleofficetask.R.id.recycler_dashboard)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager


        // passed custom layout to alert dialog
        fab_dashboard.setOnClickListener {
            // alert dialog with custom layout
            val myDialog = AlertDialog.Builder(this@DashboardActivity)
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(com.example.sampleofficetask.R.layout.input_form, null)
            myDialog.setView(view)
            val dialog = myDialog.create()
            dialog.show()

            // event listener for save button
            view.btn_save_input_form.setOnClickListener {
                // get the user input
                val title = view.edt_title_input_form.text.toString().trim()
                val note = view.edt_note_input_form.text.toString().trim()
                // validate the data
                if (!TextUtils.isEmpty(title)) {
                    // title is not empty
                    if (!TextUtils.isEmpty(note)) {
                        // title and note is not empty
                        // id and date
                        val id = mReference.push().key
                        val date = DateFormat.getDateInstance().format(Date())
                        // model class
                        val data = Data(id!!, title, note, date)
                        // push to realtime database with unique key and date
                        mReference.child(id).setValue(data)
                        // dismiss the dialog
                        dialog.dismiss()

                    } else {
                        view.textInputLayout_note_input_form.error = "Please provide note here"
                    }
                } else {
                    view.textInputLayout_title_input_form.error = "Please provide title here"
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val query = mDatabase.reference.child("NoteTask")
        val options = FirebaseRecyclerOptions.Builder<Data>()
            .setLifecycleOwner(this@DashboardActivity)
            .setQuery(query, Data::class.java)
            .build()
        // retrieve available note list at onStart()
        val adapter = object : FirebaseRecyclerAdapter<Data, ListViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
                val inflater = LayoutInflater.from(this@DashboardActivity)
                val view = inflater.inflate(com.example.sampleofficetask.R.layout.note_item, parent, false)
                return ListViewHolder(view)
            }

            override fun onBindViewHolder(holder: ListViewHolder, position: Int, data: Data) {
                holder.setDate(data.date)
                holder.setTitle(data.title)
                holder.setNote(data.note)
            }

        }
        recyclerView.adapter = adapter

    }

}
