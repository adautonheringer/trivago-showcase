package com.joinus.trivagoshowcase.features.map

import services.mappers.Business

data class MapViewState(
    val isLoading: Boolean = false,
    val onRefreshClicked: Boolean = false,
    val businesses: List<Business> = listOf(),
    val snapedViewId: String? = null,
)