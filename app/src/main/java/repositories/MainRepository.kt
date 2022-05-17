package repositories

import retrofit2.HttpException
import services.network.ApiResponse
import services.mappers.Business
import services.mappers.MapperImpl
import services.network.RetrofitInstance
import services.mappers.BusinessDetails
import java.io.IOException

object MainRepository {

    suspend fun getBusinesses(term: String): ApiResponse<List<Business>> {
        return try {
            val response = RetrofitInstance
                .getYelpServices()
                .getBusinesses(term = term)
            val businessList = response
                .businesses.map {
                    MapperImpl.toBusinessModel(it)
                }
            ApiResponse.Success(data = businessList)
        } catch (e: HttpException) {
            ApiResponse.Error(exception = e)
        } catch (e: IOException) {
            ApiResponse.Error(exception = e)
        }
    }

    suspend fun getBusinessDetails(id: String): ApiResponse<BusinessDetails> {
        return try {
            val response = RetrofitInstance
                .getYelpServices()
                .getBusinessDetails(id = id)
            val businessDetails = MapperImpl.toBusinessDetailModel(response)
            ApiResponse.Success(data = businessDetails)
        } catch (e: HttpException) {
            ApiResponse.Error(exception = e)
        } catch (e: IOException) {
            ApiResponse.Error(exception = e)
        }
    }
}