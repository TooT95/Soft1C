package com.example.soft1c.network

import com.example.soft1c.Utils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

object Network {

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocket = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocket, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { _, _ -> true }
        builder.addInterceptor(BasicAuthInterceptor(Utils.username, Utils.password))
        return builder
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(BasicAuthInterceptor(Utils.username, Utils.password))
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .client(getUnsafeOkHttpClient().build())
        .baseUrl(Utils.base_url + Utils.auth)
        .build()

    val api: BaseApi = retrofit.create(BaseApi::class.java)

    const val KEY_BASENAME = "key_basename"
    const val KEY_USERNAME = "key_username"
    const val KEY_PASSWORD = "key_password"
    const val KEY_BASE_URL = "key_base_url"
}