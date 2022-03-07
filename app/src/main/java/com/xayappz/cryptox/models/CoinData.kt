package com.xayappz.cryptox.models

import com.google.gson.annotations.SerializedName


class CoinData {

    @SerializedName("data")
    private val dataCoin: List<Data?>? = null

    fun getDataCoin(): List<Data?>? {
        return dataCoin
    }

    var ErrorOccured: String = ""

    fun getError(): String {
        return ErrorOccured
    }

    @SerializedName("status")
    private val status: Status? = null
    fun getStatus(): Status? {
        return status
    }


}