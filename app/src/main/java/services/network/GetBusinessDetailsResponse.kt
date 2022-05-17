package services.network

import com.google.gson.annotations.SerializedName

data class GetBusinessDetailsResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("photos")
    val photos: List<String>,
    @SerializedName("categories")
    val categories: List<GetBusinessesResponse.BusinessCategoryResponse>,
    @SerializedName("price")
    val price: String,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("location")
    val location: Location,
    @SerializedName("display_phone")
    val displayPhone: String,
) {
    data class Location(
        @SerializedName("display_address")
        val displayAddress: List<String>
    )
}

