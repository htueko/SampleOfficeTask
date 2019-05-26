package com.example.sampleofficetask.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleofficetask.R

class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setTitle(title: String) {
        val mTitle = itemView.findViewById<TextView>(R.id.txt_title_note_item)
        mTitle.text = title
    }

    fun setNote(note: String) {
        val mNote = itemView.findViewById<TextView>(R.id.txt_note_note_item)
        mNote.text = note
    }

    fun setDate(date: String) {
        val mDate = itemView.findViewById<TextView>(R.id.txt_date_note_item)
        mDate.text = date
    }

}