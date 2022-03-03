package com.xayappz.cryptox.models

data class Data(
    val circulating_supply: Double,
    val cmc_rank: Double,
    val date_added: String,
    val id: Double,
    val last_updated: String,
    val max_supply: Double,
    val name: String,
    val num_market_pairs: Double,
    val platform: Any,
    val quote: Quote,
    val self_reported_circulating_supply: Any,
    val self_reported_market_cap: Any,
    val slug: String,
    val symbol: String,
    val tags: List<String>,
    val total_supply: Double
)
data class Quote(
    val USD: USD
)
data class ResponseApi(
    val data: List<Data>,
    val status: Status
)
data class Status(
    val credit_count: Int,
    val elapsed: Int,
    val error_code: Int,
    val error_message: Any,
    val notice: Any,
    val timestamp: String,
    val total_count: Int
)
data class USD(
    val fully_diluted_market_cap: Double,
    val last_updated: String,
    val market_cap: Double,
    val market_cap_dominance: Double,
    val percent_change_1h: Double,
    val percent_change_24h: Double,
    val percent_change_30d: Double,
    val percent_change_60d: Double,
    val percent_change_7d: Double,
    val percent_change_90d: Double,
    val price: Double,
    val volume_24h: Double,
    val volume_change_24h: Double
)