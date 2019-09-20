//package com.sziti.locationkit2.treeRecycleView;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import java.util.List;
//
//public class SelectImageAdatper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    //最多选择多少张
//    private int MAX_SELECT = 5;
//    private static final int TYPE_ADD = 0;
//    private static final int TYPE_PICTURE = 1;
//    private List<String> list;
//    private SelectListener selectListener = null;
//
//    public void setDeleteClickListener(SelectListener deleteClickListener) {
//        this.selectListener = deleteClickListener;
//    }
//
//    public SelectImageAdatper(int max_select, List<String> list) {
//        MAX_SELECT = max_select;
//        this.list = list;
//    }
//
//    public void refresh(List<String> imgList) {
////        List<String> temp = new ArrayList();
////        //需要特殊处理一下 方便放大预览
//        for (int i = 0; i < imgList.size(); i++) {
//            if (imgList.get(i).contains("file:///")) continue;
//            String s = "file:///" + imgList.get(i);
//            imgList.set(i, s);
//        }
//        list = imgList;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_image, parent, false);
//        return new SelectHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
//        switch (getItemViewType(position)) {
//            case TYPE_ADD:
//                ((SelectHolder) holder).deleteButton.setVisibility(View.INVISIBLE);
//                ((SelectHolder) holder).imageView.setImageResource(R.mipmap.upload_image_icon);
//                ((SelectHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (selectListener != null)
//                            selectListener.onAdd();
//                    }
//                });
//                break;
//            case TYPE_PICTURE:
//                Glide.with(holder.itemView.getContext())
//                        .load(list.get(position))
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .thumbnail(1f)
//                        .into(((SelectHolder) holder).imageView);
//                ((SelectHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ImageTrans.with(holder.itemView.getContext())
//                                .setImageList(list)
//                                .setSourceImageView(new SourceImageViewGet() {
//                                    @Override
//                                    public ImageView getImageView(int pos) {
//                                        if (((SelectHolder) holder).imageView != null)
//                                            return ((SelectHolder) holder).imageView;
//                                        return null;
//                                    }
//                                })
//                                .setScaleType(ImageView.ScaleType.CENTER_CROP)
//                                .setImageLoad(new MyImageLoad())
//                                .setNowIndex(position)
//                                .show();
//                    }
//                });
//                ((SelectHolder) holder).deleteButton.setVisibility(View.VISIBLE);
//                ((SelectHolder) holder).deleteButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //notifyItemRemoved只是执行动画 并没有实际删除原始数据  onBindViewHolder方法也并没有被调用
//                        if (selectListener != null)
//                            selectListener.onDelete(holder.getLayoutPosition());
//                    }
//                });
//                break;
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        if (list.size() < MAX_SELECT) {
//            return list.size() + 1;// 注意这里,这里会做判断,若图片集合大小小于最多显示几张那么就让count+1
//        } else {
//            return list.size();
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (position + 1 == getItemCount() && list.size() < MAX_SELECT) {
//            return TYPE_ADD;
//        } else {
//            return TYPE_PICTURE;
//        }
//    }
//
//    class SelectHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//        Button deleteButton;
//
//        public SelectHolder(View itemView) {
//            super(itemView);
//            imageView = (ImageView) itemView.findViewById(R.id.item_select_imageView);
//            deleteButton = (Button) itemView.findViewById(R.id.item_select_delete);
//        }
//    }
//
//    public interface SelectListener {
//        //点选添加按钮
//        void onAdd();
//
//        //点击图片右上角删除按钮
//        void onDelete(int position);
//    }
//}
