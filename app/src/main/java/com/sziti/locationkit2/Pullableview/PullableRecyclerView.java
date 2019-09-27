package com.sziti.locationkit2.Pullableview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class PullableRecyclerView extends RecyclerView implements Pullable{
	public PullableRecyclerView(@NonNull Context context) {
		super(context);
	}

	public PullableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown(float x, float y) {
//		if (recyclerView == null) return true;
		if (getAdapter() == null) return true;
		if (getAdapter().getItemCount() == 0) {
			// 没有item的时候也可以下拉刷新
			return true;
		}
		if (x > y * 0.5) {
			return false;
		}
		if (((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition() == 0 && getChildAt(0).getTop() >= 0) {
			// 滑到ListView的顶部了
			return true;
		}
		return false;
	}

	@Override
	public boolean canPullUp(float x, float y) {
//		if (recyclerView == null) return true;
		if (getAdapter() == null) return true;
		if (getAdapter().getItemCount() == 0) {
			// 没有item的时候也可以上拉加载
			return true;
		}
		int lastPos = ((LinearLayoutManager)getLayoutManager()).findLastVisibleItemPosition();
		if (x > y * 0.5) {
			return false;
		}
		if (lastPos == getAdapter().getItemCount() - 1) {
			if (computeVerticalScrollExtent() + computeVerticalScrollOffset() >= computeVerticalScrollRange())
				return true;
		}
		//拒绝上拉
		return false;
	}
}
