package com.example.soft1c.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.soft1c.SingleLiveEvent
import com.example.soft1c.model.AnyModel
import com.example.soft1c.repository.BaseRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BaseViewModel(application: Application) : AndroidViewModel(application) {

    private val exceptionScope = CoroutineExceptionHandler { coroutineContext, throwable ->
        toastMutableData.postValue("Error on ${coroutineContext}, text ${throwable.message}")
    }
    private val repository = BaseRepository()

    private val authMutableData = MutableLiveData<Boolean>()
    private val toastMutableData = MutableLiveData<String>()
    private val anyObjectMutableData = SingleLiveEvent<Pair<Int, List<AnyModel>>>()

    val authLiveData: LiveData<Boolean>
        get() = authMutableData
    val toastLiveData: LiveData<String>
        get() = toastMutableData
    val anyObjectLiveData: LiveData<Pair<Int,List<AnyModel>>>
        get() = anyObjectMutableData

    fun auth() {
        viewModelScope.launch(exceptionScope + Dispatchers.IO) {
            authMutableData.postValue(repository.getAccessToken())
        }
    }

    fun downloadType(type: Int) {
        viewModelScope.launch(exceptionScope + Dispatchers.IO) {
            anyObjectMutableData.postValue(repository.getAnyApi(type))
        }
    }

}