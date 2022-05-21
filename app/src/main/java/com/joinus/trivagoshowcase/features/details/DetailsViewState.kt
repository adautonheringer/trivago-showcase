package com.joinus.trivagoshowcase.features.details

import services.mappers.BusinessDetails

data class DetailsViewState(
    val isLoading: Boolean = false,
    val businessDetails: BusinessDetails? = null,
)