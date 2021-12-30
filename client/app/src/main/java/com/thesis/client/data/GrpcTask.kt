package com.thesis.client.data

import android.os.AsyncTask
import flwr.android_client.FlowerServiceGrpc
import io.grpc.ManagedChannel
import java.io.PrintWriter
import java.io.StringWriter

class GrpcTask(
    private val flowerClient: FlowerClient,
    private val grpcRunnable: GRPCRunnable,
    private val channel: ManagedChannel,
//    activity: MainActivity
) :
    AsyncTask<Void?, Void?, String>() {
    //    private val activityReference: MainActivity

    init {
        //        activityReference = activity
    }

    override fun doInBackground(vararg params: Void?): String {
        return try {
            grpcRunnable.run(
                flowerClient,
                FlowerServiceGrpc.newBlockingStub(channel), FlowerServiceGrpc.newStub(channel),
                //   activityReference
            )
            "Connection to the FL server successful \n"
        } catch (e: Exception) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            pw.flush()
            "Failed to connect to the FL server \n$sw"
        }
    }

    override fun onPostExecute(result: String) {
//        val activity: MainActivity = activityReference ?: return
//        activity.setResultText(result)
//        activity.trainButton.setEnabled(false)
    }
}