package com.joinus.trivagoshowcase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repositories.MainRepository
import services.network.ApiResponse
import services.mappers.Business
import services.mappers.BusinessDetails
import services.network.YelpServices
import javax.inject.Inject

class MainViewModel : ViewModel() {

    private val _businesses = MutableLiveData<List<Business>>(emptyList())
    private val businesses: LiveData<List<Business>> = _businesses

    private val _details = MutableLiveData<BusinessDetails>()
    private val details: LiveData<BusinessDetails> = _details

    fun getBusinesses(term: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = MainRepository().getBusinesses(term)
            if (response is ApiResponse.Success) {
                _businesses.postValue(response.data!!)
                Log.d("firstLog", "${response.data}")
            }
            if (response is ApiResponse.Error) {
                Log.d("firstLog", "${response.exception}")
            }
        }
    }

    fun getBusinessDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = MainRepository().getBusinessDetails(id)
            if (response is ApiResponse.Success) {
                _details.postValue(response.data!!)
                Log.d("firstLog", "${response.data}")
            }
            if (response is ApiResponse.Error) {
                Log.d("firstLog", "${response.exception}")
            }
        }
    }
}