package services.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.yelp.com/v3/businesses/"
const val AUTH_HEADER = "Authorization"
const val API_KEY = "eCIicZI97gytGORMinAMrd208ujDIonk05uAL4AkerOyq3W9TGLJul89vRIEejVpQzrgIsOPANTvVdkTrc1xjhcyn6TpWQ_wIrT_2wp53mXR9qE5Ho3zd5MuIoaDYnYx"

object RetrofitInstance {
    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient()
                    .newBuilder()
                    .addInterceptor { chain ->
                        chain.proceed(
                            chain
                                .request()
                                .newBuilder()
                                .addHeader(AUTH_HEADER, "BEARER $API_KEY")
                                .build(),
                        )
                    }
                    .build(),
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun getYelpServices(): YelpServices {
        return retrofit.create(YelpServices::class.java)
    }
}