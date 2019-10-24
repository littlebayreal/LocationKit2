package com.sziti.locationkit2.util;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadKit {
	public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
		List<MultipartBody.Part> parts = new ArrayList<>(files.size());
		for (int i = 0; i < files.size(); i++) {
			RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), files.get(i));
			MultipartBody.Part part = MultipartBody.Part.createFormData("file1", files.get(i).getName(), requestBody);
			parts.add(part);
		}
		return parts;
	}
    public static MultipartBody filesToMultipartBody(List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
            builder.addFormDataPart("file1", file.getName(), requestBody);
        }
        builder.setType(MultipartBody.FORM);
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }

    public static Map<String, RequestBody> picToFiles(List<File> files) {
        Map<String, RequestBody> images = new HashMap<String, RequestBody>();
        for (File file : files) {
            Log.i("qwer", "图片上传:" + file.getAbsolutePath());
//            RequestBody  requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
            //一定要加("AttachmentKey\"; filename=\"" +，不然失败
            images.put("AttachmentKey\"; filename=\"" + file.getName(), requestBody);
//            images.put("files\";filename=\""+file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));
        }
        return images;
    }
    public static HashMap<String, RequestBody> picToFile(File file) {
        HashMap<String, RequestBody> image = new HashMap<String, RequestBody>();
//        for (File file : files) {
        Log.i("qwer", "图片上传:" + file.getAbsolutePath());
//            RequestBody  requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
            //一定要加("AttachmentKey\"; filename=\"" +，不然失败
        image.put("AttachmentKey\"; filename=\"" + file.getName(), requestBody);
//            images.put("files\";filename=\""+file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));
//        }
        return image;
    }
}
