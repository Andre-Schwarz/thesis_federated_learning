package com.thesis.client.data

import flwr.android_client.FlowerServiceGrpc

interface GRPCRunnable {
//    @Throws(Exception::class)
//    fun run(
//        blockingStub: FlowerServiceBlockingStub?,
//        asyncStub: FlowerServiceStub?,
//        activity: MainActivity?
//    )

    fun run(
        flowerClient: FlowerClient,
        blockingStub: FlowerServiceGrpc.FlowerServiceBlockingStub,
        asyncStub: FlowerServiceGrpc.FlowerServiceStub,
    )
}