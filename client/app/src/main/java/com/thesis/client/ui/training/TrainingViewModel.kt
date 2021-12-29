package com.thesis.client.ui.training

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.client.R

class TrainingViewModel : ViewModel() {

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

    fun handleLoadDataButton(clientID: Int?) {
        _loadDataImageDrawable.value = CHECKMARK_DRAWABLE_ID

        clientID?.let {
            if(clientID > 10 || clientID < 1){

            }

        }



//        if (TextUtils.isEmpty(clientID)) {
//            Toast.makeText(
//                this,
//                "Please enter a client partition ID between 1 and 10 (inclusive)",
//                Toast.LENGTH_LONG
//            ).show()
//        } else if (device_id.getText().toString().toInt() > 10 || device_id.getText().toString()
//                .toInt() < 1
//        ) {
//            Toast.makeText(
//                this,
//                "Please enter a client partition ID between 1 and 10 (inclusive)",
//                Toast.LENGTH_LONG
//            ).show()
//        } else {
//            MainActivity.hideKeyboard(this)
//            setResultText("Loading the local training dataset in memory. It will take several seconds.")
//            loadDataButton.setEnabled(false)
//            val handler = Handler()
//            handler.postDelayed({
//                fc.loadData(device_id.getText().toString().toInt())
//                setResultText("Training dataset is loaded in memory.")
//                connectButton.setEnabled(true)
//            }, 1000)
//        }

    }

    fun handleEstablishConnectionButton() {
        _establishConnectionImageDrawable.value = CHECKMARK_DRAWABLE_ID
    }

    fun handleStartTrainingButton() {
        _startTrainingImageDrawable.value = CHECKMARK_DRAWABLE_ID
    }

    // endregion
}