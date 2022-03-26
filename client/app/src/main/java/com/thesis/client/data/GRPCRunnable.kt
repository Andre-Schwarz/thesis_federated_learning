package com.thesis.client.data

import flwr.android_client.FlowerServiceGrpc

interface GRPCRunnable {
    fun run(
        flowerClient: FlowerClient,
        blockingStub: FlowerServiceGrpc.FlowerServiceBlockingStub,
        asyncStub: FlowerServiceGrpc.FlowerServiceStub,
    )
}