package services.mappers

data class BusinessDetails(
    val id: String,
    val name: String,
    val imageUrl: String,
    val photos: List<String>,
    val categories: List<String>,
    val price: String?,
    val rating: Float,
    val displayAddress: List<String>,
    val displayPhone: String,
)
