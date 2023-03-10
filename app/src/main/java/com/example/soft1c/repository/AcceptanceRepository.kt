package com.example.soft1c.repository

import com.example.soft1c.adapter.AcceptanceAdapter
import com.example.soft1c.utils.Utils
import com.example.soft1c.model.Acceptance
import com.example.soft1c.model.AcceptanceEnableVisible
import com.example.soft1c.model.AnyModel
import com.example.soft1c.model.Client
import com.example.soft1c.network.Network
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AcceptanceRepository {

    suspend fun getAcceptanceApi(number: String): Pair<Acceptance, List<AcceptanceEnableVisible>> {
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
                        continuation.resume(Pair(Acceptance(""), emptyList()))
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

    suspend fun getClientApi(clientCode: String): Pair<Client, Boolean> {
        return suspendCoroutine { continuation ->
            Network.api.client(clientCode).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string() ?: ""
                        val jsonObject = JSONArray(responseBody).getJSONObject(0)
                        val code = jsonObject.getString(Client.CODE_KEY)
                        val serialDoc = jsonObject.getString(Client.SERAIL_DOC_KEY)
                        val numberDoc = jsonObject.getString(Client.NUMBER_DOC_KEY)
                        continuation.resume(Pair(Client(code, serialDoc, numberDoc), true))
                    } else {
                        continuation.resume(Pair(Client(), false))
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    suspend fun createUpdateAccApi(acceptance: Acceptance): Pair<Acceptance, String> {
        return suspendCoroutine { continuation ->
            val jsonObject = JSONObject()
            if (acceptance.ref.isNotEmpty())
                jsonObject.put(REF_KEY, acceptance.ref)
            else
                jsonObject.put(REF_KEY, null)
            jsonObject.put(CLIENT_KEY, acceptance.client)
            jsonObject.put(PACKAGE_UID_KEY, acceptance.packageUid)
            jsonObject.put(ZONE_KEY, acceptance.zoneUid)
            jsonObject.put(ID_CARD_KEY, acceptance.idCard)
            jsonObject.put(STORE_UID_KEY, acceptance.storeUid)
            jsonObject.put(PHONE_KEY, acceptance.phoneNumber)
            jsonObject.put(STORE_NAME_KEY, acceptance.storeName)
            jsonObject.put(PRODUCT_TYPE_KEY, acceptance.productType)
            jsonObject.put(REPRESENTATIVE_NAME_KEY, acceptance.representativeName)
            jsonObject.put(AUTO_NUMBER_KEY, acceptance.autoNumber)
            jsonObject.put(COUNT_SEAT_KEY, acceptance.countSeat)
            jsonObject.put(COUNT_IN_PACKAGE_KEY, acceptance.countInPackage)
            jsonObject.put(ALL_WEIGHT_KEY, acceptance.allWeight)
            jsonObject.put(GLASS_KEY, acceptance.glass)
            jsonObject.put(EXPENSIVE_KEY, acceptance.expensive)
            jsonObject.put(Z_KEY, acceptance.z)
            jsonObject.put(NOT_TURN_OVER_KEY, acceptance.notTurnOver)
            jsonObject.put(BRAND_KEY, acceptance.brand)
            jsonObject.put(COUNT_PACKAGE_KEY, acceptance.countPackage)
            val requestBody =
                jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            Network.api.createUpdateAcceptance(requestBody)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        if (response.isSuccessful) {
                            val jsonString = response.body()?.string() ?: ""
                            if (jsonString.isEmpty())
                                continuation.resume(Pair(acceptance, ""))
                            val ref = JSONArray(jsonString).getJSONObject(0).getString(REF_KEY)
                            acceptance.ref = ref
                            AcceptanceAdapter.ACCEPTANCE_GUID = ref
                            continuation.resume(Pair(acceptance, ""))
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val jsonError =
                                JSONArray(errorBody).getJSONObject(0).getString(ERROR_ARRAY)
                            continuation.resume(Pair(acceptance, jsonError))
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }

                })
        }
    }

    private fun getAcceptanceJson(responseString: String): Pair<Acceptance, List<AcceptanceEnableVisible>> {
        val jsonArray = JSONArray(responseString)
        val jsonObject = jsonArray.getJSONObject(0)
        val acceptance = getaAcceptanceFromJsonObject(jsonObject, true)
        val enableVisibleList = mutableListOf<AcceptanceEnableVisible>()
        val propertyArray = jsonObject.getJSONArray(FIELDS_PROPERTY_KEY)
        for (item in 0 until propertyArray.length()) {
            val propertyJson = propertyArray.getJSONObject(item)
            val field = propertyJson.getString(FIELD_KEY)
            val enable = propertyJson.getBoolean(ENABLE_KEY)
            val visible = propertyJson.getBoolean(VISIBLE_KEY)
            enableVisibleList.add(AcceptanceEnableVisible(field, enable, visible))
        }
        return Pair(acceptance, enableVisibleList)
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
        val ref = acceptJson.getString(REF_KEY)
        val number = acceptJson.getString(NUMBER_KEY)
        val client = acceptJson.getString(CLIENT_KEY)
        val packageUid = acceptJson.getString(PACKAGE_UID_KEY)
        val packageName = getPackageNameFromUid(packageUid)
        val zoneUid = acceptJson.getString(ZONE_KEY)
        val zoneName = getZoneNameFromUid(zoneUid)
        if (hasAdditionalFields) {
            val autoNumber = acceptJson.getString(AUTO_NUMBER_KEY)
            val idCard = acceptJson.getString(ID_CARD_KEY)
            val countSeat = acceptJson.getInt(COUNT_SEAT_KEY)
            val countInPackage = acceptJson.getInt(COUNT_IN_PACKAGE_KEY)
            val countPackage = acceptJson.getInt(COUNT_PACKAGE_KEY)
            val allWeight = acceptJson.getDouble(ALL_WEIGHT_KEY)
            val storeUid = acceptJson.getString(STORE_UID_KEY)
            val storeAddressName = getAddressNameFromUid(storeUid)
            val productType = acceptJson.getString(PRODUCT_TYPE_KEY)
            val productTypeName = getProductTypeFromUid(productType)
            val phoneNumber = acceptJson.getString(PHONE_KEY)
            val storeName = acceptJson.getString(STORE_NAME_KEY)
            val representativeName = acceptJson.getString(REPRESENTATIVE_NAME_KEY)
            val batchGuid = acceptJson.getString(BATCH_GUID_KEY)
            val z = acceptJson.getBoolean(Z_KEY)
            val brand = acceptJson.getBoolean(BRAND_KEY)
            val glass = acceptJson.getBoolean(GLASS_KEY)
            val expensive = acceptJson.getBoolean(EXPENSIVE_KEY)
            val notTurnOver = acceptJson.getBoolean(NOT_TURN_OVER_KEY)
            return Acceptance(
                z = z,
                brand = brand,
                glass = glass,
                expensive = expensive,
                notTurnOver = notTurnOver,
                ref = ref,
                countPackage = countPackage,
                storeAddressName = storeAddressName,
                productTypeName = productTypeName,
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
                zone = zoneName,
                _package = packageName,
                allWeight = allWeight,
                client = client,
                zoneUid = zoneUid,
                representativeName = representativeName
            )
        }
        val weight = acceptJson.getBoolean(WEIGHT_KEY)
        val capacity = acceptJson.getBoolean(CAPACITY_KEY)
        return Acceptance(
            _package = packageName,
            zoneUid = zoneUid,
            zone = zoneName,
            packageUid = packageUid,
            ref = ref,
            number = number,
            client = client,
            weight = weight,
            capacity = capacity
        )
    }

    private fun getZoneNameFromUid(zoneUid: String): String {
        val elem = Utils.zones.find {
            (it as AnyModel.Zone).ref == zoneUid
        } ?: return ""
        return (elem as AnyModel.Zone).name
    }

    private fun getProductTypeFromUid(zoneUid: String): String {
        val elem = Utils.productTypes.find {
            (it as AnyModel.ProductType).ref == zoneUid
        } ?: return ""
        return (elem as AnyModel.ProductType).name
    }

    private fun getPackageNameFromUid(zoneUid: String): String {
        val elem = Utils.packages.find {
            (it as AnyModel.PackageModel).ref == zoneUid
        } ?: return ""
        return (elem as AnyModel.PackageModel).name
    }

    private fun getAddressNameFromUid(zoneUid: String): String {
        val elem = Utils.addressess.find {
            (it as AnyModel.AddressModel).ref == zoneUid
        } ?: return ""
        return (elem as AnyModel.AddressModel).name
    }

    companion object {
        const val REF_KEY = "????????????"
        const val NUMBER_KEY = "??????????"
        const val CLIENT_KEY = "????????????"
        const val AUTO_NUMBER_KEY = "??????????????????"
        const val ID_CARD_KEY = "ID????????????????"
        const val WEIGHT_KEY = "??????"
        const val CAPACITY_KEY = "??????????"
        const val ZONE_KEY = "????????"
        const val COUNT_SEAT_KEY = "????????????????????????????"
        const val COUNT_IN_PACKAGE_KEY = "??????????????????????????????????????"
        const val COUNT_PACKAGE_KEY = "??????????????????????????????????????????????"
        const val ALL_WEIGHT_KEY = "????????????????"
        const val PACKAGE_UID_KEY = "??????????????????????"
        const val PRODUCT_TYPE_KEY = "??????????????????"
        const val STORE_UID_KEY = "??????????????????????????"
        const val PHONE_KEY = "??????????????????????????????"
        const val STORE_NAME_KEY = "????????????????????????????????????????"
        const val REPRESENTATIVE_NAME_KEY = "????????????????????????????????"
        const val BATCH_GUID_KEY = "GUID????????????"
        const val Z_KEY = "Z??????????"
        const val BRAND_KEY = "??????????"
        const val GLASS_KEY = "????????????"
        const val EXPENSIVE_KEY = "??????????????"
        const val NOT_TURN_OVER_KEY = "??????????????????????"
        const val ERROR_ARRAY = "??????????????????????????"
        const val FIELDS_PROPERTY_KEY = "????????????????????????????????????"
        const val FIELD_KEY = "????????"
        const val ENABLE_KEY = "??????????????????????"
        const val VISIBLE_KEY = "??????????????????"
        const val ON_CHINESE = "??????????????????????"
    }

}