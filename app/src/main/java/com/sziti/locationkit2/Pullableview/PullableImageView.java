package com.sziti.locationkit2.Pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class PullableImageView extends ImageView implements Pullable
{

	public PullableImageView(Context context)
	{
		super(context);
	}

	public PullableImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown(float x,float y)
	{
		return true;
	}

	@Override
	public boolean canPullUp(float x, float y) {
		return true;
	}
}
