package com.sziti.locationkit2

import com.sziti.locationkit2.treeRecycleView.base.TreeItem;
import com.sziti.locationkit2.treeRecycleView.base.ViewHolder

class HistoryHeaderItem: TreeItem<HistoryHeaderData>() {
	override fun initLayoutId(): Int {
		return R.layout.item_history_header
	}

	override fun onBindViewHolder(viewHolder: ViewHolder?) {

	}
}
