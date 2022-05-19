package services.mappers

import com.google.android.gms.maps.model.LatLng

data class Business(
    val id: String,
    val name: String,
    val imageUrl: String,
    val categories: List<String>,
    val price: String,
    val rating: Float,
    val latLng: LatLng,
)
