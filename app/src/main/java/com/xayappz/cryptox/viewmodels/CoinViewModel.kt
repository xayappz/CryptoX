package com.xayappz.cryptox.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xayappz.cryptox.models.CoinData
import com.xayappz.cryptox.models.Data
import com.xayappz.cryptox.repository.RepositoryCoin


class CoinViewModel constructor(val repository: RepositoryCoin) : ViewModel() {
    var coinListData = MutableLiveData<CoinData?>()
    var data: ArrayList<Data?>
    var coinListDataSearch: MutableLiveData<List<Data?>>?
    var searchIndex = MutableLiveData<Int?>()

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

    fun setSearchIndex(position: Int) {
        searchIndex?.value = position
    }

    fun getSearchPosition(): MutableLiveData<Int?> {
        return searchIndex
    }

    fun getSearchData(): MutableLiveData<List<Data?>> {
        return coinListDataSearch!!
    }


}