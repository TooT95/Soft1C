package com.example.soft1c.repository

import com.example.soft1c.model.Acceptance
import com.example.soft1c.model.SizeAcceptance
import com.example.soft1c.network.Network
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AcceptanceSizeRepository {

    suspend fun getSizeDataApi(acceptanceGuid: String): SizeAcceptance {
        return suspendCoroutine { continuation ->
            Network.api.getAcceptanceSizeData(acceptanceGuid).enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string() ?: ""
                        continuation.resume(getSizeDataFromJson(responseBody))
                    } else {
                        continuation.resume(SizeAcceptance(dataArray = emptyList()))
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    private fun getSizeDataFromJson(responseBody: String): SizeAcceptance {
        val jsonObject = JSONObject(responseBody)
        val recordAllowed = jsonObject.getBoolean(RECORD_ALLOWED_KEY)
        val sum = jsonObject.getInt(SUM_KEY)
        val allWeight = jsonObject.getInt(ALL_WEIGHT_KEY)
        val priceM = jsonObject.getInt(PRICE_M_KEY)
        val priceWeight = jsonObject.getInt(PRICE_WEIGHT_KEY)
        val dataArray = if (recordAllowed) {
            getSizeDataArray(jsonObject.getJSONArray(ARRAY_DATA_KEY))
        } else {
            emptyList()
        }
        return SizeAcceptance(recordAllowed = recordAllowed,
            sum = sum,
            allWeight = allWeight,
            priceM3 = priceM,
            priceWeight = priceWeight,
            dataArray = dataArray)
    }

    private fun getSizeDataArray(jsonArray: JSONArray): List<SizeAcceptance.SizeData> {
        val dataList = mutableListOf<SizeAcceptance.SizeData>()
        for (item in 0 until jsonArray.length()) {
            val jsonItemObject = jsonArray.getJSONObject(item)
            val seatNumber = jsonItemObject.getInt(SEAT_NUMBER_KEY)
            val length = jsonItemObject.getInt(LENGTH_KEY)
            val width = jsonItemObject.getInt(WIDTH_KEY)
            val height = jsonItemObject.getInt(HEIGHT_KEY)
            val weight = jsonItemObject.getInt(WEIGHT_KEY)
            dataList.add(SizeAcceptance.SizeData(seatNumber = seatNumber,
                length = length,
                width = width,
                height = height,
                weight = weight))
        }
        return dataList
    }

    companion object {
        const val RECORD_ALLOWED_KEY = "ЗаписьРазрешена"
        const val SUM_KEY = "Сумма"
        const val ALL_WEIGHT_KEY = "ОбщийОбъем"
        const val PRICE_M_KEY = "ЦенаЗаКуб"
        const val PRICE_WEIGHT_KEY = "ЦенаЗаТонну"
        const val ARRAY_DATA_KEY = "МассивДанных"
        const val SEAT_NUMBER_KEY = "НомерМеста"
        const val LENGTH_KEY = "Длина"
        const val WIDTH_KEY = "Ширина"
        const val HEIGHT_KEY = "Высота"
        const val WEIGHT_KEY = "Объем"
    }
}