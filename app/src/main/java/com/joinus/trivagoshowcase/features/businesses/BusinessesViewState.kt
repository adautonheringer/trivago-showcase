package com.joinus.trivagoshowcase.features.businesses

import services.mappers.Business

data class BusinessesViewState(
    val isLoading: Boolean = false,
    val businesses: List<Business> = listOf(),
    val mapViewPosition: Int? = null,
)