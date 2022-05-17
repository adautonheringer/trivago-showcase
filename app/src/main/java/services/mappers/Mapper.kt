package services.mappers

import services.network.GetBusinessDetailsResponse
import services.network.GetBusinessesResponse.*

interface Mapper {
    fun toBusinessModel(
        businessesResponse: BusinessesResponse
    ) : Business

    fun toBusinessDetailModel(
        businessDetailsResponse: GetBusinessDetailsResponse
    ) : BusinessDetails
}