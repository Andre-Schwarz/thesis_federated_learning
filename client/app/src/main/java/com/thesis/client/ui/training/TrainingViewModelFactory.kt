package com.thesis.client.ui.training

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thesis.client.data.FlowerClient


class TrainingViewModelFactory(application: Context, param: FlowerClient) :
    ViewModelProvider.Factory {
    private val mApplication: Context = application
    private val mParam: FlowerClient = param
//    override fun <T : ViewModel?> create(modelClass: Class<T>?): T {
//        return TrainingViewModel(mApplication, mParam) as T
//    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrainingViewModel(mApplication, mParam) as T
    }


}