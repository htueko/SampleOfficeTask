package com.example.sampleofficetask.ui.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sampleofficetask.R
import com.example.sampleofficetask.model.Data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.input_form.view.*
import java.text.DateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    // fields
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // init
        mAuth = FirebaseAuth.getInstance()
        val mUser = mAuth.currentUser
        val uId = mUser!!.uid
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child("NoteTask").child(uId)



        // passed custom layout to alert dialog
        fab_dashboard.setOnClickListener {
            // alert dialog with custom layout
            val myDialog = AlertDialog.Builder(this@DashboardActivity)
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.input_form, null)
            myDialog.setView(view)
            val dialog = myDialog.create()
            dialog.show()

            // event listener for save button
            view.btn_save_input_form.setOnClickListener {
                // get the user input
                val title = view.edt_title_input_form.text.toString().trim()
                val note = view.edt_note_input_form.text.toString().trim()
                // validate the data
                if (!TextUtils.isEmpty(title)){
                    // title is not empty
                    if (!TextUtils.isEmpty(note)){
                        // title and note is not empty
                        // id and date
                        val id = mDatabaseReference.push().key
                        val date = DateFormat.getDateInstance().format(Date())
                        // model class
                        val data = Data(id!!, title, note, date)
                        // push to realtime database with unique key and date
                        mDatabaseReference.child(id).setValue(data)
                        // dismiss the dialog
                        dialog.dismiss()

                    }else{
                        view.textInputLayout_note_input_form.error = "Please provide note here"
                    }
                }else{
                    view.textInputLayout_title_input_form.error = "Please provide title here"
                }

            }

        }

    }
}
