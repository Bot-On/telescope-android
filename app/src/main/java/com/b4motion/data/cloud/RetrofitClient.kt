package com.b4motion.data.cloud

import android.content.Context
import com.b4motion.geolocation.BuildConfig
import io.reactivex.android.plugins.RxAndroidPlugins
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by frodriguez on 7/18/2018.
 *
 */
class RetrofitClient {

    companion object {

        private var retrofit: Retrofit? = null

        fun getClient(): Retrofit {
            if(retrofit == null){
                val interceptorLogHeaders = HttpLoggingInterceptor()
                val interceptorLogBody = HttpLoggingInterceptor()
                val headersInterceptor = Interceptor { chain ->
                    val request = chain.request()?.newBuilder()?.
                            addHeader("Accept", "application/json")?.
                            addHeader("Authorization", Credentials.basic("gps@theboton.io","xdmT7v1yfdwgbeQN"))?.
                            build()
                    chain.proceed(request)
                }

                interceptorLogHeaders.level = HttpLoggingInterceptor.Level.HEADERS
                interceptorLogBody.level = HttpLoggingInterceptor.Level.BODY

                val client = OkHttpClient.Builder()
                        .addInterceptor(headersInterceptor)
                        .addInterceptor(interceptorLogHeaders)
                        .addInterceptor(interceptorLogBody)
                        .build()

                retrofit = Retrofit.Builder()
                        .baseUrl(BuildConfig.URL_BASE)
                        .client(client)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit!!
        }

        fun resetClient() {
            retrofit = null
        }

    }
}