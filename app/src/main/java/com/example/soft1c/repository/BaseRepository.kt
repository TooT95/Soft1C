package com.example.soft1c.repository

import com.example.soft1c.network.BaseApi
import com.example.soft1c.network.Network
import okhttp3.ResponseBody
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
//                            val responseBody = response.body()?.string() ?: ""
//                            Timber.d("responseBody $responseBody")
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

}