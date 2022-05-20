package com.joinus.trivagoshowcase

import android.view.View
import services.mappers.Business
import services.mappers.BusinessDetails

data class MainViewState(
    val isGoingToBusinessDetails: Boolean = false,
    val isGoingBack: Boolean = false,
    val isError: Boolean = false,
    val sharedViews: List<View> = listOf(),
    val business: Business? = null,
)

data class SearchViewState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isRefreshButtonVisible: Boolean = true,
    )

data class BusinessesViewState(
    val isLoading: Boolean = false,
    val businesses: List<Business> = listOf(),
    val mapViewPosition: Int? = null,
    val isError: Boolean = false,
)

data class MapViewState(
    val isLoading: Boolean = false,
    val onRefreshClicked: Boolean = false,
    val businesses: List<Business> = listOf(),
    val snapedViewId: String? = null,
    val isError: Boolean = false,
)

data class DetailsViewState(
    val isLoading: Boolean = false,
    val businessDetails: BusinessDetails? = null,
    val isError: Boolean = false,
)