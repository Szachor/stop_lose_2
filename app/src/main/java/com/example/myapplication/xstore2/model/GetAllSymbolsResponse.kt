package com.example.myapplication.xstore2.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetAllSymbolsResponse(status: Boolean, returnData: List<GetSymbolResponseReturnData>?) :
    BaseResponse<List<GetSymbolResponseReturnData>>(status, returnData)