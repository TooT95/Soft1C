package com.example.soft1c.repository

import com.example.soft1c.model.Acceptance
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

class AcceptanceRepository() {

    suspend fun getAcceptanceListApi(): List<Acceptance> {
        return suspendCoroutine { continuation ->
            Network.api.acceptanceList().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string() ?: ""
                        continuation.resume(getAcceptanceList(responseBody))
                    } else {
                        continuation.resume(emptyList())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }

    }

    private fun getAcceptanceList(responseString: String): List<Acceptance> {
        val acceptanceList = mutableListOf<Acceptance>()
        val acceptArray = JSONArray(responseString)
        for (index in 0 until acceptArray.length()) {
            val acceptJson = acceptArray.getJSONObject(index)
            val acceptance = getaAcceptanceFromJsonObject(acceptJson)
            acceptanceList.add(acceptance)
        }
        return acceptanceList
    }

    private fun getaAcceptanceFromJsonObject(acceptJson: JSONObject): Acceptance {
//        val ref = acceptJson.getString(REF_KEY)
        val number = acceptJson.getString(NUMBER_KEY)
        val client = acceptJson.getString(CLIENT_KEY)
        val packageName = acceptJson.getString(PACKAGE_NAME_KEY)
//        val productTypeName = acceptJson.getString(PRODUCT_TYPE_NAME_KEY)
//        val zoneName = acceptJson.getString(ZONE_NAME_KEY)
//        val date = acceptJson.getString(DATE_KEY)
        val weight = acceptJson.getBoolean(WEIGHT_KEY)
        val capacity = acceptJson.getBoolean(CAPACITY_KEY)
//        val zone = acceptJson.getString(ZONE_KEY)
//        val packageUid = acceptJson.getString(PACKAGE_KEY)
//        val productType = acceptJson.getString(PRODUCT_TYPE_KEY)
        return Acceptance(number = number,
            client = client,
            _package = packageName,
            weight = weight,
            capacity = capacity)
    }

    companion object {
        private const val REF_KEY = "Ссылка"
        private const val NUMBER_KEY = "Номер"
        private const val CLIENT_KEY = "Клиент"
        private const val PACKAGE_NAME_KEY = "ТипУпаковкиНаименование"
        private const val PRODUCT_TYPE_NAME_KEY = "ВидТовараНаименование"
        private const val ZONE_NAME_KEY = "ЗонаНаименование"
        private const val DATE_KEY = "Дата"
        private const val WEIGHT_KEY = "Вес"
        private const val CAPACITY_KEY = "Замер"
        private const val ZONE_KEY = "Зона"
        private const val PACKAGE_KEY = "ТипУпаковки"
        private const val PRODUCT_TYPE_KEY = "ВидТовара"
    }

}