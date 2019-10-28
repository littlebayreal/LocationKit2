package com.sziti.locationkit2.http

import com.sziti.locationkit2.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable
interface APIService {
	/**
	 * 登录
	 */
	@POST("/MobileLogin")
	@FormUrlEncoded
	@Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
	fun mobileLogin(@Field("UserAccount")UserAccount:String,@Field("UserPassword")UserPassword:String):Observable<LoginData>
	/**
	 * 上传点位数据
	 */
	@POST("Mobile/SaveSitePosition")
	@FormUrlEncoded
	@Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
	fun saveSitePosition(
		@Field("PositionID") PositionID:String,@Field("SiteID") SiteID:String, @Field("Latitude")Latitude:Double, @Field("Longitude")Longitude:Double,
		@Field("SitePhotos")SitePhotos:String,@Field("RequestedBy") RequestedBy:String):Observable<CommonResult>
	/**
	 * 获取站点列表
	 */
	@POST("Mobile/BDSiteInfoGetList")
	@FormUrlEncoded
	@Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
	fun getBDSiteInfoList(
		@Field("IsPage") IsPage:String,@Field("CurrentPage") CurrentPage:Int, @Field("PageSize")PageSize:Int):Observable<BDSiteInfoData>
	/**
	 * 查询历史点位
	 */
	@POST("Mobile/QuerySitePositionRecord")
	@FormUrlEncoded
	@Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
	fun querySitePositionRecord(@Field("SiteID")SiteID:String,@Field("IsPage")IsPage:Boolean,
								@Field("PageSize")PageSize:Int,@Field("CurrentPage")CurrentPage:Int):Observable<QueryPositionRecordResult>

	/**
	 * 删除历史点位记录
	 */
	@POST("Mobile/DeleteSitePosition")
	@FormUrlEncoded
	@Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
	fun deleteSitePosition(@Field("PositionID") PositionID:String):Observable<CommonResult>

	/**
	 * 上传图片(多张)
	 */
	@POST("Upload/UploadFile")
	fun FileUpload(@Body m:MultipartBody): Observable<FileUploadResultData>

	/**
	 * 上传图片(多张)
	 */
	@Multipart
	@POST("Upload/UploadFile")
	@Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
	fun FileUpload(@PartMap m:HashMap<String, RequestBody>):Observable<FileUploadResultData>
}
