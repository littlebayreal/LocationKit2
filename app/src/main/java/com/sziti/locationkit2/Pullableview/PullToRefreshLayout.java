package com.sziti.locationkit2.Pullableview;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sziti.locationkit2.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义的布局，用来管理三个子控件，其中一个是下拉头，一个是包含内容的pullableView（可以是实现Pullable接口的的任何View），
 * 还有一个上拉头，更多详解见博客http://blog.csdn.net/zhongkejingwang/article/details/38868463
 *
 * @author 陈靖
 */
public class PullToRefreshLayout extends RelativeLayout {
    public static final String TAG = "PullToRefreshLayout";
    // 初始状态
    public static final int INIT = 0;
    // 释放刷新
    public static final int RELEASE_TO_REFRESH = 1;
    // 正在刷新
    public static final int REFRESHING = 2;
    //刷新完成，显示提示信息
    public static final int SHOWTOAST = 3;
    // 释放加载
    public static final int RELEASE_TO_LOAD = 4;
    // 正在加载
    public static final int LOADING = 5;
    // 操作完毕
    public static final int DONE = 6;
    // 当前状态
    private int state = INIT;
    // 刷新回调接口
    private OnRefreshListener mListener;
    // 刷新成功
    public static final int SUCCEED = 0;
    // 刷新失败
    public static final int FAIL = 1;
    // 按下Y坐标，上一个事件点Y坐标
    private float downY, lastY;

    // 下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
    public float pullDownY = 0;
    // 上拉的距离
    private float pullUpY = 0;

    // 释放刷新的距离
    private float refreshDist = 200;
    // 释放加载的距离
    private float loadmoreDist = 200;
    //消息提示框的高度
    private float showToastDist = 0;
    private MyTimer timer;
    // 回滚速度
    public float MOVE_SPEED = 8;
    // 第一次执行布局
    private boolean isLayout = false;
    // 在刷新过程中滑动操作
    private boolean isTouch = false;
    // 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
    private float radio = 2;

    // 下拉箭头的转180°动画
    private RotateAnimation rotateAnimation;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;
    private RelativeLayout refreshLayout;

    // 下拉头
    private View refreshView;
    // 下拉的箭头
    private View pullView;
    // 正在刷新的图标
    private View refreshingView;
    // 刷新结果图标
//    private View refreshStateImageView;
    // 刷新结果：成功或失败
    private TextView refreshStateTextView;
    private View showToastView;
    private TextView showToast;
    // 上拉头
    private View loadmoreView;
    // 上拉的箭头
    private View pullUpView;
    // 正在加载的图标
    private View loadingView;
    // 加载结果图标
    private View loadStateImageView;
    // 加载结果：成功或失败
    private TextView loadStateTextView;

    // 实现了Pullable接口的View
    private View pullableView;
    // 过滤多点触碰
    private int mEvents;
    // 这两个变量用来控制pull的方向，如果不加控制，当情况满足可上拉又可下拉时没法下拉
    private boolean canPullDown = true;
    private boolean canPullUp = true;

    private Context mContext;

