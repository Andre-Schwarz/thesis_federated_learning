package com.thesis.client

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class GlobalViewModel : ViewModel() {

    private val _notifications = MutableLiveData<String>().apply {
        value = ""
    }
    val notifications: LiveData<String> = _notifications


    fun addTextToLogging(log: String) {
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        val time = dateFormat.format(Date())

        val oldValue = _notifications.value
        _notifications.value = oldValue + log
    }

}