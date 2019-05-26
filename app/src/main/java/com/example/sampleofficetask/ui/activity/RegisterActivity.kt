package com.example.sampleofficetask.ui.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sampleofficetask.R
import com.example.sampleofficetask.util.Constants
import com.example.sampleofficetask.util.showSnackBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDialog: ProgressDialog
    private lateinit var parentLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val firstTime = getSharedPreferences(Constants.SHARE_PREF, Context.MODE_PRIVATE)
        if (firstTime.getBoolean(Constants.FIRST_TIME, true)) {
            // for the first time used ( here user don't put any value to edit text)
            Log.d(Constants.ACTIVITY_REGISTER, "First time or user don't put any value yet!")
            showSnackBar(parentLayout, "Have fun creating an account")
            firstTime.edit().putBoolean(Constants.FIRST_TIME, false).apply()
        }

        // init
        parentLayout = findViewById(R.id.activity_register)
        mAuth = FirebaseAuth.getInstance()
        mDialog = ProgressDialog(this)

        // send to main activity (to login)
        txt_sign_in_register.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        }

        btn_register_register.setOnClickListener {
            // get the user input
            val email = edt_email_register.text.toString().trim()
            val password = edt_password_register.text.toString().trim()
            // validate the user
            Toast.makeText(this, "email: $email and password: $password", Toast.LENGTH_LONG).show()
            if (!TextUtils.isEmpty(email)) {
                // email is valid
                if (password.isNotEmpty() && password.isNotBlank() && password.length >= 8) {
                    // password is valid
                    // create new user
                    createUser(email, password)
                } else {
                    textInputLayout_password_register.error = "Please provide valid password"
                }
            } else {
                textInputLayout_email_register.error = "Please provide valid email address"
            }
        }

    }

    private fun createUser(email: String, password: String) {
        // login the user
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // sign in successfully
                    Log.d(Constants.ACTIVITY_REGISTER, "Log in user with $email and $password")
                    mDialog.dismiss()
                    showSnackBar(parentLayout, "Login in successfully")
                    startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
                } else {
                    // sign in error
                    Log.d(Constants.ACTIVITY_REGISTER, "Error while login in, ${it.exception?.localizedMessage}")
                    mDialog.dismiss()
                    showSnackBar(parentLayout, "${it.exception?.localizedMessage}")
                }
            }

    }

}








