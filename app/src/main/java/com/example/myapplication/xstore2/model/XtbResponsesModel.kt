package com.example.myapplication.xstore2.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


open class BaseResponse<V>(
    val status: Boolean,
    val returnData: V,
    val customTag: String? = null
)



