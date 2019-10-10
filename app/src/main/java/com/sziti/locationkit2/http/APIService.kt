package com.sziti.locationkit2.http

import com.sziti.locationkit2.data.BaseResult
import com.sziti.locationkit2.data.CommonResult
import com.sziti.locationkit2.data.QueryPositionRecord
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable
interface APIService {
	/**
	 * 上传点位数据
	 */
	@POST("Mobile/SaveSitePosition")
	@FormUrlEncoded
	fun saveSitePosition(
		@Field("PositionID") PositionID:String,@Field("SiteID") SiteID:String, @Field("Latitude")Latitude:Double, @Field("Longitude")Longitude:Double,
		@Field("SitePhotos")SitePhotos:String,@Field("RequestedBy") RequestedBy:String):Observable<BaseResult<CommonResult>>

	/**
	 * 查询历史点位
	 */
	@POST("Mobile/QuerySitePositionRecord")
	@FormUrlEncoded
	fun querySitePositionRecord(@Field("SiteID")SiteID:String,@Field("IsPage")IsPage:Boolean,
								@Field("PageSize")PageSize:Int,@Field("CurrentPage")CurrentPage:Int):Observable<BaseResult<List<QueryPositionRecord>>>

	/**
	 * 删除历史点位记录
	 */
	@POST("Mobile/DeleteSitePosition")
	@FormUrlEncoded
	fun deleteSitePosition(@Field("PositionID") PositionID:String):Observable<BaseResult<CommonResult>>
}
