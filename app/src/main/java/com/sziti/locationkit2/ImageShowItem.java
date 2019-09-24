package com.sziti.locationkit2;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.sziti.locationkit2.treeRecycleView.base.ViewHolder;

//public class ImageShowItem extends TreeItem<MaintainDetailData.UploadedAttachmentData> {
//    @Override
//    protected int initLayoutId() {
//        return R.layout.item_image_show;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder viewHolder) {
//        ((ImageView)viewHolder.getView(R.id.item_image_show_img)).setImageResource(R.mipmap.ic_launcher);
//        //载入图片
//        LoginData loginData = (LoginData) FileKit.getObject(MyApplication.getInstance().getApplicationContext(), GlobCons.OBJ_USER);
//        String path = GlobCons.base_url + "api/FileDownload?attachmentID=" + getData().getAttachmentID();
//		Glide.displayImage(viewHolder.itemView.getContext(), new GlideUrl(path, new LazyHeaders.Builder()
//                        .addHeader("token", loginData.getToken())
//                        .addHeader("applicationID", GlobCons.ApplicationID)
//                        .addHeader("userID", loginData.getUserID()).build()),
//                (ImageView) viewHolder.getView(R.id.item_image_show_img));
//    }
//}
