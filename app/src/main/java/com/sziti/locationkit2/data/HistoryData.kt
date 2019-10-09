package com.sziti.locationkit2.data

/**
 * 历史定位数据bean类
 */
class HistoryData constructor(type:Int,longitude:String,latitude:String,time:String) {
	/**
	 * 定义数据类型  0为头部布局 1为内容布局
	 */
	var type:Int = 1
	var longitude: String = ""
		set(value) {
			field = value
		}
		get() = field
	var latitude: String = ""
        set(value) {
			field = value
		}
	    get() = field
	var time: String = ""
		set(value) {
			field = value
		}
		get() {
			return field
		}
	init {
		this.type = type
		this.longitude = longitude
		this.latitude = latitude
		this.time = time
	}
}
