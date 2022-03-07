package com.xayappz.cryptox.apiinterfaces

import com.xayappz.cryptox.BuildConfig
import com.xayappz.cryptox.models.CoinData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiInterfaceService {
    @Headers(BuildConfig.API_KEY)
    @GET(BuildConfig.COIN_ENDPOINT)
    fun getAllCoinsResponse(): Call<CoinData>


    companion object {
        var retrofitService: ApiInterfaceService? = null

        fun getInstance(): ApiInterfaceService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(ApiInterfaceService::class.java)
            }
            return retrofitService!!
        }
    }
}
