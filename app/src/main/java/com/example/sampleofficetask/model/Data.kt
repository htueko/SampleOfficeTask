package com.example.sampleofficetask.model

data class Data(
    var id: String,
    var title: String,
    var note: String,
    var date: String
){
    // no argument constructor
    constructor() : this("", "", "", "")
}