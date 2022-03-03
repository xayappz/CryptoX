package com.xayappz.cryptox.repository

import com.xayappz.cryptox.apiinterfaces.ApiInterfaceService

open class RepositoryCoin(private val retrofitService: ApiInterfaceService) {

     fun getAllCoins() = retrofitService.getAllCoinsResponse()

}