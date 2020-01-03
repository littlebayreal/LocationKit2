package com.sziti.locationkit2.data

class SiteInfo(PositionID:String,SiteID:String,SiteName:String ,SiteDirection:String,
		Latitude:Double,Longitude:Double,
		SitePhotos:String,RequestedBy:String){
	var PositionID:String = ""
	var SiteID:String = ""
	var SiteName:String = ""
	var SiteDirection:String = ""
	var Latitude:Double = 0.0
	var Longitude:Double = 0.0
	var SitePhotos:String = ""
	var RequestedBy:String = ""
	init {
	    this.PositionID = PositionID
		this.SiteID = SiteID
		this.SiteName = SiteName
		this.SiteDirection = SiteDirection
		this.Latitude = Latitude
		this.Longitude = Longitude
		this.SitePhotos = SitePhotos
		this.RequestedBy = RequestedBy
	}
}
