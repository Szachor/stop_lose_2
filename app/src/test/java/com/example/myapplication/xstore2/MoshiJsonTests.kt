package com.example.myapplication.xstore2

import com.example.myapplication.xstore2.model.GetAllSymbolsResponse
import com.example.myapplication.xstore2.model.GetSymbolResponse
import com.example.myapplication.xstore2.model.GetTickPricesResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import junit.framework.TestCase


open class MoshiJsonTests : TestCase() {

    var getSymbolResponseStringTestCase = """{
    "status": true,
    "returnData": {
        "symbol": "USDPLN",
        "currency": "USD",
        "categoryName": "FX",
        "currencyProfit": "PLN",
        "quoteId": 10,
        "quoteIdCross": 4,
        "marginMode": 101,
        "profitMode": 5,
        "pipsPrecision": 4,
        "contractSize": 100000,
        "exemode": 1,
        "time": 1624046399773,
        "expiration": null,
        "stopsLevel": 0,
        "precision": 5,
        "swapType": 1,
        "stepRuleId": 5,
        "type": 494,
        "instantMaxVolume": 2147483647,
        "groupName": "Emergings",
        "description": "American Dollar to Polish Zloty",
        "longOnly": false,
        "trailingEnabled": true,
        "marginHedgedStrong": false,
        "swapEnable": true,
        "percentage": 100.0,
        "bid": 3.83450,
        "ask": 3.83760,
        "high": 3.84480,
        "low": 3.80950,
        "lotMin": 0.01,
        "lotMax": 100.00,
        "lotStep": 0.01,
        "tickSize": 0.00001,
        "tickValue": 1.00000,
        "swapLong": -6.236,
        "swapShort": -7.918,
        "leverage": 1.00,
        "spreadRaw": 0.00310,
        "spreadTable": 31.0,
        "starting": null,
        "swap_rollover3days": 0,
        "marginMaintenance": 0,
        "marginHedged": 0,
        "initialMargin": 0,
        "currencyPair": true,
        "shortSelling": true,
        "timeString": "Fri Jun 18 21:59:59 CEST 2021"
    }
}"""

    private var getTickPricesResponseStringTestCase = """{"status": true,
"returnData": {
    "ask": 1.0,
    "askVolume": 10,
    "bid": 1.0,
    "bidVolume": 1,
    "high": 10.0,
    "level": 1,
    "low": 1.0,
    "quoteId": 1,
    "spreadRaw": 1.0,
    "spreadTable": 1.0,
    "symbol": "USDPLN",
    "timestamp": 12345678
}}"""

    private var getAllSymbolsResponseStringTestCase =
        """{"status":true,"returnData":[{"symbol":"CLX.US_4","currency":"USD","categoryName":"STC","currencyProfit":"USD","quoteId":6,"quoteIdCross":4,"marginMode":103,"profitMode":6,"pipsPrecision":2,"contractSize":1,"exemode":1,"time":1624046393575,"expiration":null,"stopsLevel":0,"precision":2,"swapType":2,"stepRuleId":12,"type":13,"instantMaxVolume":2147483647,"groupName":"US","description":"Clorox Co CFD","longOnly":false,"trailingEnabled":false,"marginHedgedStrong":false,"swapEnable":true,"percentage":100.0,"bid":174.23,"ask":174.35,"high":174.94,"low":173.00,"lotMin":1.00,"lotMax":1000000.00,"lotStep":1.00,"tickSize":0.01,"tickValue":0.01,"swapLong":-0.00717,"swapShort":-0.00672,"leverage":20.00,"spreadRaw":0.12,"spreadTable":12.0,"starting":null,"swap_rollover3days":0,"marginMaintenance":0,"marginHedged":0,"initialMargin":0,"currencyPair":false,"shortSelling":true,"timeString":"Fri Jun 18 21:59:53 CEST 2021"},{"symbol":"IDNA.UK_5","currency":"USD","categoryName":"ETF","currencyProfit":"USD","quoteId":6,"quoteIdCross":4,"marginMode":103,"profitMode":6,"pipsPrecision":4,"contractSize":1,"exemode":1,"time":1624031098585,"expiration":null,"stopsLevel":0,"precision":4,"swapType":2,"stepRuleId":272,"type":60,"instantMaxVolume":2147483647,"groupName":"ETF","description":"iShares MSCI North America UCITS ETF (Dist, USD) CFD","longOnly":true,"trailingEnabled":false,"marginHedgedStrong":false,"swapEnable":true,"percentage":100.0,"bid":79.1200,"ask":79.2300,"high":80.1300,"low":79.0400,"lotMin":1.00,"lotMax":1000000.00,"lotStep":1.00,"tickSize":0.0001,"tickValue":0.0001,"swapLong":-0.0071,"swapShort":-0.00679,"leverage":20.00,"spreadRaw":0.1100,"spreadTable":1100.0,"starting":null,"swap_rollover3days":0,"marginMaintenance":0,"marginHedged":0,"initialMargin":0,"currencyPair":false,"shortSelling":false,"timeString":"Fri Jun 18 17:44:58 CEST 2021"}]}"""

    fun testGetTickPricesResponse() {
        val moshi: Moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<GetTickPricesResponse> =
            moshi.adapter(GetTickPricesResponse::class.java)

        val getTickPricesResponse = adapter.fromJson(getTickPricesResponseStringTestCase)
        val tickPricesResponseJsonResult = adapter.toJson(getTickPricesResponse)

        assertEquals(
            removeUnlikedCharactersFromString(getTickPricesResponseStringTestCase),
            tickPricesResponseJsonResult
        )
    }

    fun testGetSymbolResponse() {
        val moshi: Moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<GetSymbolResponse> =
            moshi.adapter(GetSymbolResponse::class.java)

        val getSymbolResponseParsedJsonToClass = adapter.fromJson(getSymbolResponseStringTestCase)

        assertEquals("USDPLN", getSymbolResponseParsedJsonToClass?.returnData?.symbol)
    }

    fun testGetAllSymbolsResponse() {
        val moshi: Moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<GetAllSymbolsResponse> =
            moshi.adapter(GetAllSymbolsResponse::class.java)

        val getAllSymbolsResponseParsedJsonToClass = adapter.fromJson(getAllSymbolsResponseStringTestCase)

        assertEquals("CLX.US_4", getAllSymbolsResponseParsedJsonToClass?.returnData!![0].symbol)
    }



    // Removes spaces and \n from string
    private fun removeUnlikedCharactersFromString(string: String): String {
        return string.replace(" ", "")
            .replace("\n", "")
    }
}