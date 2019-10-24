package com.sziti.locationkit2.data;

import com.google.gson.annotations.SerializedName;

public class FileUploadResultData {
	@SerializedName("Flag")
	private Boolean flag;
    @SerializedName("Msg")
    private String savedFileInfo;

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public String getSavedFileInfo() {
        return savedFileInfo;
    }

    public void setSavedFileInfo(String savedFileInfo) {
        this.savedFileInfo = savedFileInfo;
    }
}
