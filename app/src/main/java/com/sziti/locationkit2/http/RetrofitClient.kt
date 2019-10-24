package com.sziti.locationkit2.http

import com.sziti.locationkit2.GlobalObjects
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor



class RetrofitClient {
	companion object {
		var apiService: APIService?=null
		private fun getHttpClient(): OkHttpClient {
			var logInterceptor =
				HttpLoggingInterceptor(HttpLog()).setLevel(HttpLoggingInterceptor.Level.BODY)
			return OkHttpClient.Builder()
				.readTimeout(40, TimeUnit.SECONDS)
				.connectTimeout(40, TimeUnit.SECONDS)
				.addInterceptor(logInterceptor)
				.build()
		}
		fun getInstance(): APIService {
			if (apiService == null) {
				val retrofit = Retrofit.Builder()
					.baseUrl(GlobalObjects.base_url)
					.addConverterFactory(GsonConverterFactory.create())
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.client(getHttpClient())
					.build()
				apiService = retrofit.create(APIService::class.java)
			}
			return apiService!!
		}
	}
}
