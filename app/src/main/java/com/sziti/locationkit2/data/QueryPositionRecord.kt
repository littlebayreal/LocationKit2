package com.sziti.locationkit2.data

class QueryPositionRecord(siteID:String,positionID:String,latitude:String,longitude:String) {
	var SiteID:String = ""
	var PositionID:String = ""
	var Latitude:String = ""
	var Longitude:String = ""
    init {
        this.SiteID = siteID
		this.PositionID = positionID
		this.Latitude = latitude
		this.Longitude = longitude
    }

	override fun toString(): String {
		return "QueryPositionRecord(SiteID='$SiteID', PositionID='$PositionID', Latitude='$Latitude', Longitude='$Longitude')"
	}
}
