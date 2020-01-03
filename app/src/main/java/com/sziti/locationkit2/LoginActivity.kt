package com.sziti.locationkit2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.sziti.locationkit2.data.BaseResult
import com.sziti.locationkit2.data.LoginData
import com.sziti.locationkit2.http.RetrofitClient
import kotlinx.android.synthetic.main.activity_login.*
import rx.Scheduler
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers

class LoginActivity : AppCompatActivity() {
	var t =
		"{\"Guid\":\"5b1d03e3-4875-4f80-93e0-08b685c8b108\",\"LoginName\":\"SuperAdmin\",\"RealName\":\"超级管理员\",\"Password\":null,\"CompanyId\":\"71134daa-0916-4196-8664-faa0b25df1b9\",\"PhoneNo\":\"18662241371\",\"TelNo\":\"67876980\",\"Email\":\"zhenhua.xie@163.com\",\"Type\":null,\"Name\":\"苏州智能交通信息科技股份有限公司\",\"DataBDCompanyGUID\":\"4d95ac5d-d54c-4e15-9c5d-aff153d77c30\",\"MenuList\":[{\"MenuName\":\"Module1\",\"MenuUri\":\"/123\",\"MenuIcon\":\"abc\",\"SubMenuList\":[{\"MenuName\":\"Module2\",\"MenuUri\":\"/abc\",\"MenuIcon\":\"bcd\",\"SubMenuList\":null}]}],\"RoleList\":[{\"RoleName\":\"Role1\",\"RoleID\":\"1\"}],\"ErrorCode\":0}"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		//检测定位权限
		checkPermission()

		btn_login.setOnClickListener(object : View.OnClickListener {
			override fun onClick(p0: View?) {
//				Log.i("zzz","点击选择:"+(if(btn_login_rg.checkedRadioButtonId == btn_login_pad.id)"测点" else "随手拍"))
				RetrofitClient.getInstance()
					.mobileLogin(username.text.toString(), password.text.toString())
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : Subscriber<LoginData>() {
						override fun onNext(t: LoginData?) {
							if ((t as LoginData).ErrorCode == 0) {
								if (btn_login_rg.checkedRadioButtonId == btn_login_pad.id)
									startActivity(
										Intent(
											this@LoginActivity,
											MainActivity::class.java
										)
									)
								else
									startActivity(
										Intent(
											this@LoginActivity,
											WebActivity::class.java
										)
									)
								GlobalObjects.Username = t.LoginName
								finish()
							} else {
								Toast.makeText(this@LoginActivity, "账号或者密码错误!", Toast.LENGTH_SHORT)
									.show()
							}
						}

						override fun onCompleted() {

						}

						override fun onError(e: Throwable?) {
							Log.e("LoginActivity", e?.message)
						}
					})
			}
		})
//		btn_login_pad.setOnClickListener(View.OnClickListener {
//
//			RetrofitClient.getInstance()
//				.mobileLogin(username.text.toString(), password.text.toString())
//				.subscribeOn(Schedulers.io())
//				.observeOn(AndroidSchedulers.mainThread())
//				.subscribe(object : Subscriber<LoginData>() {
//					override fun onNext(t: LoginData?) {
//						if ((t as LoginData).ErrorCode == 0) {
//								startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//								finish()
//						} else {
//							Toast.makeText(this@LoginActivity, "账号或者密码错误!", Toast.LENGTH_SHORT)
//								.show()
//						}
//					}
//
//					override fun onCompleted() {
//
//					}
//
//					override fun onError(e: Throwable?) {
//						Log.e("LoginActivity", e?.message)
//					}
//				})
//		})
//		btn_login_phone.setOnClickListener(View.OnClickListener {
//
//		})
	}

	fun checkPermission() {
		//查看是否已有权限
		val checkSelfPermission = ContextCompat.checkSelfPermission(
			LoginActivity@ this,
			Manifest.permission.ACCESS_FINE_LOCATION
		)
		if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
			//可以添加多个权限申请
			val permissions = arrayOf(
				Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.ACCESS_FINE_LOCATION
			)
			requestPermissions(permissions, 1)
		}
	}

	/**
	 * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
	 * @param context
	 * @return 平板返回 True，手机返回 False
	 */
	fun isPad(context: Context): Boolean {
		return context.getResources().getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
	}

	//切换横竖屏调用
	override fun onConfigurationChanged(newConfig: Configuration?) {
		super.onConfigurationChanged(newConfig)
		Log.i("LoginActivity", "横竖屏切换:" + newConfig?.orientation)
	}

	/***
	 * 权限请求结果  在Activity 重新这个方法 得到获取权限的结果  可以返回多个结果
	 */
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		//是否获取到权限
		if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(LoginActivity@ this, "由于您拒绝提供定位信息，软件无法正常使用", Toast.LENGTH_SHORT).show()
		}
	}
}
