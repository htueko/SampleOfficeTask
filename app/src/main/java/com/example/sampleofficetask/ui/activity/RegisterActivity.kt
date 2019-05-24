package com.example.sampleofficetask.ui.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.TextUtilsCompat
import com.example.sampleofficetask.R
import com.example.sampleofficetask.util.Constants
import com.example.sampleofficetask.util.showSnackBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDialog: ProgressDialog
    private lateinit var parentLayout: ConstraintLayout
    private var email = ""
    private var password = ""

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

        // get the user input
        email = edt_email_register.text.toString().trim()
        password = edt_password_register.text.toString().trim()

        // validate the user input
        if (TextUtils.isEmpty(email)) textInputLayout_email_register.error =
            "Please enter valid email address" else return
        when {
            TextUtils.isEmpty(password) -> {
                textInputLayout_password_register.error = "Please enter valid password"
                return
            }
            password.length <= 7 -> textInputLayout_password_register.hint = "password should be equal or longer than 8 character"
            else -> return
        }

        // event handler for register button
        btn_register_register.setOnClickListener {
            // show the progress
            mDialog.setMessage("Processing...")
            mDialog.show()
            // create a new user
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // task successful
                        showSnackBar(parentLayout, "Account creation successfully")
                        startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
                    } else {
                        // task isn't successful
                        mDialog.dismiss()
                        showSnackBar(parentLayout, "Account creation is not finished :(")
                    }
                }
        }

    }

}
