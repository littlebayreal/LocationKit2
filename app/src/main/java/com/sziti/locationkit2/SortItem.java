package com.sziti.locationkit2;

import android.widget.ImageView;
import android.widget.TextView;

import com.sziti.locationkit2.treeRecycleView.base.TreeItem;
import com.sziti.locationkit2.treeRecycleView.base.ViewHolder;

public class SortItem extends TreeItem<String> {
    @Override
    protected int initLayoutId() {
        return R.layout.item_sort_child;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder) {
		((TextView)viewHolder.getView(R.id.item_sort_child_tv)).setText(getData());
        //设置点击事件
//        viewHolder.setOnClickListener(R.id.tv_call,getData().getOnClickListener());
    }
}
