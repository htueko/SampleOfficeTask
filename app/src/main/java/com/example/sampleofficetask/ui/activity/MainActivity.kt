package com.example.sampleofficetask.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sampleofficetask.R
import com.example.sampleofficetask.util.Constants
import com.example.sampleofficetask.util.showSnackBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // fields
    private lateinit var mAuth: FirebaseAuth
    private lateinit var parentLayout: ConstraintLayout
    private lateinit var mDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init
        mAuth = FirebaseAuth.getInstance()
        parentLayout = findViewById(R.id.activity_main)
        mDialog = ProgressDialog(this)

        // check the user is already signed up or not
        // if so sent to DashBoard Activity
        if (mAuth.currentUser != null) {
            showSnackBar(parentLayout, "Welcome back ${mAuth?.currentUser?.displayName}")
            startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
        }

        // send to register activity (to register)
        txt_sign_up_main.setOnClickListener {
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }

        btn_login_main.setOnClickListener {
            // get the user input
            val email = edt_email_main.text.toString().trim()
            val password = edt_password_main.text.toString().trim()
            // validate the user input
            if (!TextUtils.isEmpty(email)) {
                // email is valid
                if (password.isNotEmpty() && password.isNotBlank() && password.length >= 8) {
                    // password is valid
                    // sign in user
                    userSignIn(email, password)
                } else {
                    textInputLayout_password_main.error = "Please provide valid password"
                }
            } else {
                textInputLayout_email_main.error = "Please provide valid email address"
            }

        }

    }

    private fun userSignIn(email: String, password: String) {
        // login the user
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // sign in successfully
                    Log.d(Constants.ACTIVITY_MAIN, "Log in user with $email and $password")
                    mDialog.dismiss()
                    showSnackBar(parentLayout, "Login in successfully")
                    startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                } else {
                    // sign in error
                    Log.d(Constants.ACTIVITY_MAIN, "Error while login in, ${it.exception?.localizedMessage}")
                    mDialog.dismiss()
                    showSnackBar(parentLayout, "${it.exception?.localizedMessage}")
                }
            }

    }

}
