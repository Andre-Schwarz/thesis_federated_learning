package com.thesis.client.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.client.R

class HomeViewModel : ViewModel() {

    companion object {
        const val CROSS_DRAWABLE_ID = R.drawable.cross
        const val CHECKMARK_DRAWABLE_ID = R.drawable.checkmark
    }

    // region Button Texts

    private val _loadDataButtonText = MutableLiveData<String>().apply {
        value = "Datensatz laden"
    }
    val loadDataButtonText: LiveData<String> = _loadDataButtonText

    private val _establishConnectionButtonText = MutableLiveData<String>().apply {
        value = "Serververbindung aufbauen"
    }
    val establishConnectionButtonText: LiveData<String> = _establishConnectionButtonText

    private val _startTrainingButtonText = MutableLiveData<String>().apply {
        value = "Training starten"
    }
    val startTrainingButtonText: LiveData<String> = _startTrainingButtonText

    // endregion

    // region images

    private val _loadDataImageDrawable = MutableLiveData<Int>().apply {
        value = CROSS_DRAWABLE_ID
    }
    val loadDataImageDrawable: LiveData<Int> = _loadDataImageDrawable

    private val _establishConnectionImageDrawable = MutableLiveData<Int>().apply {
        value = CROSS_DRAWABLE_ID
    }
    val establishConnectionImageDrawable: LiveData<Int> = _establishConnectionImageDrawable

    private val _startTrainingImageDrawable = MutableLiveData<Int>().apply {
        value = CROSS_DRAWABLE_ID
    }
    val startTrainingImageDrawable: LiveData<Int> = _startTrainingImageDrawable

    // endregion

    // region button commands

    fun handleLoadDataButton() {
        _loadDataImageDrawable.value = CHECKMARK_DRAWABLE_ID
    }

    fun handleEstablishConnectionButton() {
        _establishConnectionImageDrawable.value = CHECKMARK_DRAWABLE_ID
    }

    fun handleStartTrainingButton() {
        _startTrainingImageDrawable.value = CHECKMARK_DRAWABLE_ID
    }

    // endregion
}