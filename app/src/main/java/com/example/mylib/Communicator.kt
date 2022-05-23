package com.example.mylib

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class Communicator: ViewModel() {
    val UserUID = MutableLiveData<String>()
    val UserEmail = MutableLiveData<String>()

    fun setUserUID(value: String){
        UserUID.value = value
    }

    fun setUserEmail(value: String){
        UserEmail.value = value
    }
}