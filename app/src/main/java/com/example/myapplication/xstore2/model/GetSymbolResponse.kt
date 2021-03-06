package com.example.myapplication.xstore2.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetSymbolResponse(
    status: Boolean,
    customTag: String?,
    errorCode: String?,
    returnData: GetSymbolResponseReturnData?
) : BaseResponse<GetSymbolResponseReturnData>(
    status, returnData, customTag, errorCode
)

@JsonClass(generateAdapter = true)
data class GetSymbolResponseReturnData(
    val symbol: String = "",
    val marginHedgedStrong: Boolean = false,
    val leverage: Double = 0.0,
    val marginHedged: Int = 0,
    val stepRuleId: Int = 0,
    val pipsPrecision: Int = 0,
    val shortSelling: Boolean = false,
    val precision: Int = 0,
    val spreadTable: Double = 0.0,
    val description: String = "",
    val marginMode: Int = 0,
    val type: Int = 0,
    val categoryName: String = "",
    val lotMax: Int = 0,
    val tickSize: Double = 0.0,
    val initialMargin: Int = 0,
    val longOnly: Boolean = false,
    val trailingEnabled: Boolean = false,
    val high: Double = 0.0,
    val spreadRaw: Double = 0.0,
    val swapRolloverDays: Int = 0,
    val currencyProfit: String = "",
    val low: Double = 0.0,
    val percentage: Int = 0,
    val quoteIdCross: Int = 0,
    val currency: String = "",
    val contractSize: Int = 0,
    val lotMin: Double = 0.0,
    val currencyPair: Boolean = false,
    val swapEnable: Boolean = false,
    val instantMaxVolume: Int = 0,
    val exemode: Int = 0,
    val swapType: Int = 0,
    val lotStep: Double = 0.0,
    val profitMode: Int = 0,
    val stopsLevel: Int = 0,
    val quoteId: Int = 0,
    val swapLong: Double = 0.0,
    val swapShort: Double = 0.0,
    val marginMaintenance: Int = 0,
    val groupName: String = "",
    val tickValue: Double = 0.0,
    val timeString: String = "",
    val ask: Double = 0.0,
    val expiration: Long? = 0,
    val time: Long = 0,
    val bid: Double = 0.0,
    val starting: Long? = 0
)