package com.example.soft1c.network

import retrofit2.http.GET
import okhttp3.ResponseBody
import retrofit2.Call

interface BaseApi {

    @GET("authorization") //authorization
    fun auth(): Call<ResponseBody>

}