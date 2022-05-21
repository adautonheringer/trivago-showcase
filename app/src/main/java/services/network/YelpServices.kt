package services.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YelpServices {
    @GET("search")
    suspend fun getBusinesses(
        @Query("latitude") latitude: Double = 51.233334,
        @Query("longitude") location: Double = 6.783333,
        @Query("limit") limit: Int = 15,
    ): GetBusinessesResponse

    @GET("{id}")
    suspend fun getBusinessDetails(
        @Path("id") id: String,
    ): GetBusinessDetailsResponse
}