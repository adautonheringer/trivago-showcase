package com.joinus.trivagoshowcase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import repositories.MainRepository
import services.network.ApiResponse
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val _viewState =
        MutableStateFlow(MainViewState())
    val viewState: StateFlow<MainViewState> = _viewState.asStateFlow()

    init {
        getBusinesses()
    }

    fun getBusinesses(lat: Double = 51.233334, lng: Double = 6.783333) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { it.copy(isLoading = true, refreshButtonIsVisible = false) }
            val response = repository.getBusinesses(lat, lng)
            if (response is ApiResponse.Success) {
                _viewState.update {
                    it.copy(isLoading = false, businesses = response.data)
                }
            }
            if (response is ApiResponse.Error) {
                _viewState.update {
                    it.copy(isLoading = false, isError = true)
                }
            }
        }
    }

    fun onMapOverlayViewClicked(position: Int?) {
        _viewState.update {
            it.copy(onMapViewClick = position)
        }
    }

    fun onSnapView(id: String) {
        _viewState.update {
            it.copy(snapedViewId = id)
        }
    }

    fun refreshButton(isVisible: Boolean) {
        _viewState.update {
            it.copy(refreshButtonIsVisible = isVisible)
        }
    }

    fun getBusinessDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.emit(_viewState.value.copy(isLoading = true))
            val response = repository.getBusinessDetails(id)

//            if (response is ApiResponse.Success) {
//
//            }
//            if (response is ApiResponse.Error) {
//                _viewState.emit(_viewState.value.copy(isLoading = false))
//            }
        }
    }

}