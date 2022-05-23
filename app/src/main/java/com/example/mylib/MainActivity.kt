package com.example.mylib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mylib.data.Database
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val db = Database()
    }
}