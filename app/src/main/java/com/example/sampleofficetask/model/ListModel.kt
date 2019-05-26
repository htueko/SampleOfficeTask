package com.example.sampleofficetask.model

// model class for item list
class ListModel {
    var id: String? = null
    var title: String? = null
    var note: String? = null

    constructor()
    constructor(id: String, title: String, note: String) {
        this.id = id
        this.title = title
        this.note = note
    }
}