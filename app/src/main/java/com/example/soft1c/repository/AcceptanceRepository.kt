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

    suspend fun getAcceptanceApi(number: String): Acceptance {
        return suspendCoroutine { continuation ->
            Network.api.acceptance(number).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string() ?: ""
                        continuation.resume(getAcceptanceJson(responseBody))
                    } else {
                        continuation.resume(Acceptance(""))
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

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

    private fun getAcceptanceJson(responseString: String): Acceptance {
        val jsonArray = JSONArray(responseString)
        return getaAcceptanceFromJsonObject(jsonArray.getJSONObject(0), true)
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

    private fun getaAcceptanceFromJsonObject(
        acceptJson: JSONObject,
        hasAdditionalFields: Boolean = false,
    ): Acceptance {
        val number = acceptJson.getString(NUMBER_KEY)
        val client = acceptJson.getString(CLIENT_KEY)
        if (hasAdditionalFields) {
            val ref = acceptJson.getString(REF_KEY)
            val zoneUid = acceptJson.getString(ZONE_KEY)
            val autoNumber = acceptJson.getString(AUTO_NUMBER_KEY)
            val idCard = acceptJson.getString(ID_CARD_KEY)
            val countSeat = acceptJson.getInt(COUNT_SEAT_KEY)
            val countInPackage = acceptJson.getInt(COUNT_IN_PACKAGE_KEY)
            val countPackage = acceptJson.getInt(COUNT_PACKAGE_KEY)
//            val allWeight = acceptJson.getDouble(ALL_WEIGHT_KEY)
            val packageUid = acceptJson.getString(PACKAGE_UID_KEY)
            val storeUid = acceptJson.getString(STORE_UID_KEY)
            val productType = acceptJson.getString(PRODUCT_TYPE_KEY)
            val phoneNumber = acceptJson.getString(PHONE_KEY)
            val storeName = acceptJson.getString(STORE_NAME_KEY)
            val representativeName = acceptJson.getString(REPRESENTATIVE_NAME_KEY)
            val batchGuid = acceptJson.getString(BATCH_GUID_KEY)
            return Acceptance(ref = ref,
                countPackage = countPackage,
                batchGuid = batchGuid,
                autoNumber = autoNumber,
                countInPackage = countInPackage,
                number = number,
                phoneNumber = phoneNumber,
                packageUid = packageUid,
                storeName = storeName,
                productType = productType,
                countSeat = countSeat,
                storeUid = storeUid,
                idCard = idCard,
//                allWeight = allWeight,
                client = client,
                zoneUid = zoneUid,
                representativeName = representativeName)
        }
        val weight = acceptJson.getBoolean(WEIGHT_KEY)
        val capacity = acceptJson.getBoolean(CAPACITY_KEY)
        return Acceptance(number = number,
            client = client,
            weight = weight,
            capacity = capacity)
    }

    companion object {
        private const val REF_KEY = "Ссылка"
        private const val NUMBER_KEY = "Номер"
        private const val CLIENT_KEY = "Клиент"
        private const val AUTO_NUMBER_KEY = "НомерАвто"
        private const val ID_CARD_KEY = "IDПродавца"
        private const val WEIGHT_KEY = "Вес"
        private const val CAPACITY_KEY = "Замер"
        private const val ZONE_KEY = "Зона"
        private const val COUNT_SEAT_KEY = "КоличествоМест"
        private const val COUNT_IN_PACKAGE_KEY = "КоличествоВУпаковке"
        private const val COUNT_PACKAGE_KEY = "КоличествоТиповУпаковок"
        private const val ALL_WEIGHT_KEY = "КоличествоТиповУпаковок"
        private const val PACKAGE_UID_KEY = "ТипУпаковки"
        private const val PRODUCT_TYPE_KEY = "ВидТовара"
        private const val STORE_UID_KEY = "АдресМагазина"
        private const val PHONE_KEY = "ТелефонМагазина"
        private const val STORE_NAME_KEY = "НаименованиеМагазина"
        private const val REPRESENTATIVE_NAME_KEY = "ИмяПредставителя"
        private const val BATCH_GUID_KEY = "GUIDПартии"
    }

}