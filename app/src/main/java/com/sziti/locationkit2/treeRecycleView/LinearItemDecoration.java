package com.sziti.locationkit2.treeRecycleView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * LinearLayout分割线
 */
public class LinearItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;
    public LinearItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
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
    }
}
