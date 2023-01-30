package com.example.soft1c.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.soft1c.SingleLiveEvent
import com.example.soft1c.model.Acceptance
import com.example.soft1c.model.Client
import com.example.soft1c.repository.AcceptanceRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AcceptanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AcceptanceRepository()

    private val exceptionScope = CoroutineExceptionHandler { coroutineContext, throwable ->
        toastMutableData.postValue("Error on $coroutineContext , error message ${throwable.message}")
    }

    private val toastMutableData = SingleLiveEvent<String>()
    private val acceptanceListMutableData = MutableLiveData<List<Acceptance>>()
    private val acceptanceMutableData = SingleLiveEvent<Acceptance>()
    private val clientMutableData = SingleLiveEvent<Pair<Client, Boolean>>()
    private val createUpdateMutableData = SingleLiveEvent<Pair<Acceptance, Boolean>>()

    val toastLiveData: LiveData<String>
        get() = toastMutableData

    val acceptanceListLiveData: LiveData<List<Acceptance>>
        get() = acceptanceListMutableData

    val acceptanceLiveData: LiveData<Acceptance>
        get() = acceptanceMutableData

    val clientLiveData: LiveData<Pair<Client, Boolean>>
        get() = clientMutableData

    val createUpdateLiveData: LiveData<Pair<Acceptance, Boolean>>
        get() = createUpdateMutableData

    fun getAcceptanceList() {
        viewModelScope.launch((exceptionScope + Dispatchers.IO)) {
            acceptanceListMutableData.postValue(repository.getAcceptanceListApi())
        }
    }

    fun getAcceptance(number: String) {
        viewModelScope.launch((exceptionScope + Dispatchers.IO)) {
            acceptanceMutableData.postValue(repository.getAcceptanceApi(number))
        }
    }

    fun getClient(clientCode: String) {
        viewModelScope.launch((exceptionScope + Dispatchers.IO)) {
            clientMutableData.postValue(repository.getClientApi(clientCode))
        }
    }

    fun createUpdateAcceptance(acceptance: Acceptance) {
        viewModelScope.launch((exceptionScope + Dispatchers.IO)) {
            createUpdateMutableData.postValue(repository.createUpdateAccApi(acceptance))
        }
    }
}