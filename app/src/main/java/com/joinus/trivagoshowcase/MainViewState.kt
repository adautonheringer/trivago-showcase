package com.joinus.trivagoshowcase

import android.view.View
import services.mappers.Business

data class MainViewState(
    val isGoingToBusinessDetails: Boolean = false,
    val isGoingBack: Boolean = false,
    val isError: Boolean = false,
    val sharedViews: List<View> = listOf(),
    val business: Business? = null,
)

