package com.sziti.locationkit2.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

public class ImageCacheUtil {
	private static final int RADIX = 10 + 26; // 10 digits + 26 letters
	private static final String HASH_ALGORITHM = "MD5";
	static DisplayMetrics mDisplayMetrics;
	public static ExecutorService cThreadPool;
	private static String IMAGE_CACHE_PATH;
	public static void deleteFiles(String path) {
		deleteFiles(new File(path));
	}

	public static int getScreenWidth(Context context) {
		if (mDisplayMetrics == null)
			mDisplayMetrics = context.getResources().getDisplayMetrics();
		return mDisplayMetrics.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		if (mDisplayMetrics == null)
			mDisplayMetrics = context.getResources().getDisplayMetrics();
		return mDisplayMetrics.heightPixels;
	}
	public static void deleteFiles(File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isFile()) {
			file.delete();
			return;
		}
		//文件夹递归删除
		File[] files = file.listFiles();
		if (null == files) {
			return;
		}
		for (File subFile : files) {
			deleteFiles(subFile);
		}
		file.delete();
	}

	public static String getImageCachePath() {
		return IMAGE_CACHE_PATH;
	}

	public static int getMaxSizeOfBitMap(String path) {
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, op);
		return Math.max(op.outWidth, op.outHeight);
	}

	private static byte[] getMD5(byte[] data) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
		}
		return hash;
	}

	public static String generate(String imageUri) {
		byte[] md5 = getMD5(imageUri.getBytes());
		BigInteger bi = new BigInteger(md5).abs();
		if (imageUri.endsWith(".gif") || imageUri.endsWith(".GIF")) {
			return bi.toString(RADIX) + ".itgif";
		}
		return bi.toString(RADIX) + ".it";
	}

	public static void clearCache(final Context context) {
		cThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				File file = new File(IMAGE_CACHE_PATH);
				File[] files = file.listFiles();
				for (File item : files) {
					item.delete();
				}
				Glide.get(context).clearDiskCache();
			}
		});

	}

	public static String getFromAssets(Context context, String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null)
				Result += line;
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getCachedPath(String url) {
		String key = generate(url);
		String destUrl = getImageCachePath() + "/" + key;
		return destUrl;
	}
}
