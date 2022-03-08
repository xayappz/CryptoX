package com.xayappz.cryptox.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.xayappz.cryptox.apiinterfaces.ApiInterfaceService
import com.xayappz.cryptox.models.CoinData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryCoin(private val retrofitService: ApiInterfaceService) {
    var coinListData = MutableLiveData<CoinData?>()


    var getAllCoins: Call<CoinData> = retrofitService.getAllCoinsResponse()


    fun getCoinData(): MutableLiveData<CoinData?> {
        val response = getAllCoins.clone()
        response.enqueue(object : Callback<CoinData> {
            override fun onResponse(
                call: Call<CoinData>,
                response: Response<CoinData>
            ) {
                coinListData.value = response.body()
            }

            override fun onFailure(call: Call<CoinData>, t: Throwable) {

                coinListData.value = null

            }
        })
        return coinListData
    }

}