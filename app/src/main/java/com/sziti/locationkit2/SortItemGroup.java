package com.sziti.locationkit2;

import com.sziti.locationkit2.data.LetterData;
import com.sziti.locationkit2.treeRecycleView.base.ItemHelperFactory;
import com.sziti.locationkit2.treeRecycleView.base.TreeItem;
import com.sziti.locationkit2.treeRecycleView.base.TreeItemGroup;
import com.sziti.locationkit2.treeRecycleView.base.ViewHolder;

import java.util.List;

public class SortItemGroup extends TreeItemGroup<LetterData> {

    @Override
    protected List<TreeItem> initChildList(LetterData data) {
        return ItemHelperFactory.createTreeItemList(data.getDatas(), SortItem.class, this);
    }

    @Override
    protected int initLayoutId() {
        return R.layout.item_sort_group;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder) {
        viewHolder.setText(R.id.tv_content,data.getLetter());
    }
}
