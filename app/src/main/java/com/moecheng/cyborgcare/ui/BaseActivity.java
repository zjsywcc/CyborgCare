package com.moecheng.cyborgcare.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.moecheng.cyborgcare.R;
import com.moecheng.cyborgcare.statusbar.StatusBarManager;
import com.moecheng.cyborgcare.util.Compat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wangchengcheng on 2017/11/21.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Bind(R.id.base_content)
    FrameLayout baseContent;
    @Bind(R.id.tool_bar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_layout);
        ButterKnife.bind(this);
        initControlViews();
        initViews();
    }

    /**
     * 初始化控件
     */
    private void initControlViews() {
        baseContent.addView(getContentView());
    }

    /**
     * 设置标题栏中间标题
     *
     * @param resourceId
     */
    public void setToolbarTitleTv(int resourceId) {
        toolbarTitleTv.setText(Compat.getString(this, resourceId));
    }

    /**
     * 设置标题栏背景颜色
     *
     * @param color
     */
    protected void setTitleBgColor(int color) {
        toolbar.setBackgroundColor(Compat.getColor(this, color));
        //状态栏背景相关配置
        new StatusBarManager.builder(this)
                .setStatusBarColor(color)
                .create();
    }

    /**
     * Navigation Button点击回调，默认回退销毁页面，其他操作子类可重写
     *
     * @param view
     */
    protected void callbackOnClickNavigationAction(View view) {
        onBackPressed();
    }


    /**
     * 获取布局View
     *
     * @return
     */
    protected abstract View getContentView();

    /**
     * 控件初始化操作
     *
     * @return
     */
    public abstract void initViews();

}
