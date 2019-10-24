package com.sziti.locationkit2.data

class BDSiteInfoData {
	var Code:Int = 0
	var Msg:String = ""
	var Model:List<ModelData> ?= null
	var Total = 0

	class ModelData {
		var Guid = ""
		var SName = ""
		var SNoteQueryGUID = ""
		var SBoardGUID = ""
		var SDirect = ""
		var SLON = ""
		var SLAT = ""
	}
}


