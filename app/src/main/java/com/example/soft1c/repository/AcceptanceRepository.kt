package com.example.soft1c.repository

import com.example.soft1c.Utils
import com.example.soft1c.model.Acceptance
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

    suspend fun createUpdateAccApi(acceptance: Acceptance): Pair<Acceptance, Boolean> {
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
                            continuation.resume(Pair(acceptance, true))
                        } else {
                            continuation.resume(Pair(acceptance, false))
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
        val ref = acceptJson.getString(REF_KEY)
        val number = acceptJson.getString(NUMBER_KEY)
        val client = acceptJson.getString(CLIENT_KEY)
        val packageUid = acceptJson.getString(PACKAGE_UID_KEY)
        val packageName = getPackageNameFromUid(packageUid)
        if (hasAdditionalFields) {
            val zoneUid = acceptJson.getString(ZONE_KEY)
            val zoneName = getZoneNameFromUid(zoneUid)
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
        private const val ALL_WEIGHT_KEY = "ОбщийВес"
        private const val PACKAGE_UID_KEY = "ТипУпаковки"
        private const val PRODUCT_TYPE_KEY = "ВидТовара"
        private const val STORE_UID_KEY = "АдресМагазина"
        private const val PHONE_KEY = "ТелефонМагазина"
        private const val STORE_NAME_KEY = "НаименованиеМагазина"
        private const val REPRESENTATIVE_NAME_KEY = "ИмяПредставителя"
        private const val BATCH_GUID_KEY = "GUIDПартии"
        private const val Z_KEY = "ZТовар"
        private const val BRAND_KEY = "Брэнд"
        private const val GLASS_KEY = "Стекло"
        private const val EXPENSIVE_KEY = "Дорогой"
        private const val NOT_TURN_OVER_KEY = "НеКантовать"
    }

}