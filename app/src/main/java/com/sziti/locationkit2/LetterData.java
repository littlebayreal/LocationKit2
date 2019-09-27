package com.sziti.locationkit2;


import com.sziti.locationkit2.treeRecycleView.base.BaseItemData;

import java.util.List;

public class LetterData extends BaseItemData {
    //首字母
    private String letter;
    //字母下的子数据
    private List<String> datas;

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

	public List<String> getDatas() {
		return datas;
	}

	public void setDatas(List<String> datas) {
		this.datas = datas;
	}
}
