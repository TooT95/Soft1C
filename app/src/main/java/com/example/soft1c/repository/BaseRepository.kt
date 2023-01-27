package com.example.soft1c.repository

import com.example.soft1c.Utils
import com.example.soft1c.model.*
import com.example.soft1c.network.Network
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.suspendCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BaseRepository() {

    suspend fun getAccessToken(): Boolean {
        return suspendCoroutine { continuation ->
            Network.api.auth()
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        if (response.isSuccessful) {
                            continuation.resume(true)
                            return
                        }
                        continuation.resume(false)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Timber.d("error ${t.message}")
                        continuation.resumeWithException(t)
                    }

                })
        }
    }

    suspend fun getAnyApi(type: Int): Pair<Int, List<AnyModel>> {
        return suspendCoroutine { continuation ->
            when (type) {
                Utils.ObjectModelType.ADDRESS -> Network.api.addressList()
                Utils.ObjectModelType._PACKAGE -> Network.api.packageList()
                Utils.ObjectModelType.PRODUCT_TYPE -> Network.api.productTypeList()
                Utils.ObjectModelType.ZONE -> Network.api.zoneList()
                else -> Network.api.addressList()
            }
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()?.string() ?: ""
                            continuation.resume(Pair(type, getAddressListJson(responseBody, type)))
                        } else {
                            continuation.resume(Pair(Utils.ObjectModelType.EMPTY, emptyList()))
                        }

                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }

                })

        }
    }

    private fun getAddressListJson(responseBody: String, type: Int): List<AnyModel> {
        val addressList = mutableListOf<AnyModel>()
        val arrayJson = JSONArray(responseBody)
        for (item in 0 until arrayJson.length()) {
            val objectJson = arrayJson.getJSONObject(item)
            val ref = objectJson.getString(Utils.Contracts.REF_KEY)
            val name = objectJson.getString(Utils.Contracts.NAME_KEY)
            val code = objectJson.getString(Utils.Contracts.CODE_KEY)
            val anyObject = when (type) {
                Utils.ObjectModelType.ADDRESS -> AnyModel.AddressModel(ref, name, code)
                Utils.ObjectModelType._PACKAGE -> AnyModel.PackageModel(ref, name, code)
                Utils.ObjectModelType.PRODUCT_TYPE -> AnyModel.ProductType(ref, name, code)
                Utils.ObjectModelType.ZONE -> AnyModel.Zone(ref, name, code)
                else -> AnyModel.AddressModel(ref, name, code)
            }
            addressList.add(anyObject)
        }
        return addressList
    }
}