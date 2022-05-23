package com.example.mylib.data

class Database {
    companion object {
        private val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://mylib-609a4-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("entries")
    }
}