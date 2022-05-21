package com.joinus.trivagoshowcase

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joinus.trivagoshowcase.di.IoDispatcher
import com.joinus.trivagoshowcase.features.businesses.BusinessesViewState
import com.joinus.trivagoshowcase.features.details.DetailsViewState
import com.joinus.trivagoshowcase.features.map.MapViewState
import com.joinus.trivagoshowcase.features.search.SearchViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import repositories.MainRepository
import services.mappers.Business
import services.network.ApiResponse
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

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

    fun getBusinesses(lat: Double = 51.233334, lng: Double = 6.783333) {
        viewModelScope.launch(dispatcher) {
            _mapViewState.update { it.copy(isLoading = true, onRefreshClicked = false) }
            _businessesViewState.update { it.copy(isLoading = true) }
            _searchViewState.update { it.copy(isLoading = true, isRefreshButtonVisible = false) }
            _mainViewState.update { it.copy(isError = false) }
            when (val response = repository.getBusinesses(lat, lng)) {
                is ApiResponse.Success -> {
                    _mapViewState.update { it.copy(isLoading = false, businesses = response.data, snapedViewId = null) }
                    _businessesViewState.update { it.copy(isLoading = false, businesses = response.data, mapViewPosition = null) }
                    _searchViewState.update { it.copy(isLoading = false) }
                }
                is ApiResponse.Error -> {
                    _mapViewState.update { it.copy(isLoading = false) }
                    _businessesViewState.update { it.copy(isLoading = false) }
                    _searchViewState.update { it.copy(isLoading = false) }
                    _mainViewState.update { it.copy(isError = true) }
                }
            }
        }
    }

    fun getBusinessDetails(business: Business, sharedViews: List<View>) {
        viewModelScope.launch(dispatcher) {
            _mainViewState.update { it.copy(isGoingToBusinessDetails = true, sharedViews = sharedViews, business = business)}
            _detailsViewState.update { it.copy(isLoading = true) }
            when (val response = repository.getBusinessDetails(business.id)) {
                is ApiResponse.Success -> {
                    _mainViewState.update { MainViewState() }
                    _detailsViewState.update { it.copy(isLoading = false,businessDetails = response.data) }
                }
                is ApiResponse.Error ->  {
                    _mainViewState.update { it.copy(isGoingToBusinessDetails = false, isError = true) }
                    _detailsViewState.update { it.copy(isLoading = false) }
                }
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
        _searchViewState.update { it.copy(isRefreshButtonVisible = isVisible) }
    }

    fun onRefreshButtonClicked(isVisible: Boolean) {
        _mapViewState.update { it.copy(onRefreshClicked = isVisible) }
    }

    fun onBackPressed() {
        _mainViewState.update { it.copy(isGoingBack = true, isGoingToBusinessDetails = false) }
    }

    fun retry() {
        _mapViewState.update { MapViewState() }
        _searchViewState.update { SearchViewState() }
        _mainViewState.update { MainViewState() }
        _detailsViewState.update { DetailsViewState() }
        getBusinesses()
    }
}