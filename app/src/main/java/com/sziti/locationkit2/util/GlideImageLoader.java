package com.sziti.locationkit2.util;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.lzy.imagepicker.loader.ImageLoader;
import com.sziti.locationkit2.R;

public class GlideImageLoader implements ImageLoader {
    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(width, height)
                .error(R.mipmap.default_image)
                .placeholder(R.mipmap.default_image)
                .thumbnail(1f)
                .into(imageView);
    }

	@Override
	public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
		Glide.with(activity)
				.load(path)
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				.override(width, height)
				.error(R.mipmap.default_image)
				.placeholder(R.mipmap.default_image)
				.thumbnail(1f)
				.into(imageView);
	}

	public static void displayImage(Activity activity, String path, ImageView imageView) {
        Glide.with(activity)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.default_image)
                .placeholder(R.mipmap.default_image)
                .thumbnail(1f)
                .into(imageView);
    }

    public static void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.default_image)
                .placeholder(R.mipmap.default_image)
                .thumbnail(1f)
                .into(imageView);
    }

    public static void displayImage(Context context, GlideUrl glideUrl, ImageView imageView) {
        Glide.with(context)
                .load(glideUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.default_image)
                .placeholder(R.mipmap.default_image)
                .thumbnail(1f)
                .into(imageView);
    }
    @Override
    public void clearMemoryCache() {

    }
}
