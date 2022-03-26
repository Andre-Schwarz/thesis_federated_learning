package com.thesis.client.ui.data_selection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataSelectionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is data selection Fragment"
    }
    val text: LiveData<String> = _text
}