package com.xayappz.cryptox.apiinterfaces

import com.xayappz.cryptox.models.CoinData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiInterfaceService {
    @Headers("X-CMC_PRO_API_KEY:253d6962-09da-45b8-89ff-58bda8c00b6d")
    @GET("cryptocurrency/listings/latest")
    fun getAllCoinsResponse(): Call<CoinData>


    companion object {
        var retrofitService: ApiInterfaceService? = null

        fun getInstance(): ApiInterfaceService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://pro-api.coinmarketcap.com/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(ApiInterfaceService::class.java)
            }
            return retrofitService!!
        }
    }
}
