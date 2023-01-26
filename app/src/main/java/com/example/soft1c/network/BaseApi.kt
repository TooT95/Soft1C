package com.example.soft1c.network

import retrofit2.http.GET
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Query

interface BaseApi {

    @GET("authorization") //authorization
    fun auth(): Call<ResponseBody>

    @GET("Priemki") //authorization
    fun acceptanceList(): Call<ResponseBody>

    @GET("DanniePriemki") //authorization
    fun acceptance(@Query("Nomer") number:String): Call<ResponseBody>

}