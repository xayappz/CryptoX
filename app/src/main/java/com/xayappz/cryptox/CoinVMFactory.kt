package com.xayappz.cryptox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xayappz.cryptox.repository.RepositoryCoin
import com.xayappz.cryptox.viewmodels.CoinViewModel

class CoinVMFactory
constructor(private val repository: RepositoryCoin): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CoinViewModel::class.java)) {
            CoinViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}