package com.example.sampleofficetask.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note_item.view.*

class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title = itemView.txt_title_note_item
    val note = itemView.txt_note_note_item
    val date = itemView.txt_date_note_item
}