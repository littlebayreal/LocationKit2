package com.sziti.locationkit2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var stopName:TextView?=null
    var stopLocation:TextView?=null
	var longitude:TextView?=null
	var latitude:TextView?=null


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		initView()
		Log.i("zxb","stopLocation:"+ stopLocation)
	}
	fun initView(){
		stopName = findViewById(R.id.tv_stop_name)
		stopLocation = findViewById(R.id.tv_stop_location)
		longitude = findViewById(R.id.tv_longitude)
		latitude=findViewById(R.id.tv_latitude)
	}
}
