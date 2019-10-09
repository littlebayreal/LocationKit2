package com.sziti.locationkit2;

import android.widget.TextView;

import com.sziti.locationkit2.data.HistoryData;
import com.sziti.locationkit2.treeRecycleView.base.TreeItem;
import com.sziti.locationkit2.treeRecycleView.base.ViewHolder;

public class HistoryItem extends TreeItem<HistoryData> {
	@Override
	protected int initLayoutId() {
		return R.layout.item_image_show;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder) {
		((TextView) viewHolder.getView(R.id.item_longitude)).setText(getData().getLongitude());
		((TextView) viewHolder.getView(R.id.item_latitude)).setText(getData().getLatitude());
		((TextView) viewHolder.getView(R.id.item_time)).setText(getData().getTime());
	}
}
