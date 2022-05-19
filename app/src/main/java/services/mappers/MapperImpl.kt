package services.mappers

import com.google.android.gms.maps.model.LatLng
import services.network.GetBusinessDetailsResponse
import services.network.GetBusinessesResponse

object MapperImpl : Mapper {
    override fun toBusinessModel(businessesResponse: GetBusinessesResponse.BusinessesResponse): Business {
        return Business(
            id = businessesResponse.id,
            name = businessesResponse.name,
            imageUrl = businessesResponse.imageUrl,
            categories = businessesResponse.categories.map { it.title },
            price = businessesResponse.price ?: "$",
            rating = businessesResponse.rating,
            latLng = LatLng(
                businessesResponse.coordinates.latitude,
                businessesResponse.coordinates.longitude,
            )
        )
    }

    override fun toBusinessDetailModel(businessDetailsResponse: GetBusinessDetailsResponse): BusinessDetails {
        return BusinessDetails(
            id = businessDetailsResponse.id,
            name = businessDetailsResponse.name,
            imageUrl = businessDetailsResponse.imageUrl,
            photos = businessDetailsResponse.photos,
            categories = businessDetailsResponse.categories.map { it.title },
            price = businessDetailsResponse.price,
            rating = businessDetailsResponse.rating,
            displayAddress = businessDetailsResponse.location.displayAddress,
            displayPhone = businessDetailsResponse.displayPhone,
        )
    }
}