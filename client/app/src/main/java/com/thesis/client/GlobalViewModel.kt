package com.thesis.client

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.client.data.DATA_SELECTION_TYPE
import com.thesis.client.data.DATA_SELECTION_TYPE.PARTITION
import java.util.*

class GlobalViewModel : ViewModel() {

    private val _notifications = MutableLiveData<String>().apply {
        value = ""
    }
    val notifications: LiveData<String> = _notifications

    private val _dataSelectionType = MutableLiveData<DATA_SELECTION_TYPE>().apply {
        value = PARTITION
    }
    val dataSelectionType: LiveData<DATA_SELECTION_TYPE> = _dataSelectionType


    fun addTextToLogging(log: String) {
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        val time = dateFormat.format(Date())

        val oldValue = _notifications.value
        _notifications.value = oldValue + log
    }

    fun changeDataSelection(type: DATA_SELECTION_TYPE) {
        _dataSelectionType.value = type
    }
}