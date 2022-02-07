package com.thesis.client.ui.training

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thesis.client.data.FlowerClient


class TrainingViewModelFactory(
    application: Context,
    flowerClient: FlowerClient,
    setResultText: (String) -> Unit
) :
    ViewModelProvider.Factory {
    private val mApplication: Context = application
    private val mFlowerClient: FlowerClient = flowerClient
    private val mSetResultText: (String) -> Unit = setResultText

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrainingViewModel(mApplication, mFlowerClient, mSetResultText) as T
    }

}