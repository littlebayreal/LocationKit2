package com.sziti.locationkit2.data

 class LoginData {
//	{
//		"Guid": "5b1d03e3-4875-4f80-93e0-08b685c8b108",
//		"LoginName": "SuperAdmin",
//		"RealName": "超级管理员",
//		"Password": null,
//		"CompanyId": "71134daa-0916-4196-8664-faa0b25df1b9",
//		"PhoneNo": "18662241371",
//		"TelNo": "67876980",
//		"Email": "zhenhua.xie@163.com",
//		"Type": null,
//		"Name": "苏州智能交通信息科技股份有限公司",
//		"DataBDCompanyGUID": "4d95ac5d-d54c-4e15-9c5d-aff153d77c30",
//		"MenuList": [{
//		"MenuName": "Module1",
//		"MenuUri": "/123",
//		"MenuIcon": "abc",
//		"SubMenuList": [{
//		"MenuName": "Module2",
//		"MenuUri": "/abc",
//		"MenuIcon": "bcd",
//		"SubMenuList": null
//	}]
//	}],
//		"RoleList": [{
//		"RoleName": "Role1",
//		"RoleID": "1"
//	}],
//		"ErrorCode": 0
//	}

	//	ErrorCode:3 密码错
//	ErrorCode:2 账户不存在
	var ErrorCode = 0

	var RoleList:List<RoleListData> ?=null

	class RoleListData {
		var RoleName: String? = null
		var RoleID: String? = null

		override fun toString(): String {
			return "RoleListData(RoleName='$RoleName', RoleID='$RoleID')"
		}

	}
}
