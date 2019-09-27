package com.sziti.locationkit2.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sziti.locationkit2.GlobalObjects;
import com.sziti.locationkit2.R;
import com.sziti.locationkit2.treeRecycleView.TreeRecyclerAdapter;

/**
 * Author: liuqiang
 * Time: 2018-01-02 13:28
 * Description:
 */
public class AlertDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private RecyclerView txt_msg;
    private IndexBar txt_index_bar;
    private TextView txt_index;
    private Button btn_neg;
    private Button btn_pos;
    private ImageView img_line;
    private Display display;
    private int orientation;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private OnIndexChangedListener onIndexChangedListener;
    public AlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
		orientation = context.getResources().getConfiguration().orientation;
    }

    public AlertDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_alertdialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg = (RecyclerView) view.findViewById(R.id.alert_dialog_recyclerView);
        txt_msg.setVisibility(View.GONE);
        txt_index_bar = view.findViewById(R.id.alert_dialog_indexBar);
        txt_index_bar.setVisibility(View.GONE);
        txt_index = view.findViewById(R.id.alert_dialog_index);
        txt_index.setVisibility(View.GONE);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        img_line = (ImageView) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小

        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams(this.orientation == Configuration.ORIENTATION_LANDSCAPE?(int) (display
                .getWidth() * 0.5):(int) (display
			.getWidth() * 0.8), this.orientation == Configuration.ORIENTATION_LANDSCAPE?(int) (display.getHeight() * 0.8):(int) (display.getHeight() * 0.5)));

		(view.findViewById(R.id.alert_dialog_relativeLayout))
			.setLayoutParams(new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, this.orientation == Configuration.ORIENTATION_LANDSCAPE?(int) (display.getHeight() * 0.65):(int) (display.getHeight() * 0.4)));

		txt_index_bar.setVisibility(View.VISIBLE);
		//初始化右侧字母导航栏
		txt_index_bar.setIndexs(GlobalObjects.Companion.getLETTERS());
		txt_index_bar.setSelectedIndexTextView(txt_index);
		//监听字母的滑动
		txt_index_bar.setOnIndexChangedListener(new IndexBar.OnIndexChangedListener() {
			@Override
			public void onIndexChanged(String letter) {
				if (onIndexChangedListener != null)
					onIndexChangedListener.onIndexChanged(letter);
			}

			@Override
			public void onIndexBarDown() {

			}

			@Override
			public void onIndexBarUp() {

			}
		});
        return this;
    }

    public AlertDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }
    //设置recyclerview的显示方式
	public AlertDialog setMsgLayoutManager(RecyclerView.LayoutManager layoutManager){
    	txt_msg.setLayoutManager(layoutManager);
    	return this;
	}
    //设置显示列表数据
    public AlertDialog setMsgAdapter(RecyclerView.Adapter adapter){
    	txt_msg.setVisibility(View.VISIBLE);
    	txt_msg.setAdapter(adapter);
    	adapter.notifyDataSetChanged();
    	return this;
	}
    public AlertDialog setMsg(String msg) {
        showMsg = true;
//        if ("".equals(msg)) {
//            txt_msg.setText("内容");
//        } else {
//            txt_msg.setText(msg);
//        }
        return this;
    }

    public AlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public AlertDialog setPositiveButton(String text,
                                         final View.OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null)
                    listener.onClick(v);
            }
        });
        return this;
    }

    public AlertDialog setNegativeButton(String text,
                                         final View.OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null)
                    listener.onClick(v);
            }
        });
        return this;
    }
    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText("确定");
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btn_pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
    }
    public RecyclerView getTxt_msg(){
    	return txt_msg;
	}
    public void show() {
        setLayout();
        dialog.show();
    }
    public void dismiss(){
        dialog.dismiss();
    }

    public boolean isShowing(){
        return dialog.isShowing();
    }

    public void setOnIndexChangedListener(OnIndexChangedListener on){
    	this.onIndexChangedListener = on;
	}
    public interface OnIndexChangedListener{
    	void onIndexChanged(String letter);
	}
}
