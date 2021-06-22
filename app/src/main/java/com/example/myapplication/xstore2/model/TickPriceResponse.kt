package com.example.myapplication.xstore2.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetTickPricesResponse constructor(status: Boolean, returnData: GetTickPricesReturnData) : BaseResponse<GetTickPricesReturnData>(
    status, returnData
)

@JsonClass(generateAdapter = true)
data class GetTickPricesReturnData(
    val ask: Double,
    val askVolume: Int,
    val bid: Double,
    val bidVolume: Int,
    val high: Double,
    val level: Int,
    val low: Double,
    val quoteId: Int,
    val spreadRaw: Double,
    val spreadTable: Double,
    val symbol: String,
    val timestamp: Long = System.currentTimeMillis()
)