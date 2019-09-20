package com.sziti.locationkit2.treeRecycleView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class GridItemDecoration extends RecyclerView.ItemDecoration {
	private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
	private Drawable mDivider;
	//判断是否有头布局
	private boolean hasHeader;

	public GridItemDecoration(Context context) {
		final TypedArray a = context.obtainStyledAttributes(ATTRS);
		mDivider = a.getDrawable(0);
		a.recycle();
	}

	public GridItemDecoration(Context context, boolean header) {
		this(context);
		hasHeader = header;
	}

	//设置item的内边距 来空出线的空间
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		int position = parent.getChildAdapterPosition(view);
		int pos = position;

		outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
		if (hasHeader) {
			if (position == 0) {
				//画头布局的底部边线
				outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
				return;
			} else {
				pos = position - 1;
			}
		}
//		int type = parent.getAdapter().getItemViewType(pos);
//        switch (type) {
//            case HomeAdapter.TYPE_SUB_CLASS:
//                break;
//            case HomeAdapter.TYPE_SUB_CONTENT:
		int right = mDivider.getIntrinsicWidth();
		int bottom = mDivider.getIntrinsicHeight();
		outRect.set(0, 0, right, bottom);
//                break;
//	}
}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		int count = parent.getChildCount();
		//绘制水平方向
		for (int i = 0; i < count; i++) {
			//获取每个子布局
			View mChildView = parent.getChildAt(i);
			RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) mChildView
					.getLayoutParams();
			int left = mChildView.getLeft() - mLayoutParams.leftMargin;
			int right = mChildView.getRight() + mLayoutParams.rightMargin;
			int top = mChildView.getBottom() + mLayoutParams.bottomMargin;
			int bottom = top + mDivider.getIntrinsicHeight();
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}

		//绘制垂直方向
		for (int i = 0; i < count; i++) {
			//获取每个子布局
			View mChildView = parent.getChildAt(i);
			RecyclerView.LayoutParams mLayoutParams = (RecyclerView.LayoutParams) mChildView
					.getLayoutParams();
			int top = mChildView.getTop() - mLayoutParams.topMargin;
			int bottom = mChildView.getBottom() + mLayoutParams.bottomMargin;
			int left = mChildView.getRight() + mLayoutParams.rightMargin;
			int right = left + mDivider.getIntrinsicWidth();
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	/**
	 * 获取列数
	 * 如果是GridView，就获取列数，如果是ListView，列数就是1
	 *
	 * @param parent
	 * @return
	 */
	private int getSpanCount(RecyclerView parent) {
		RecyclerView.LayoutManager mLayoutManager = parent.getLayoutManager();
		if (mLayoutManager instanceof GridLayoutManager) {
			GridLayoutManager mGridLayoutManager = (GridLayoutManager) mLayoutManager;
			//获取列数
			return mGridLayoutManager.getSpanCount();
		}
		return 1;
	}
}
