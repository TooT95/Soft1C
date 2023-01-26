package com.example.soft1c.network

import retrofit2.http.GET
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Query

interface BaseApi {

    @GET("authorization")
    fun auth(): Call<ResponseBody>

    @GET("Priemki")
    fun acceptanceList(): Call<ResponseBody>

    @GET("DanniePriemki")
    fun acceptance(@Query("Nomer") number:String): Call<ResponseBody>

    @GET("Addressa")
    fun addressList(): Call<ResponseBody>

    @GET("Zoni")
    fun zoneList(): Call<ResponseBody>

    @GET("TipiUpakovki")
    fun packageList(): Call<ResponseBody>

    @GET("VidiTovarov")
    fun productTypeList(): Call<ResponseBody>
}