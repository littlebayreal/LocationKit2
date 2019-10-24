package com.sziti.locationkit2

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
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
	var t = "{\"Guid\":\"5b1d03e3-4875-4f80-93e0-08b685c8b108\",\"LoginName\":\"SuperAdmin\",\"RealName\":\"超级管理员\",\"Password\":null,\"CompanyId\":\"71134daa-0916-4196-8664-faa0b25df1b9\",\"PhoneNo\":\"18662241371\",\"TelNo\":\"67876980\",\"Email\":\"zhenhua.xie@163.com\",\"Type\":null,\"Name\":\"苏州智能交通信息科技股份有限公司\",\"DataBDCompanyGUID\":\"4d95ac5d-d54c-4e15-9c5d-aff153d77c30\",\"MenuList\":[{\"MenuName\":\"Module1\",\"MenuUri\":\"/123\",\"MenuIcon\":\"abc\",\"SubMenuList\":[{\"MenuName\":\"Module2\",\"MenuUri\":\"/abc\",\"MenuIcon\":\"bcd\",\"SubMenuList\":null}]}],\"RoleList\":[{\"RoleName\":\"Role1\",\"RoleID\":\"1\"}],\"ErrorCode\":0}"
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

		btn_login.setOnClickListener(View.OnClickListener {
			RetrofitClient.getInstance().mobileLogin(username.text.toString(),password.text.toString())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object:Subscriber<LoginData>(){
					override fun onNext(t: LoginData?) {
						if((t as LoginData).ErrorCode == 0){
							startActivity(Intent(this@LoginActivity,MainActivity::class.java))
							finish()
						}else{
							Toast.makeText(this@LoginActivity,"账号或者密码错误!",Toast.LENGTH_SHORT).show()
						}
					}
					override fun onCompleted() {

					}
					override fun onError(e: Throwable?) {
						Log.e("LoginActivity",e?.message)
					}
				})
		})
	}
    //切换横竖屏调用
	override fun onConfigurationChanged(newConfig: Configuration?) {
		super.onConfigurationChanged(newConfig)
		Log.i("LoginActivity","横竖屏切换:"+ newConfig?.orientation)
	}
}
