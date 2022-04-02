package com.thesis.client.ui.training

import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.client.GlobalViewModel
import com.thesis.client.R
import com.thesis.client.data.*
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

class TrainingViewModel(
    private val context: Context,
    private val flowerClient: FlowerClient,
    private val setResultText: (String) -> Unit,
    private val globalViewModel: GlobalViewModel
) : ViewModel() {

    companion object {
        const val CROSS_DRAWABLE_ID = R.drawable.cross
        const val CHECKMARK_DRAWABLE_ID = R.drawable.checkmark
    }

    private var channel: ManagedChannel? = null

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

    // region Visibility

    private val _loadDataButtonEnabled = MutableLiveData<Boolean>().apply {
        value = true
    }
    val loadDataButtonEnabled: LiveData<Boolean> = _loadDataButtonEnabled

    private val _establishConnectionButtonEnabled = MutableLiveData<Boolean>().apply {
        value = false
    }
    val establishConnectionButtonEnabled: LiveData<Boolean> =
        _establishConnectionButtonEnabled

    private val _startTrainingButtonEnabled = MutableLiveData<Boolean>().apply {
        value = false
    }
    val startTrainingButtonEnabled: LiveData<Boolean> = _startTrainingButtonEnabled

    // endregion

    // region button commands

    fun handleLoadDataButton(clientID: String?) {
        if (!clientID.isNullOrEmpty()) {
            _loadDataButtonEnabled.value = false
            val clientIdValue = Integer.valueOf(clientID)
            clientIdValue.let {
                val handler = Handler()
                handler.postDelayed({
                    setResultText("Going to load the Training dataset with $clientIdValue")

                    var filename: String?
                    val value = globalViewModel.selectedModelArchitecture.value
                    if (value != null) {
                        flowerClient.selectModelArchitecture(value)
                    } else {
                        flowerClient.selectModelArchitecture(ModelArchitecture.CUSTOM)
                    }

                    when (globalViewModel.dataSelectionType.value) {
                        DATA_SELECTION_TYPE.PARTITION -> {
                            filename = "data/partition/partition_" + (clientIdValue - 1)
                            flowerClient.loadData(filename)
                        }
                        DATA_SELECTION_TYPE.CLASS_FULL -> {
                            val selectedDataClasses = globalViewModel.selectedDataClasses.value
                            if (selectedDataClasses != null) {
                                for (dataClass in selectedDataClasses) {

                                    filename = "data/single_classes_full/" + dataClass.className
                                    flowerClient.loadData(filename)
                                }
                            }
                        }
                        DATA_SELECTION_TYPE.CLASS_HALF -> {
                            val selectedDataClasses = globalViewModel.selectedDataClasses.value
                            if (selectedDataClasses != null) {
                                for (dataClass in selectedDataClasses) {

                                    filename = "data/single_classes_half/" + dataClass.className
                                    flowerClient.loadData(filename)
                                }
                            }
                        }
                        else -> {
                            setResultText("No Data for training selected")
                        }
                    }

                    _loadDataImageDrawable.value = CHECKMARK_DRAWABLE_ID
                    setResultText("Training dataset is loaded in memory.")
                    _loadDataButtonEnabled.value = false
                    _establishConnectionButtonEnabled.value = true
                }, 1000)
            }
        } else {
            Toast.makeText(
                context,
                "Bitte geben Sie eine Client ID zwischen 1 und 10 ein",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun handleEstablishConnectionButton(ip: String, port: Int) {
        if (TextUtils.isEmpty(ip) || !Patterns.IP_ADDRESS.matcher(ip).matches()) {
            Log.e("TAG", "handleEstablishConnectionButton: IP IS WRONG")
            Toast.makeText(
                context,
                "Please enter the correct IP and port of the FL server",
                Toast.LENGTH_LONG
            ).show()
        } else {
            channel =
                ManagedChannelBuilder.forAddress(ip, port).maxInboundMessageSize(10 * 1024 * 1024)
                    .usePlaintext().build()

            if (channel != null) {
                Log.e("TAG", "handleEstablishConnectionButton: CHANNEL CREATED")
                _establishConnectionImageDrawable.value = CHECKMARK_DRAWABLE_ID

                setResultText("Channel object created. Ready to train!")
                _establishConnectionButtonEnabled.value = false
                _startTrainingButtonEnabled.value = true
            } else {
                Log.e("TAG", "handleEstablishConnectionButton: CHANNEL NOT CREATED")
                setResultText("Channel could not be created")
            }
        }
    }

    fun handleStartTrainingButton() {
        channel?.let { channel ->
            val grpcTask =
                GrpcTask(flowerClient, setResultText, FlowerServiceRunnable(setResultText), channel)
            grpcTask.execute()

            _startTrainingImageDrawable.value = CHECKMARK_DRAWABLE_ID
            _startTrainingButtonEnabled.value = false
        }
    }

    // endregion


}