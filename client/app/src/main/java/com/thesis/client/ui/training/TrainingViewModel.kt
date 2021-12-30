package com.thesis.client.ui.training

import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.client.R
import com.thesis.client.data.FlowerClient
import com.thesis.client.data.FlowerServiceRunnable
import com.thesis.client.data.GrpcTask
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

class TrainingViewModel(context: Context, private val flowerClient: FlowerClient) : ViewModel() {

    companion object {
        const val CROSS_DRAWABLE_ID = R.drawable.cross
        const val CHECKMARK_DRAWABLE_ID = R.drawable.checkmark
    }

//    var fc: FlowerClient

    init {
//        fc = FlowerClient()
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

    // region button commands

    fun handleLoadDataButton(clientID: Int?) {

        clientID?.let {
            if (clientID > 10 || clientID < 1) {

            }
            val handler = Handler()
            handler.postDelayed({
                flowerClient.loadData(clientID)
                _loadDataImageDrawable.value = CHECKMARK_DRAWABLE_ID
//                setResultText("Training dataset is loaded in memory.")
//                connectButton.setEnabled(true)
            }, 1000)
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

    fun handleEstablishConnectionButton(ip: String, port: Int) {


//        val host: String = ip.getText().toString()
//        val portStr: String =  port.getText().toString()
        if (TextUtils.isEmpty(ip) || !Patterns.IP_ADDRESS.matcher(ip).matches()
        ) {
            Log.e("TAG", "handleEstablishConnectionButton: IP IS WRONG")
//            Toast.makeText(
//                this,
//                "Please enter the correct IP and port of the FL server",
//                Toast.LENGTH_LONG
//            ).show()
        } else {
            channel =
                ManagedChannelBuilder.forAddress(ip, port).maxInboundMessageSize(10 * 1024 * 1024)
                    .usePlaintext().build()

            if (channel != null) {
                Log.e("TAG", "handleEstablishConnectionButton: CHANNEL CREATED")
                _establishConnectionImageDrawable.value = CHECKMARK_DRAWABLE_ID
            } else {
                Log.e("TAG", "handleEstablishConnectionButton: CHANNEL NOT CREATED")
            }

//            MainActivity.hideKeyboard(this)
//            trainButton.setEnabled(true)
//            connectButton.setEnabled(false)
//            setResultText("Channel object created. Ready to train!")
        }

    }

    fun handleStartTrainingButton() {

        channel?.let { channel ->
            val grpcTask = GrpcTask(flowerClient, FlowerServiceRunnable(), channel)
            grpcTask.execute()

            _startTrainingImageDrawable.value = CHECKMARK_DRAWABLE_ID
        }
    }

    // endregion


}