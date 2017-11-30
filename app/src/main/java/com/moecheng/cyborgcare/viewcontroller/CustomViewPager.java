package com.moecheng.cyborgcare.viewcontroller;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by wangchengcheng on 2017/11/20.
 */

public class CustomViewPager extends ViewPager {


    private int lastAction = -1;

    private boolean isScrollable = true;
    private boolean isCancellable = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCancellable && isScrollable) {
            if (ev.getActionMasked() == MotionEvent.ACTION_UP && lastAction == MotionEvent.ACTION_MOVE) {
                ev.setAction(MotionEvent.ACTION_CANCEL);
            }
            lastAction = ev.getActionMasked();
        } else if (!isScrollable && ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.onTouchEvent(ev);
    }

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		return super.dispatchTouchEvent(ev);
//	}

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isScrollable && super.onInterceptTouchEvent(event);
    }

    /**
     * 设置能否手动滑动
     * @param isScrollable 标识
     */
    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }

    /**
     * 设置是否滑动回弹
     * @param isCancellable 标识
     */
    public void setCancellable(boolean isCancellable) {
        this.isCancellable = isCancellable;
    }

    /**
     * 去掉ScrollView、GrdiView、ListView、ViewPager等滑动到边缘的光晕效果
     * https://zmywly8866.github.io/2014/12/16/android-remove-scroll-edge-halo.html
     */
    public void disableOverScroll() {
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public boolean isScrollable() {
        return isScrollable;
    }

    public boolean isCancellable() {
        return isCancellable;
    }
}
