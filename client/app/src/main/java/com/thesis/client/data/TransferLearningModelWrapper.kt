package com.thesis.client.data

import android.content.Context
import android.os.ConditionVariable
import android.util.Pair
import org.tensorflow.lite.examples.transfer.api.AssetModelLoader
import org.tensorflow.lite.examples.transfer.api.TransferLearningModel
import org.tensorflow.lite.examples.transfer.api.TransferLearningModel.LossConsumer
import java.io.Closeable
import java.nio.ByteBuffer
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future

/**
 * App-layer wrapper for [TransferLearningModel].
 *
 *
 * This wrapper allows to run training continuously, using start/stop API, in contrast to
 * run-once API of [TransferLearningModel].
 */
class TransferLearningModelWrapper internal constructor(context: Context, directoryName: String) :
    Closeable {

    companion object {
        const val IMAGE_SIZE = 32
    }

    private val model: TransferLearningModel = TransferLearningModel(
        AssetModelLoader(context, directoryName),
        listOf(
            "airplane", "automobile", "bird", "cat", "deer",
            "dog", "frog", "horse", "ship", "truck"
        )
    )

    private val shouldTrain = ConditionVariable()

    @Volatile
    private var lossConsumer: LossConsumer? = null

    fun train(epochs: Int) {
        Thread {
            shouldTrain.block()
            try {
                model.train(epochs, lossConsumer).get()
            } catch (e: ExecutionException) {
                throw RuntimeException("Exception occurred during model training", e.cause)
            } catch (e: InterruptedException) {
                // no-op
            }
        }.start()
    }

    fun addSample(image: FloatArray?, className: String?, isTraining: Boolean?): Future<Void> {
        return model.addSample(image, className, isTraining)
    }

    fun calculateTestStatistics(): Pair<Float, Float> {
        return model.testStatistics
    }

    fun enableTraining(lossConsumer: LossConsumer?) {
        this.lossConsumer = lossConsumer
        shouldTrain.open()
    }

    val trainingSize: Int
        get() = model.size_Training
    val testingSize: Int
        get() = model.size_Testing
    val parameters: Array<ByteBuffer>
        get() = model.parameters

    fun updateParameters(newParams: Array<ByteBuffer?>?) {
        model.updateParameters(newParams)
    }

    fun disableTraining() {
        shouldTrain.close()
    }

    override fun close() {
        model.close()
    }
}