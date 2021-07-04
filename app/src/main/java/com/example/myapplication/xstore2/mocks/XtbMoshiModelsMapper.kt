package com.example.myapplication.xstore2.mocks

import com.example.myapplication.xstore2.model.GetAllSymbolsResponse
import com.example.myapplication.xstore2.model.GetSymbolResponse
import com.example.myapplication.xstore2.model.GetTickPricesResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class XtbMoshiModelsMapper {
    companion object{
        val moshi: Moshi = Moshi.Builder().build()

        private val getTickPricesResponseAdapter: JsonAdapter<GetTickPricesResponse> =
            moshi.adapter(GetTickPricesResponse::class.java)
        private val getSymbolResponseAdapter: JsonAdapter<GetSymbolResponse> =
            moshi.adapter(GetSymbolResponse::class.java)
        private val getAllSymbolsResponseAdapter: JsonAdapter<GetAllSymbolsResponse> =
            moshi.adapter(GetAllSymbolsResponse::class.java)

        fun getTickPrices(jsonString: String): GetTickPricesResponse? {
            return getTickPricesResponseAdapter.fromJson(jsonString)
        }

        fun getSymbol(jsonString: String): GetSymbolResponse? {
            return getSymbolResponseAdapter.fromJson(jsonString)
        }

        fun getAllSymbols(jsonString: String): GetAllSymbolsResponse? {
            return getAllSymbolsResponseAdapter.fromJson(jsonString)
        }

    }
}