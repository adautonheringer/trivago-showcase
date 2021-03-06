package repositories

import retrofit2.HttpException
import services.network.ApiResponse
import services.mappers.Business
import services.mappers.MapperImpl
import services.network.RetrofitInstance
import services.mappers.BusinessDetails
import services.network.YelpServices
import java.io.IOException
import javax.inject.Inject

class MainRepository @Inject constructor(private val yelpServices: YelpServices) {

    suspend fun getBusinesses(lat: Double, lng: Double): ApiResponse<List<Business>> {
        return try {
            val response = yelpServices.getBusinesses(lat, lng)
            val businesses = response.businesses.map { MapperImpl.toBusinessModel(it) }
            ApiResponse.Success(data = businesses)
        } catch (e: HttpException) {
            ApiResponse.Error(exception = e)
        } catch (e: IOException) {
            ApiResponse.Error(exception = e)
        }
    }

    suspend fun getBusinessDetails(id: String): ApiResponse<BusinessDetails> {
        return try {
            val response = yelpServices.getBusinessDetails(id = id)
            val businessDetails = MapperImpl.toBusinessDetailModel(response)
            ApiResponse.Success(data = businessDetails)
        } catch (e: HttpException) {
            ApiResponse.Error(exception = e)
        } catch (e: IOException) {
            ApiResponse.Error(exception = e)
        }
    }
}