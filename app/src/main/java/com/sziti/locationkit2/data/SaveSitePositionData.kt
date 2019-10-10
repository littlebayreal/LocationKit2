package com.sziti.locationkit2.data

class SaveSitePositionData constructor(
	positionData: String, siteID: String, latitude: Double, longitude: Double,
	sitePhotos: String, userID: String
) {
	var PositionID: String = ""
	var SiteID: String = ""
	var Latitude: Double = 0.0
	var Longitude: Double = 0.0
	var SitePhotos: String = ""
	var RequestedBy: String = ""

	init {
		this.PositionID = positionData
		this.SiteID = siteID
		this.Latitude = latitude
		this.Longitude = longitude
		this.SitePhotos = sitePhotos
		this.RequestedBy = userID
	}

	override fun toString(): String {
		return "SaveSitePositionData(PositionID='$PositionID', SiteID='$SiteID', Latitude=$Latitude, Longitude=$Longitude, SitePhotos='$SitePhotos', RequestedBy='$RequestedBy')"
	}
}
