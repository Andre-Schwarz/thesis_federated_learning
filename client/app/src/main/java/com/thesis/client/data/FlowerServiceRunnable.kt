package com.thesis.client.data

import android.util.Log
import android.util.Pair
import com.google.protobuf.ByteString
import flwr.android_client.*
import flwr.android_client.ClientMessage.*
import io.grpc.stub.StreamObserver
import java.nio.ByteBuffer
import java.util.concurrent.CountDownLatch

class FlowerServiceRunnable(private val setResultText: (String) -> Unit) : GRPCRunnable {
    private var failed: Throwable? = null
    private var requestObserver: StreamObserver<ClientMessage?>? = null

    @Throws(Exception::class)
    override fun run(
        flowerClient: FlowerClient,
        blockingStub: FlowerServiceGrpc.FlowerServiceBlockingStub,
        asyncStub: FlowerServiceGrpc.FlowerServiceStub
    ) {
        join(
            flowerClient,
            asyncStub
        )
    }

    @Throws(InterruptedException::class, RuntimeException::class)
    private fun join(
        flowerClient: FlowerClient,
        asyncStub: FlowerServiceGrpc.FlowerServiceStub
    ) {
        val finishLatch = CountDownLatch(1)
        requestObserver = asyncStub.join(
            object : StreamObserver<ServerMessage> {
                override fun onNext(msg: ServerMessage) {
                    handleMessage(
                        flowerClient,
                        msg
                    )
                }

                override fun onError(t: Throwable) {
                    failed = t
                    finishLatch.countDown()
                    Log.e("MainActivity.Error", t.message!!)

//                    setResultText("Error Service Runnable " + t.message)
                }

                override fun onCompleted() {
                    finishLatch.countDown()
                    Log.e("MainActivity.Completed", "Done")
//                    setResultText("Service Runnable completed")
                }
            })
    }

    private fun handleMessage(
        flowerClient: FlowerClient,
        message: ServerMessage
    ) {
        try {
            val weights: Array<ByteBuffer>
            var c: ClientMessage? = null
            when {
                message.hasGetParameters() -> {
                    Log.e("FlowerServiceRunnable", "Handling GetParameters")
                    setResultText("Handling GetParameters message from the server.")
                    weights = flowerClient.getWeights()
                    c = weightsAsProto(weights)
                }
                message.hasFitIns() -> {
                    Log.e("FlowerServiceRunnable", "Handling FitIns")
                    setResultText("Handling Fit request from the server.")
                    val layers: List<ByteString> = message.fitIns.parameters.tensorsList
                    val epochConfig: Scalar = message.fitIns.configMap
                        .getOrDefault("local_epochs", Scalar.newBuilder().setSint64(1).build())
                    val localEpochs = epochConfig.sint64.toInt()

                    // Our model has 10 layers
                    val newWeights = arrayOfNulls<ByteBuffer>(10)
                    for (i in 0..9) {
                        newWeights[i] = ByteBuffer.wrap(layers[i].toByteArray())
                    }
                    val outputs: Pair<Array<ByteBuffer>, Int> =
                        flowerClient.fit(newWeights, localEpochs)
                    c = fitResAsProto(outputs.first, outputs.second)
                }
                message.hasEvaluateIns() -> {
                    Log.e("FlowerServiceRunnable", "Handling EvaluateIns")
                    setResultText("Handling Evaluate request from the server")
                    val layers: List<ByteString> =
                        message.evaluateIns.parameters.tensorsList

                    // Our model has 10 layers
                    val newWeights = arrayOfNulls<ByteBuffer>(10)
                    for (i in 0..9) {
                        newWeights[i] = ByteBuffer.wrap(layers[i].toByteArray())
                    }
                    val inference: Pair<Pair<Float, Float>, Int> = flowerClient.evaluate(newWeights)
                    val loss = inference.first.first
                    val accuracy = inference.first.second
                    setResultText("Test Accuracy after this round = $accuracy")
                    val testSize = inference.second
                    c = evaluateResAsProto(loss, testSize)
                }
                else -> {
                    setResultText("Some Problem within the message handling occurred ")
                }
            }
            requestObserver!!.onNext(c)
            setResultText("Response sent to the server")
            c = null
        } catch (e: Exception) {
            Log.e("FlowerServiceRunnable", e.message!!)
        }
    }

    private fun weightsAsProto(weights: Array<ByteBuffer>): ClientMessage? {
        val layers: MutableList<ByteString> = ArrayList()
        for (i in weights.indices) {
            layers.add(ByteString.copyFrom(weights[i]))
        }
        val p = Parameters.newBuilder().addAllTensors(layers).setTensorType("ND").build()
        val res = ParametersRes.newBuilder().setParameters(p).build()
        return newBuilder().setParametersRes(res).build()
    }

    private fun fitResAsProto(weights: Array<ByteBuffer>, training_size: Int): ClientMessage? {
        val layers: MutableList<ByteString> = ArrayList()
        for (i in weights.indices) {
            layers.add(ByteString.copyFrom(weights[i]))
        }
        val p = Parameters.newBuilder().addAllTensors(layers).setTensorType("ND").build()
        val res =
            FitRes.newBuilder().setParameters(p).setNumExamples(training_size.toLong()).build()
        return newBuilder().setFitRes(res).build()
    }

    private fun evaluateResAsProto(accuracy: Float, testing_size: Int): ClientMessage? {
        val res =
            EvaluateRes.newBuilder().setLoss(accuracy).setNumExamples(testing_size.toLong()).build()
        return newBuilder().setEvaluateRes(res).build()
    }
}