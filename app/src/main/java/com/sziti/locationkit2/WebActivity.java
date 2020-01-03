package com.sziti.locationkit2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.util.ArrayList;
import java.util.List;

public class WebActivity extends AppCompatActivity {
	private WebView webView;
	private ValueCallback<Uri> uploadMessage;
	private ValueCallback<Uri[]> uploadMessageAboveL;
	private final static int FILE_CHOOSER_RESULT_CODE = 10000;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);

		webView = (WebView) findViewById(R.id.webView);

		WebSettings webSettings = webView.getSettings();

		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setAllowFileAccessFromFileURLs(true);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(false);

//		webView.addJavascriptInterface(new JsInterface(), "control");

//		webView.loadUrl("http://10.111.12.143:8001/freeshoot/freeshoot.html");
		webView.loadUrl(GlobalObjects.Companion.getBase_url()+"freeshoot/freeshoot.html");
		WebViewClient client = new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		};
		webView.setWebViewClient(client);
		//选择图片框需要我自行处理
		webView.setWebChromeClient(new WebChromeClient(){
									   // Andorid 4.1----4.4
									   public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
										   uploadMessage = uploadFile;
										   openImageChooserActivity();
									   }

									   // for 5.0+
									   @Override
									   public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
										   uploadMessageAboveL = filePathCallback;
										   openImageChooserActivity();
										   return true;
									   }
		});
	}
    private long firstTime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键
			webView.goBack();   //后退
			//webview.goForward();//前进
			return true;    //已处理
		}else{
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(WebActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				firstTime = secondTime;
				return true;
			} else{
				finish();
			}
		}
		return false;
	}

	private void openImageChooserActivity() {
//		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//		i.addCategory(Intent.CATEGORY_OPENABLE);
//		i.setType("image/*");
//		startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
		//设置本次能选择多少张
		ImagePicker.getInstance().setSelectLimit(1);
		Intent intent = new Intent(this, ImageGridActivity.class);
		startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FILE_CHOOSER_RESULT_CODE) {
			if (null == uploadMessage && null == uploadMessageAboveL) return;
			ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
			Uri result = data == null || resultCode != RESULT_OK ? null : getImageContentUri(this,images.get(0).path);
			if (uploadMessageAboveL != null) {
				onActivityResultAboveL(requestCode, resultCode, data);
			} else if (uploadMessage != null) {
				uploadMessage.onReceiveValue(result);
				uploadMessage = null;
			}
		}
//		if (requestCode == FILE_CHOOSER_RESULT_CODE) {
//			if (null == uploadMessage && null == uploadMessageAboveL) return;
//			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
//			if (uploadMessageAboveL != null) {
//				onActivityResultAboveL(requestCode, resultCode, data);
//			} else if (uploadMessage != null) {
//				uploadMessage.onReceiveValue(result);
//				uploadMessage = null;
//			}
//		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
		if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
			return;
		Uri[] results = null;
		if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
			ArrayList<ImageItem> images = (ArrayList<ImageItem>) intent.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
			results = new Uri[]{getImageContentUri(this,images.get(0).path)};
//			if (intent != null) {
//				String dataString = intent.getDataString();
//				ClipData clipData = intent.getClipData();
//				if (clipData != null) {
//					results = new Uri[clipData.getItemCount()];
//					for (int i = 0; i < clipData.getItemCount(); i++) {
//						ClipData.Item item = clipData.getItemAt(i);
//						results[i] = item.getUri();
//					}
//				}
//				if (dataString != null)
//					results = new Uri[]{Uri.parse(dataString)};
//			}
		}
		uploadMessageAboveL.onReceiveValue(results);
		uploadMessageAboveL = null;
	}
	public static Uri getImageContentUri(Context context, String filePath) {//File imageFile
		//String filePath = imageFile.getAbsolutePath();//根据文件来获取路径
		Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
			new String[] { filePath }, null);
		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/images/media");
			return Uri.withAppendedPath(baseUri, "" + id);
		} else {
			if (!TextUtils.isEmpty(filePath)) {//imageFile.exists()判断文件存不存在
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DATA, filePath);
				return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				return null;
			}
		}
	}
}
