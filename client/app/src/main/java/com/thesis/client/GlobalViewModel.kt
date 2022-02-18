package com.thesis.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.client.data.DATA_CLASSES
import com.thesis.client.data.DATA_SELECTION_TYPE
import com.thesis.client.data.DATA_SELECTION_TYPE.PARTITION

class GlobalViewModel : ViewModel() {

    private val _dataSelectionType = MutableLiveData<DATA_SELECTION_TYPE>().apply {
        value = PARTITION
    }
    val dataSelectionType: LiveData<DATA_SELECTION_TYPE> = _dataSelectionType

    fun changeDataSelection(type: DATA_SELECTION_TYPE) {
        _dataSelectionType.value = type
    }


    private val _selectedDataClasses = MutableLiveData<List<DATA_CLASSES>>().apply {
    }
    val selectedDataClasses: LiveData<List<DATA_CLASSES>> = _selectedDataClasses

    fun changeDataClassSelection(type: List<DATA_CLASSES>) {
        _selectedDataClasses.value = type
    }
}