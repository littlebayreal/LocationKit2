package com.sziti.locationkit2.data

class QueryPositionRecordResult{
	var Code = 0
	var Model:List<QueryPositionRecord> ?= null

	class QueryPositionRecord(siteID:String,positionID:String,latitude:String,longitude:String,measuringTime:String) {
		var SiteID:String = ""
		var PositionID:String = ""
		var Latitude:String = ""
		var Longitude:String = ""
		var MeasuringTime:String = ""
		init {
			this.SiteID = siteID
			this.PositionID = positionID
			this.Latitude = latitude
			this.Longitude = longitude
			this.MeasuringTime = measuringTime
		}
		override fun toString(): String {
			return "QueryPositionRecord(SiteID='$SiteID', PositionID='$PositionID', Latitude='$Latitude', Longitude='$Longitude', MeasuringTime='$MeasuringTime')"
		}
	}

	override fun toString(): String {
		return "QueryPositionRecordResult(Code=$Code, Model=$Model)"
	}

}

