package com.example.sampleofficetask.util

import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar

// snack bar
fun showSnackBar(viewGroup: ViewGroup, message: String){
    Snackbar.make(viewGroup, message, Snackbar.LENGTH_SHORT)
        .setAction("CLOSE", View.OnClickListener {

        })
        .show()
}

