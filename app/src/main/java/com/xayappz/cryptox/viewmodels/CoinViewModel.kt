package com.xayappz.cryptox.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xayappz.cryptox.models.CoinData
import com.xayappz.cryptox.models.Data
import com.xayappz.cryptox.repository.RepositoryCoin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CoinViewModel constructor(private val repository: RepositoryCoin) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val coinListData = MutableLiveData<CoinData>()
    val coinListDataSearch = MutableLiveData<Data?>()

    var searchEnabled: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>(false)

    fun isSearchEnabled(yesLBoolean: Boolean) {
        searchEnabled.setValue(yesLBoolean)
    }

    fun getSearchEnabledLiveData(): MutableLiveData<Boolean> {
        return searchEnabled
    }

    fun getAllCoinsReponse() {
        val response = repository.getAllCoins()
        response.enqueue(object : Callback<CoinData> {
            override fun onResponse(
                call: Call<CoinData>,
                response: Response<CoinData>
            ) {

                errorMessage.postValue("OK")
                coinListData.postValue(response.body())
                coinListData.value = response.body()
            }

            override fun onFailure(call: Call<CoinData>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }


    fun addSearchData(CoinData: Data?) {
        coinListDataSearch.value = CoinData
    }


    fun getSearchData(): MutableLiveData<Data?> {
        return coinListDataSearch
    }
}