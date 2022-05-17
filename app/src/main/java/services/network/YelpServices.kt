package services.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YelpServices {
    @GET("search")
    suspend fun getBusinesses (
        @Query("term") term: String? = null,
        @Query("location") location: String = "New York City",
        @Query("limit") limit: Int = 50,
    ) : GetBusinessesResponse

    @GET("{id}")
    suspend fun getBusinessDetails (
        @Path("id") id: String,
    ) : GetBusinessDetailsResponse



}