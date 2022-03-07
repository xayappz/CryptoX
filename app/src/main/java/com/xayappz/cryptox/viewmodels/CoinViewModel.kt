package com.xayappz.cryptox.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xayappz.cryptox.models.CoinData
import com.xayappz.cryptox.models.Data
import com.xayappz.cryptox.repository.RepositoryCoin


class CoinViewModel constructor(private val repository: RepositoryCoin) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    var coinListData = MutableLiveData<CoinData?>()
    val coinListDataSearch = MutableLiveData<Data?>()

    var searchEnabled: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>(false)

    fun isSearchEnabled(yesLBoolean: Boolean) {
        searchEnabled.value = yesLBoolean
    }


    init {
        getAllCoinsReponse()
    }

    private fun getAllCoinsReponse() {
        coinListData = repository.getCoinData()
    }

    fun getData(): MutableLiveData<CoinData?> {
        return coinListData
    }

    fun addSearchData(CoinData: Data?) {
        coinListDataSearch.value = CoinData
    }


    fun getSearchData(): MutableLiveData<Data?> {
        return coinListDataSearch
    }
}