package com.xing.sd.dialog;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.xing.sd.R;


/**
 * PopWindow的基类
 */
public abstract class BasePopup {
    private Context mContext;
    private PopupWindow mPopupWindow;
    private OnStateChangeListener mOnStateChangeListener;
    private LayoutInflater mInflate;
    private View mPopupContentView;

    public BasePopup(Context context) {
        init(context, -1, -1, R.style.popupScaleAnimation);
    }

    public BasePopup(Context context, int w, int h) {
        init(context, w, h, R.style.popupScaleAnimation);
    }

    public BasePopup(Context context, int w, int h, int anim) {
        init(context, w, h, anim);
    }

    private void init(Context context, int w, int h, int anim) {
        this.mContext = context;
        mInflate = LayoutInflater.from(context);
        mPopupContentView = mInflate.inflate(initLayoutId(), null);
        if (w == 0) {
            w = LayoutParams.WRAP_CONTENT;
        } else if (w == -1) {
            w = LayoutParams.MATCH_PARENT;
        }
        if (h == 0) {
            h = LayoutParams.WRAP_CONTENT;
        } else if (h == -1) {
            h = LayoutParams.MATCH_PARENT;
        }
        mPopupWindow = new PopupWindow(mPopupContentView, w, h);
        mPopupWindow.setAnimationStyle(anim);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xffA8A1A1));
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                popupShow(false);
                if (mOnStateChangeListener != null) {
                    mOnStateChangeListener.onStateChanged(mPopupWindow, false);
                }
            }
        });
    }

    public Context getContext() {
        return mContext;
    }

    public LayoutInflater getLayoutInflater() {
        return mInflate;
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public View getPopupContentView() {
        return mPopupContentView;
    }

    public OnStateChangeListener getOnStateChangeListener() {
        return mOnStateChangeListener;
    }

    public void setOnStateChangeListener(
            OnStateChangeListener mOnStateChangeListener) {
        this.mOnStateChangeListener = mOnStateChangeListener;
    }

    /**
     * 初始化布局,子类需要实现该方法，提供popwindow的布局内容。
     *
     * @return 布局文件id
     */
    public abstract int initLayoutId();

    public void showAsDropDown(View v) {
        if(mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        } else {
            mPopupWindow.showAsDropDown(v);
        }
        if (mOnStateChangeListener != null)
            mOnStateChangeListener.onStateChanged(mPopupWindow, true);
    }

    public void showAsDropDown(View v, int xoff, int yoff) {
        mPopupWindow.showAsDropDown(v, xoff, yoff);
        if (mOnStateChangeListener != null)
            mOnStateChangeListener.onStateChanged(mPopupWindow, true);
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        mPopupWindow.showAtLocation(parent, gravity, x, y);
        if (mOnStateChangeListener != null)
            mOnStateChangeListener.onStateChanged(mPopupWindow, true);
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    /**
     * 需要监听 popwindow的展示完成和消失完成的状态，请设置此接口
     */
    public interface OnStateChangeListener {
        /**
         * 当popwindow 的状态改变时被调用，
         *
         * @param popwindow 当前监听的popwindow对象
         * @param isShow    当前状态是否为显示，如果不是显示便是执行了隐藏操作。true表示显示，false表示隐藏。
         */
        void onStateChanged(PopupWindow popwindow, boolean isShow);
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    protected void popupShow(boolean isShow) {

    }
}
