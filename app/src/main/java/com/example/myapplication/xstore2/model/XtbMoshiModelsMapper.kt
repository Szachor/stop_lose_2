package com.example.myapplication.xstore2.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

// TODO Should be use used this class instead of the inclass solutions
object XtbMoshiModelsMapper {
    val moshi: Moshi = Moshi.Builder().build()

    val TickPricesResponseJsonAdapter: JsonAdapter<GetTickPricesResponse> =
        moshi.adapter(GetTickPricesResponse::class.java)
    val TickPricesStreamingResponseJsonAdapter: JsonAdapter<GetTickPricesStreamingResponse> =
        moshi.adapter(GetTickPricesStreamingResponse::class.java)
    val SymbolResponseJsonAdapter: JsonAdapter<GetSymbolResponse> =
        moshi.adapter(GetSymbolResponse::class.java)
    val AllSymbolsResponseJsonAdapter: JsonAdapter<GetAllSymbolsResponse> =
        moshi.adapter(GetAllSymbolsResponse::class.java)
}
