package com.xayappz.cryptox.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xayappz.cryptox.models.CoinData
import com.xayappz.cryptox.models.Data
import com.xayappz.cryptox.repository.RepositoryCoin


class CoinViewModel constructor(val repository: RepositoryCoin) : ViewModel() {
    var coinListData = MutableLiveData<CoinData?>()
    var data: ArrayList<Data?>
    var coinListDataSearch: MutableLiveData<List<Data?>>?

    init {

        getAllCoinsReponse()
        data = ArrayList()
        coinListDataSearch = MutableLiveData<List<Data?>>()
    }

    fun getAllCoinsReponse() {
        coinListData = repository.getCoinData()
    }


    fun getData(): MutableLiveData<CoinData?> {
        return coinListData
    }


    fun addSearchData(CoinData: List<Data?>) {
        coinListDataSearch?.value = CoinData


    }


    fun getSearchData(): MutableLiveData<List<Data?>> {
        return coinListDataSearch!!
    }


}