package com.example.sampleofficetask.ui.activity


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleofficetask.R
import com.example.sampleofficetask.adapter.ListViewHolder
import com.example.sampleofficetask.model.Data
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.input_form.view.*
import kotlinx.android.synthetic.main.update_form.view.*
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
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar_dashboard)

        // init
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        val mUser = mAuth.currentUser
        val uId = mUser!!.uid
        mReference = mDatabase.reference.child("NoteTask").child(uId)
        mReference.keepSynced(true)

        // recycler view
        recyclerView = findViewById(R.id.recycler_dashboard)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager

        // passed custom layout to alert dialog
        fab_dashboard.setOnClickListener {
            // alert dialog with custom layout
            val myDialog = AlertDialog.Builder(this@DashboardActivity)
            myDialog.setTitle("Note Task")
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.input_form, null, false)
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
        // firebase adapter
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        // query
        val query = mDatabase.reference.child("NoteTask").child(user)
        // options
        val options =
            FirebaseRecyclerOptions.Builder<Data>().setLifecycleOwner(this).setQuery(query, Data::class.java).build()
        // adapter
        val adapter = object : FirebaseRecyclerAdapter<Data, ListViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
                return ListViewHolder(view)
            }

            override fun onBindViewHolder(holder: ListViewHolder, position: Int, data: Data) {
                holder.title.text = data.title
                holder.note.text = data.note
                holder.date.text = data.date
                // event listener
                holder.itemView.setOnClickListener {
                    // get the key for the post
                    val key: String = getRef(position).key.toString()
                    val title = data.title
                    val note = data.note
                    updateNote(title, note, key)
                }
            }
        }
        adapter.notifyDataSetChanged()
        adapter.startListening()
        recyclerView.adapter = adapter
    }



    // inflate the main menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle the menu click events
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_log_out -> {
                mAuth.signOut()
                startActivity(Intent(this@DashboardActivity, MainActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // function to update the note
    private fun updateNote(title: String, note: String, key: String?){
        val myDialog = AlertDialog.Builder(this@DashboardActivity)
        myDialog.setTitle("Update the task")
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.update_form, null, false)
        myDialog.setView(view)
        val dialog = myDialog.create()

        val edtTitle = view.edt_title_update_form
        val edtNote = view.edt_note_update_form
        edtTitle.setText(title)
        // place the cursor at the end
        edtTitle.setSelection(title.length)
        edtNote.setText(note)
        // place the cursor at the end
        edtNote.setSelection(note.length)

        val date = DateFormat.getInstance().format(Date())

        view.btn_update_update_form.setOnClickListener {
            // when update button pressed
            val data = Data(key!!, title, note, date)
            mReference.child(key).setValue(data)
            dialog.dismiss()
        }

        view.btn_delete_update_form.setOnClickListener {
            // when delete button pressed
            mReference.child(key!!).removeValue()
            dialog.dismiss()
        }

        dialog.show()
    }

}
