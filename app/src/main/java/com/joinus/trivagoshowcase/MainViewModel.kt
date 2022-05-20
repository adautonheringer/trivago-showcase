package com.joinus.trivagoshowcase

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import repositories.MainRepository
import services.mappers.Business
import services.network.ApiResponse
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {


    private val _mainViewState = MutableStateFlow(MainViewState())
    val mainViewState: StateFlow<MainViewState> = _mainViewState.asStateFlow()

    private val _mapViewState = MutableStateFlow(MapViewState())
    val mapViewState: StateFlow<MapViewState> = _mapViewState.asStateFlow()

    private val _businessesViewState = MutableStateFlow(BusinessesViewState())
    val businessesViewState: StateFlow<BusinessesViewState> = _businessesViewState.asStateFlow()

    private val _searchViewState = MutableStateFlow(SearchViewState())
    val searchViewState: StateFlow<SearchViewState> = _searchViewState.asStateFlow()

    private val _detailsViewState = MutableStateFlow(DetailsViewState())
    val detailsViewState: StateFlow<DetailsViewState> = _detailsViewState.asStateFlow()

    init {
        getBusinesses()
    }

    fun getBusinesses(lat: Double = 51.233334, lng: Double = 6.783333) {
        viewModelScope.launch(Dispatchers.IO) {
            _mapViewState.update { it.copy(isLoading = true, isRefreshButtonVisible = false) }
            _businessesViewState.update { it.copy(isLoading = true) }
            _searchViewState.update { it.copy(isLoading = true) }
            val response = repository.getBusinesses(lat, lng)
            if (response is ApiResponse.Success) {
                _mapViewState.update {
                    it.copy(
                        isLoading = false,
                        businesses = response.data,
                        snapedViewId = null
                    )
                }
                _businessesViewState.update {
                    it.copy(
                        isLoading = false,
                        businesses = response.data,
                        mapViewPosition = null
                    )
                }
                _searchViewState.update { it.copy(isLoading = false) }
            }
            if (response is ApiResponse.Error) {
                _mapViewState.update { it.copy(isLoading = false, isError = true) }
                _businessesViewState.update { it.copy(isLoading = false, isError = true) }
                _searchViewState.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    fun getBusinessDetails(business: Business, sharedViews: List<View>) {
        viewModelScope.launch(Dispatchers.IO) {
            _mainViewState.update {
                it.copy(
                    isGoingToBusinessDetails = true,
                    sharedViews = sharedViews,
                    business = business
                )
            }
            _detailsViewState.update { it.copy(isLoading = true) }
            val response = repository.getBusinessDetails(business.id)
            if (response is ApiResponse.Success) {
                _mainViewState.update {
                    MainViewState()
                }
                _detailsViewState.update {
                    it.copy(
                        isLoading = false,
                        businessDetails = response.data
                    )
                }
            }
            if (response is ApiResponse.Error) {
                _mainViewState.update { it.copy(isGoingToBusinessDetails = false) }
                _detailsViewState.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    fun onMapOverlayViewClicked(position: Int?) {
        _businessesViewState.update { it.copy(mapViewPosition = position) }
    }

    fun onSnapView(id: String) {
        _mapViewState.update { it.copy(snapedViewId = id) }
    }

    fun refreshButton(isVisible: Boolean) {
        _mapViewState.update { it.copy(isRefreshButtonVisible = isVisible) }
    }

    fun onBackPressed() {
        _mainViewState.update { it.copy(isGoingBack = true, isGoingToBusinessDetails = false) }
    }


}