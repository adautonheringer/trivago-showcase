package services.mappers

data class Business(
    val id: String,
    val name: String,
    val imageUrl: String,
    val categories: List<String>,
    val price: String,
    val rating: Float,
    val latLng: LatLng,
) {
    data class LatLng(
        val lat: Double,
        val lng: Double,
    )
}
