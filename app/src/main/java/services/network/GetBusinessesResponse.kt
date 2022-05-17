package services.network

import com.google.gson.annotations.SerializedName

data class GetBusinessesResponse(
    @SerializedName("total")
    val total: Int,
    @SerializedName("businesses")
    val businesses: List<BusinessesResponse>,
) {
    data class BusinessesResponse(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("image_url")
        val imageUrl: String,
        @SerializedName("categories")
        val categories: List<BusinessCategoryResponse>,
        @SerializedName("price")
        val price: String?,
        @SerializedName("rating")
        val rating: Float,
        @SerializedName("coordinates")
        val coordinates: CoordinatesResponse
    )

    data class BusinessCategoryResponse(
        @SerializedName("title")
        val title: String,
    )

    data class CoordinatesResponse(
        @SerializedName("latitude")
        val latitude: Double,
        @SerializedName("longitude")
        val longitude: Double,
    )
}
