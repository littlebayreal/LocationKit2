package it.liuting.imagetrans;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import it.liuting.imagetrans.listener.ProgressViewGet;
import it.liuting.imagetrans.listener.SourceImageViewGet;

/**
 * Created by liuting on 18/3/14.
 * 配置文件
 */

class ImageTransBuild {
    protected int clickIndex;
    protected int nowIndex;
    protected List<String> imageList;
    //imageview的回调
    protected SourceImageViewGet sourceImageViewGet;
    //加载视图回调
    protected ProgressViewGet progressViewGet;
    protected ITConfig itConfig;
    //图片展示适配器的接口
    protected ImageTransAdapter imageTransAdapter;
    //图片加载类
    protected ImageLoad imageLoad;
    //图片缩放模式
    protected ScaleType scaleType = ScaleType.CENTER_CROP;
    //点击弹出的dialog
    protected Dialog dialog;

    //检查参数是否齐全
    void checkParam() {
        if (itConfig == null)
            itConfig = new ITConfig();
        if (imageTransAdapter == null) {
            imageTransAdapter = new ImageTransAdapter() {
                @Override
                protected View onCreateView(View parent, ViewPager viewPager, DialogInterface dialogInterface) {
                    return null;
                }
            };
        }
        if (sourceImageViewGet == null)
            throw new NullPointerException("not set SourceImageViewGet");
        if (imageLoad == null)
            throw new NullPointerException("not set ImageLoad");
        if (imageList == null)
            throw new NullPointerException("not set ImageList");
    }

    //是否需要
    boolean needTransOpen(int pos, boolean change) {
        boolean need = pos == clickIndex;
        if (need && change) {
            clickIndex = -1;
        }
        return need;
    }

    //动态加载progressbar
    View inflateProgress(Context context, FrameLayout rootView) {
        if (progressViewGet != null) {
            View progress = progressViewGet.getProgress(context);
            if (progress == null) return null;
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (progress.getLayoutParams() != null) {
                width = progress.getLayoutParams().width;
                height = progress.getLayoutParams().height;
            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
            lp.gravity = Gravity.CENTER;
            rootView.addView(progress, lp);
            return progress;
        }
        return null;
    }
}
