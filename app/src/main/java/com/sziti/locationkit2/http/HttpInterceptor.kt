package com.sziti.locationkit2.http

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset

class HttpInterceptor:Interceptor{
	val TAG = this.javaClass.simpleName

	override fun intercept(chain: Interceptor.Chain?): Response {
		var request = chain?.request()
		Log.e(
			TAG, String.format(
				"发送请求 %s on %s%n%s",
				request?.url(), chain?.connection(), request?.headers()
			)
		)
		request = request?.newBuilder()
			?.build()
		val response = chain?.proceed(request)
		val costTime =
			(response!!.receivedResponseAtMillis() - response!!.sentRequestAtMillis()) / 1000.0f
        response.body()
        Log.i(TAG, "response body:"+ response.body().contentLength())
        Log.d(TAG,"request bodyLength:"+ request?.body()?.contentLength())
		Log.i(TAG, "response.code:" + response.code())
		Log.i(
			TAG,
			String.format(
				"[%s] %s, %d in %.2f ms",
				request?.method(),
				request?.url(),
				response.code(),
				costTime
			)
		)
		Log.i(TAG, "-----------------------------------")
		Log.i(TAG, "          Request Headers          ")
		Log.i(TAG, "-----------------------------------")
		Log.i(TAG, String.format("%s", request?.headers()))
		Log.i(TAG, "-----------------------------------")
		Log.i(TAG, "          Request Body          ")
		Log.i(TAG, String.format("%s", request?.body()))
		Log.i(TAG, "-----------------------------------")
		Log.i(TAG, "-----------------------------------")
		Log.i(TAG, "          Response Headers          ")
		Log.i(TAG, "-----------------------------------")
		Log.i(TAG, String.format("%s", response.headers()))
		Log.i(TAG, " ")
		Log.i(TAG, " ")
		//获得返回的body，注意此处不要使用responseBody.string()获取返回数据，原因在于这个方法会消耗返回结果的数据(buffer)
		val responseBody = response.body()

		//为了不消耗buffer，我们这里使用source先获得buffer对象，然后clone()后使用
		val source = responseBody.source()
		source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
		//获得返回的数据
		val buffer = source.buffer()
		//使用前clone()下，避免直接消耗
		Log.e(TAG, "response:" + buffer.clone().readString(Charset.forName("UTF-8")))

		return response
	}
}
