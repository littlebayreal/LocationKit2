package com.sziti.locationkit2.http;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLog implements HttpLoggingInterceptor.Logger {
	@Override
	public void log(String message) {
		Log.d("HttpLogInfo", message);
	}
}