    private int direction = 0;
    /**
     * 执行自动回滚的handler
     */
    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // 回弹速度随下拉距离moveDeltaY增大而增大
            MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2
                    / getMeasuredHeight() * (pullDownY + Math.abs(pullUpY))));
            switch (msg.what) {
                case 0:
                    if (!isTouch) {
                        // 正在刷新，且没有往上推的话则悬停，显示"正在刷新..."
                        if (state == REFRESHING && pullDownY <= refreshDist) {
                            pullDownY = refreshDist;
                            timer.cancel();
                        } else if (state == LOADING && -pullUpY <= loadmoreDist) {
                            pullUpY = -loadmoreDist;
                            timer.cancel();
                        }

                    }
                    if (pullDownY > showToastDist && (state == SHOWTOAST || state == REFRESHING))
                        pullDownY -= MOVE_SPEED;
                    else if (pullDownY > 0 && state == INIT)
                        pullDownY -= MOVE_SPEED;
                    else if (pullUpY < 0) {
                        Log.e("ccc", "pullupy:" + pullUpY);
                        pullUpY += MOVE_SPEED;
                    }
                    //以下都是打断重绘的判断
                    //没有执行刷新操作  直接还原
                    if (pullDownY <= 0 && state == INIT && pullUpY > 0) {
                        pullView.clearAnimation();
                        if (state != REFRESHING && state != LOADING)
                            changeState(INIT);
                        timer.cancel();
                        requestLayout();
                    }
                    //执行好刷新操作 回弹到showtoast
                    if (pullDownY <= showToastDist && state == SHOWTOAST) {
                        // 已完成第一阶段回弹
                        pullView.clearAnimation();
                        timer.cancel();

                        pullDownY = showToastDist;
                        updateHandler.sendEmptyMessageDelayed(1, 3000);
                    }
                    if (pullUpY > 0) {
                        // 已完成回弹
                        pullUpY = 0;
                        pullUpView.clearAnimation();
                        // 隐藏上拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
                        if (state != REFRESHING && state != LOADING)
                            changeState(INIT);
                        timer.cancel();
                        requestLayout();
                    }
                    // 刷新布局,会自动调用onLayout
                    requestLayout();
                    break;
                case 1:
                    if (pullDownY > 0) {
                        pullDownY -= MOVE_SPEED;
                        requestLayout();
                        updateHandler.sendEmptyMessageDelayed(1, 5);
                    } else {
                        //移除showtoast的动画
                        updateHandler.removeMessages(1);
                        pullDownY = 0;
                        requestLayout();
                        changeState(INIT);
                    }
                    break;
                case 2:
                    if (pullDownY > 0) {
                        showToastView.setVisibility(GONE);
                    }
                    break;
                case 3:
                    timer.cancel();
                    removeMessages(1);
                    removeMessages(2);
                    pullDownY = 0;
                    pullUpY = 0;
                    requestLayout();
                    changeState(INIT);
                    break;
            }
        }
    };

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public PullToRefreshLayout(Context context) {
        super(context);
        initView(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        //执行延时刷新界面的任务
        timer = new MyTimer(updateHandler);
        //箭头加载动画类
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.reverse_anim);
        //菊花旋转动画类
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.rotating);
        // 添加匀速转动动画
        LinearInterpolator lir = new LinearInterpolator();
        rotateAnimation.setInterpolator(lir);
        refreshingAnimation.setInterpolator(lir);
    }

    private void hide() {
        timer.schedule(5);
    }

    /**
     * 完成刷新操作，显示刷新结果。注意：刷新完成后一定要调用这个方法
     */
    /**
     * @param refreshResult PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
     */
    public void refreshFinish(int refreshResult, String showText) {
        refreshingView.clearAnimation();
        refreshingView.setVisibility(View.GONE);
        switch (refreshResult) {
            case SUCCEED:
                // 刷新成功
//                refreshStateImageView.setVisibility(View.VISIBLE);
                refreshStateTextView.setText(R.string.refresh_succeed);
                showToast.setText(showText);
//                refreshStateImageView
//                        .setBackgroundResource(R.mipmap.refresh_succeed);
                break;
            case FAIL:
            default:
                // 刷新失败
//                refreshStateImageView.setVisibility(View.VISIBLE);
                refreshStateTextView.setText(R.string.refresh_fail);
                showToast.setText(showText);
//                refreshStateImageView
//                        .setBackgroundResource(R.mipmap.refresh_failed);
                break;
        }
        if (pullDownY > 0) {
            //显示Toast 并且将滚动缩小到toast高度
            changeState(SHOWTOAST);
            //将滑动距离固定到展示框高度
            hide();
        } else {
            changeState(SHOWTOAST);
            hide();
        }
    }

    /**
     * 加载完毕，显示加载结果。注意：加载完成后一定要调用这个方法
     *
     * @param refreshResult PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
     */
    public void loadmoreFinish(int refreshResult) {
        loadingView.clearAnimation();
        loadingView.setVisibility(View.GONE);
        switch (refreshResult) {
            case SUCCEED:
                // 加载成功
                loadStateImageView.setVisibility(View.VISIBLE);
                loadStateTextView.setText(R.string.load_succeed);
                loadStateImageView.setBackgroundResource(R.mipmap.refresh_succeed);
                break;
            case FAIL:
            default:
                // 加载失败
                loadStateImageView.setVisibility(View.VISIBLE);
                loadStateTextView.setText(R.string.load_fail);
                loadStateImageView.setBackgroundResource(R.mipmap.refresh_failed);
                break;
        }
        if (pullUpY < 0) {
            // 刷新结果停留1秒
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    changeState(DONE);
                    hide();
                }
            }.sendEmptyMessageDelayed(0, 1000);
        } else {
            changeState(DONE);
            hide();
        }
    }

    private void changeState(int to) {
        state = to;
        switch (state) {
            case INIT:
                showToastView.setVisibility(GONE);
                refreshLayout.setVisibility(VISIBLE);
                // 下拉布局初始状态
//                refreshStateImageView.setVisibility(View.GONE);
                refreshStateTextView.setText(R.string.pull_to_refresh);
                pullView.clearAnimation();
                pullView.setVisibility(View.VISIBLE);
                // 上拉布局初始状态
                loadStateImageView.setVisibility(View.GONE);
                loadStateTextView.setText(R.string.pullup_to_load);
                pullUpView.clearAnimation();
                pullUpView.setVisibility(View.VISIBLE);
                break;
            case RELEASE_TO_REFRESH:
                // 释放刷新状态
                refreshStateTextView.setText(R.string.release_to_refresh);
                pullView.startAnimation(rotateAnimation);
                break;
            case REFRESHING:
                // 正在刷新状态
                pullView.clearAnimation();
                refreshingView.setVisibility(View.VISIBLE);
                pullView.setVisibility(View.INVISIBLE);
                refreshingView.startAnimation(refreshingAnimation);
                refreshStateTextView.setText(R.string.refreshing);
                break;
            case SHOWTOAST:
//                refreshLayout.setVisibility(GONE);
                showToastView.setVisibility(VISIBLE);
                break;
            case RELEASE_TO_LOAD:
                // 释放加载状态
                loadStateTextView.setText(R.string.release_to_load);
                pullUpView.startAnimation(rotateAnimation);
                break;
            case LOADING:
                // 正在加载状态
                pullUpView.clearAnimation();
                loadingView.setVisibility(View.VISIBLE);
                pullUpView.setVisibility(View.INVISIBLE);
                loadingView.startAnimation(refreshingAnimation);
                loadStateTextView.setText(R.string.loading);
                break;
            case DONE:
                // 刷新或加载完毕，啥都不做
                break;
        }
    }

    /**
     * 不限制上拉或下拉
     */
    private void releasePull() {
        canPullDown = true;
        canPullUp = true;
    }

    /*
     * （非 Javadoc）由父控件决定是否分发事件，防止事件冲突
     *
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    private float lastDownX, lastDownY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastDownX = ev.getX();
                lastDownY = ev.getY();

                downY = ev.getY();
                lastY = downY;
                timer.cancel();
                mEvents = 0;
                releasePull();
                //下拉时候 如果正在显示提示栏
                if (pullDownY > 0 && state == SHOWTOAST) {
                    //停止提示栏滚动动画
                    updateHandler.removeMessages(1);
                    updateHandler.removeMessages(2);
                    //直接隐藏showtoast
                    updateHandler.sendEmptyMessageDelayed(2, 3000);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                // 过滤多点触碰
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) Math.abs(ev.getX() - lastDownX);
                int dy = (int) Math.abs(ev.getY() - lastDownY);
//                Log.e("vvv", "canpulldown:" + (((Pullable) pullableView).canPullDown()));
//                Log.e("xxx", "canpullup:" + (((Pullable) pullableView).canPullUp()));
                //当判断为下拉动作 并且在显示提示栏的情况下就立刻还原初始状态
                if (ev.getY() - downY < 0 && state == SHOWTOAST) {
                    updateHandler.sendEmptyMessage(3);
                }
                //当非多点触控时处理
                if (mEvents == 0) {
                    //内容控件可以下拉，状态不为加载中
                    if (pullDownY > 0
                            || (((Pullable) pullableView).canPullDown(dx,dy)
                            && canPullDown && state != LOADING)) {
                        // 可以下拉，正在加载时不能下拉
                        // 对实际滑动距离做缩小，造成用力拉的感觉、
                        //得到下拉的距离
                        pullDownY = pullDownY + (ev.getY() - lastY) / radio;
                        if (pullDownY < 0) {
                            pullDownY = 0;
                            canPullDown = false;
                            canPullUp = true;
                        }
                        //下拉的距离超过控件的高度
                        if (pullDownY > getMeasuredHeight())
                            pullDownY = getMeasuredHeight();

                        if (state == REFRESHING) {
                            // 正在刷新的时候触摸移动
                            isTouch = true;
                        }
                    } else if (pullUpY < 0 //上拉的判断
                            || (((Pullable) pullableView).canPullUp(dx,dy) && canPullUp && state != REFRESHING)) {
                        //上拉时如果是展示窗口状态  那么直接将窗口状态关闭  整体上移动

                        // 可以上拉，正在刷新时不能上拉
                        pullUpY = pullUpY + (ev.getY() - lastY) / radio;
                        if (pullUpY > 0) {
                            pullUpY = 0;
                            canPullDown = true;
                            canPullUp = false;
                        }
                        if (pullUpY < -getMeasuredHeight())
                            pullUpY = -getMeasuredHeight();
                        if (state == LOADING) {
                            // 正在加载的时候触摸移动
                            isTouch = true;
                        }
                    } else
                        releasePull();
                } else
                    mEvents = 0;


                lastY = ev.getY();
                // 根据下拉距离改变比例
                radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
                        * (pullDownY + Math.abs(pullUpY))));
                //当下拉距离和上拉距离存在时  不断重绘整体布局
                if ((pullDownY > 0 && state != SHOWTOAST) || (pullUpY < 0 && state != SHOWTOAST)) {
                    requestLayout();
                }
                Log.e("hhh","pulldowny:"+ pullDownY);
                if (pullDownY > 0) {
                    if (pullDownY < refreshDist
                            && (state == RELEASE_TO_REFRESH || state == DONE)) {
                        //这是处理拉下去又推上来的情况
                        // 如果下拉距离没达到刷新的距离且当前状态是释放刷新或是完成刷新，改变状态为下拉刷新，即还原
                        changeState(INIT);
                    }
                    if (pullDownY >= refreshDist && (state == INIT || state == SHOWTOAST)) {
                        // 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
                        changeState(RELEASE_TO_REFRESH);
                    }
                } else if (pullUpY < 0) {
                    // 下面是判断上拉加载的，同上，注意pullUpY是负值
                    if (-pullUpY < loadmoreDist
                            && (state == RELEASE_TO_LOAD || state == DONE)) {
                        changeState(INIT);
                    }
                    // 上拉操作
                    if (-pullUpY >= loadmoreDist && state == INIT) {
                        changeState(RELEASE_TO_LOAD);
                    }

                }
                // 因为刷新和加载操作不能同时进行，所以pullDownY和pullUpY不会同时不为0，因此这里用(pullDownY +
                // Math.abs(pullUpY))就可以不对当前状态作区分了
                if ((pullDownY + Math.abs(pullUpY)) > 8) {
                    // 防止下拉过程中误触发长按事件和点击事件
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            case MotionEvent.ACTION_UP:
                lastDownX = 0;
                lastDownY = 0;
                if (pullDownY > refreshDist || -pullUpY > loadmoreDist)
                // 正在刷新时往下拉（正在加载时往上拉），释放后下拉头（上拉头）不隐藏
                {
                    isTouch = false;
                }
                if (state == RELEASE_TO_REFRESH) {
                    changeState(REFRESHING);
                    // 刷新操作
                    if (mListener != null)
                        mListener.onRefresh(this);
                } else if (state == RELEASE_TO_LOAD) {
                    changeState(LOADING);
                    // 加载操作
                    if (mListener != null)
                        mListener.onLoadMore(this);
                }
                //回弹效果
                hide();
            default:
                break;
        }
        // 事件分发交给父类
        super.dispatchTouchEvent(ev);
        //不分发  直接消费掉
        return true;
    }

    /**
     * @author chenjing 自动模拟手指滑动的task
     */
    private class AutoRefreshAndLoadTask extends
		AsyncTask<Integer, Float, String> {

        @Override
        protected String doInBackground(Integer... params) {
            while (pullDownY < 4 / 3 * refreshDist) {
                pullDownY += MOVE_SPEED;
                publishProgress(pullDownY);
                try {
                    Thread.sleep(params[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            changeState(REFRESHING);
            // 刷新操作
            if (mListener != null)
                mListener.onRefresh(PullToRefreshLayout.this);
            hide();
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            if (pullDownY > refreshDist)
                changeState(RELEASE_TO_REFRESH);
            requestLayout();
        }

    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        AutoRefreshAndLoadTask task = new AutoRefreshAndLoadTask();
        task.execute(20);
    }

    /**
     * 自动加载
     */
    public void autoLoad() {
        pullUpY = -loadmoreDist;
        requestLayout();
        changeState(LOADING);
        // 加载操作
        if (mListener != null)
            mListener.onLoadMore(this);
    }

    private void initView() {
        refreshLayout = refreshView.findViewById(R.id.refresh_layout);
        showToast = showToastView.findViewById(R.id.show_toast);
        // 初始化下拉布局
        pullView = refreshView.findViewById(R.id.pull_icon);
        refreshStateTextView = (TextView) refreshView
                .findViewById(R.id.state_tv);
        refreshingView = refreshView.findViewById(R.id.refreshing_icon);
//        refreshStateImageView = refreshView.findViewById(R.id.state_iv);
        // 初始化上拉布局
        pullUpView = loadmoreView.findViewById(R.id.pullup_icon);
        loadStateTextView = (TextView) loadmoreView
                .findViewById(R.id.loadstate_tv);
        loadingView = loadmoreView.findViewById(R.id.loading_icon);
        loadStateImageView = loadmoreView.findViewById(R.id.loadstate_iv);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("Test", "Test");
        if (!isLayout) {
            // 这里是第一次进来的时候做一些初始化
            refreshView = getChildAt(0);
            showToastView = getChildAt(1);
            pullableView = getChildAt(2);
            loadmoreView = getChildAt(3);
            isLayout = true;
            initView();
            //获取下拉刷新布局的高度
            refreshDist = ((ViewGroup) refreshView).getChildAt(0)
                    .getMeasuredHeight();
            //获取上拉加载的布局高度
            loadmoreDist = ((ViewGroup) loadmoreView).getChildAt(0)
                    .getMeasuredHeight();

            showToastDist = showToastView.getMeasuredHeight();
            showToastView.setVisibility(GONE);
        }
        //每次手指的滑动 引发布局的重绘
        // 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分
        Log.e("ooo", "pullDownY:" + pullDownY + "pullUpY:" + pullUpY);
        refreshView.layout(0,
                (int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
                refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
        //将showToastView放置在顶部
        showToastView.layout(0, 0, showToastView.getMeasuredWidth(), (int) (pullDownY + pullUpY));

        pullableView.layout(0, (int) (pullDownY + pullUpY),
                pullableView.getMeasuredWidth(), (int) (pullDownY + pullUpY)
                        + pullableView.getMeasuredHeight());
        loadmoreView.layout(0,
                (int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight(),
                loadmoreView.getMeasuredWidth(),
                (int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight()
                        + loadmoreView.getMeasuredHeight());
    }

    class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            public MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }

        }
    }

    /**
     * 刷新加载回调接口
     *
     * @author chenjing
     */
    public interface OnRefreshListener {
        /**
         * 刷新操作
         */
        void onRefresh(PullToRefreshLayout pullToRefreshLayout);

        /**
         * 加载操作
         */
        void onLoadMore(PullToRefreshLayout pullToRefreshLayout);
    }

}
