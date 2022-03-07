package com.xayappz.cryptox.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xayappz.cryptox.models.CoinData
import com.xayappz.cryptox.repository.RepositoryCoin


class CoinViewModel constructor(private val repository: RepositoryCoin) : ViewModel() {
    var coinListData = MutableLiveData<CoinData?>()

    var searchEnabled: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>(false)

    fun isSearchEnabled(yesLBoolean: Boolean) {
        searchEnabled.value = yesLBoolean
    }


    fun getAllCoinsReponse() {
        coinListData = repository.getCoinData()
    }

    fun getData(): MutableLiveData<CoinData?> {
        return coinListData
    }

}