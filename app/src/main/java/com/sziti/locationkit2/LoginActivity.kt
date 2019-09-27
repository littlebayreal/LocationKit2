package com.sziti.locationkit2

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

		btn_login.setOnClickListener(View.OnClickListener {
			startActivity(Intent(this@LoginActivity,MainActivity::class.java))
		})
	}
    //切换横竖屏调用
	override fun onConfigurationChanged(newConfig: Configuration?) {
		super.onConfigurationChanged(newConfig)
		Log.i("LoginActivity","横竖屏切换:"+ newConfig?.orientation)
//		if(newConfig?.orientation == Configuration.ORIENTATION_PORTRAIT){
//
//		}
	}
}
