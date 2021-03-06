package com.thesis.client.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.ConditionVariable
import android.util.Log
import android.util.Pair
import androidx.lifecycle.MutableLiveData
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.util.concurrent.ExecutionException

class FlowerClient(private val context: Context) {
    companion object {
        private const val LOWER_BYTE_MASK = 0xFF
        private const val TAG = "Flower"

    }

    private var tlModel: TransferLearningModelWrapper = TransferLearningModelWrapper(
        context,
        "model"
    )
    private var tlModelMobiNet: TransferLearningModelWrapper = TransferLearningModelWrapper(
        context,
        "model_mobinet"
    )
    private var selectedModelWrapper: TransferLearningModelWrapper = tlModel

    private val lastLoss = MutableLiveData<Float>()
    private val isTraining = ConditionVariable()
    private var localEpochs = 1

    fun fit(weights: Array<ByteBuffer?>?, epochs: Int): Pair<Array<ByteBuffer>, Int> {
        localEpochs = epochs
        tlModel.updateParameters(weights)
        isTraining.close()
        tlModel.train(localEpochs)
        tlModel.enableTraining { epoch, loss -> setLastLoss(epoch, loss) }
        Log.e(TAG, "Training enabled. Local Epochs = $localEpochs")
        isTraining.block()
        return Pair.create(getWeights(), tlModel.trainingSize)
    }

    fun evaluate(weights: Array<ByteBuffer?>?): Pair<Pair<Float, Float>, Int> {
        tlModel.updateParameters(weights)
        tlModel.disableTraining()
        return Pair.create(tlModel.calculateTestStatistics(), tlModel.testingSize)
    }

    private fun setLastLoss(epoch: Int, newLoss: Float) {
        if (epoch == localEpochs - 1) {
            Log.e(TAG, "Training finished after epoch = $epoch")
            lastLoss.postValue(newLoss)
            tlModel.disableTraining()
            isTraining.open()
        }
    }

    fun selectModelArchitecture(modelArchitecture: ModelArchitecture) {
        if (modelArchitecture == ModelArchitecture.CUSTOM) {
            selectedModelWrapper = tlModel
        } else if (modelArchitecture == ModelArchitecture.MOBILENET) {
            selectedModelWrapper = tlModelMobiNet
        }

        Log.e(TAG, "Selected ModelArchitecture $modelArchitecture")
    }

    fun loadData(filename: String) {
        try {
            Log.e(TAG, "Going to load $filename")
            var reader = BufferedReader(
                InputStreamReader(
                    context.assets.open(filename + "_train.txt")
                )
            )
            var line = ""
            var i = 0
            while (
                reader.readLine().also {
                    if (it != null) {
                        line = it
                    }
                } != null
            ) {
                i++
                Log.e(TAG, i.toString() + "th training image loaded")
                addSample("data/$line", true)
            }
            reader.close()
            i = 0
            reader =
                BufferedReader(InputStreamReader(context.assets.open(filename + "_test.txt")))
            while (reader.readLine().also {
                    if (it != null) {
                        line = it
                    }
                } != null) {
                i++
                Log.e(TAG, i.toString() + "th test image loaded")
                addSample("data/$line", false)
            }
            reader.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun addSample(photoPath: String, isTraining: Boolean) {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeStream(context.assets.open(photoPath), null, options)
        val sampleClass = getClass(photoPath)

        val rgbImage = prepareImage(bitmap)
        try {
            tlModel.addSample(rgbImage, sampleClass, isTraining).get()
        } catch (e: ExecutionException) {
            throw RuntimeException("Failed to add sample to model", e.cause)
        } catch (e: InterruptedException) {
            // no-op
        }
    }

    fun getWeights(): Array<ByteBuffer> {
        return tlModel.parameters
    }

    private fun getClass(path: String): String {
        return path.split("/").toTypedArray()[2]
    }

    private fun prepareImage(bitmap: Bitmap?): FloatArray {
        val modelImageSize: Int = TransferLearningModelWrapper.IMAGE_SIZE
        val normalizedRgb = FloatArray(modelImageSize * modelImageSize * 3)
        var nextIdx = 0
        for (y in 0 until modelImageSize) {
            for (x in 0 until modelImageSize) {
                val rgb = bitmap!!.getPixel(x, y)
                val r = (rgb shr 16 and LOWER_BYTE_MASK) * (1 / 255.0f)
                val g = (rgb shr 8 and LOWER_BYTE_MASK) * (1 / 255.0f)
                val b = (rgb and LOWER_BYTE_MASK) * (1 / 255.0f)
                normalizedRgb[nextIdx++] = r
                normalizedRgb[nextIdx++] = g
                normalizedRgb[nextIdx++] = b
            }
        }
        return normalizedRgb
    }
}