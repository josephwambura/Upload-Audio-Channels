package com.jwambura.androiduac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    private val _first_text = MutableLiveData<String>().apply {
        value = "This is first Fragment"
    }
    val first_text: LiveData<String> = _first_text

    private val _second_text = MutableLiveData<String>().apply {
        value = "This is second Fragment"
    }
    val second_text: LiveData<String> = _second_text

    private val _file = MutableLiveData<String>().apply {
        value = "This is second Fragment"
    }
    val file: LiveData<String> = _file
}