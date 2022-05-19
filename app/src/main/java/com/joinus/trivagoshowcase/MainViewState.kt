package com.joinus.trivagoshowcase

import services.mappers.Business

data class MainViewState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val businesses: List<Business> = emptyList(),
    val onMapViewClick: Int? = null,
    val snapedViewId: String? = null,
    val isGoingToBusinessDetails: Boolean = false,
)

//sealed class MainVS {
//    object Loading : MainVS()
//    object Error: MainVS()
//    object GoToBusinessDetails: MainVS()
//    data class Businesses(val data: List<Business> = emptyList())
//    data class SelectedBusiness
//}
