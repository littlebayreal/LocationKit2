package com.sziti.locationkit2.data;


import com.sziti.locationkit2.treeRecycleView.base.BaseItemData;

import java.util.List;

public class LetterData extends BaseItemData {
    //首字母
    private String letter;
    //字母下的子数据
    private List<BDSiteInfoData.ModelData> datas;

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

	public List<BDSiteInfoData.ModelData> getDatas() {
		return datas;
	}

	public void setDatas(List<BDSiteInfoData.ModelData> datas) {
		this.datas = datas;
	}

	@Override
	public String toString() {
		return "LetterData{" +
			"letter='" + letter + '\'' +
			", datas=" + datas +
			'}';
	}
}
