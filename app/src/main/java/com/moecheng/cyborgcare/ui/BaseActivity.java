package com.moecheng.cyborgcare.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.moecheng.cyborgcare.R;
import com.moecheng.cyborgcare.bluetooth.DeviceConnector;
import com.moecheng.cyborgcare.bluetooth.format.Utils;
import com.moecheng.cyborgcare.profile.BluetoothControlActivity;
import com.moecheng.cyborgcare.statusbar.StatusBarManager;
import com.moecheng.cyborgcare.util.Compat;
import com.moecheng.cyborgcare.util.DialogBuilder;
import com.moecheng.cyborgcare.util.Log;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.moecheng.cyborgcare.profile.BluetoothActivity.MESSAGE_DEVICE_NAME;
import static com.moecheng.cyborgcare.profile.BluetoothActivity.MESSAGE_READ;
import static com.moecheng.cyborgcare.profile.BluetoothActivity.MESSAGE_STATE_CHANGE;
import static com.moecheng.cyborgcare.profile.BluetoothActivity.MESSAGE_TOAST;
import static com.moecheng.cyborgcare.profile.BluetoothActivity.MESSAGE_WRITE;

/**
 * Created by wangchengcheng on 2017/11/21.
 */

public abstract class BaseActivity extends AppCompatActivity {


    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    // Message types sent from the DeviceConnector Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    protected BluetoothAdapter btAdapter;


    private static final String SAVED_PENDING_REQUEST_ENABLE_BT = "PENDING_REQUEST_ENABLE_BT";

    boolean pendingRequestEnableBt = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            pendingRequestEnableBt = savedInstanceState.getBoolean(SAVED_PENDING_REQUEST_ENABLE_BT);
        }
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            final String no_bluetooth = getString(R.string.no_bt_support);
            new DialogBuilder(this).create()
                    .setTitle(R.string.tips)
                    .setContent(no_bluetooth)
                    .setPositive(R.string.ok)
                    .show();
            Utils.log(no_bluetooth);
        }
        inflateContent(getContentView());
        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (btAdapter == null) return;

        if (!btAdapter.isEnabled() && !pendingRequestEnableBt) {
            pendingRequestEnableBt = true;
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
    }
    // ==========================================================================


    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    // ==========================================================================


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_PENDING_REQUEST_ENABLE_BT, pendingRequestEnableBt);
    }

    boolean isAdapterReady() {
        return (btAdapter != null) && (btAdapter.isEnabled());
    }

    protected void inflateContent(View view) {
        setContentView(R.layout.base_activity_layout);
        /**
         * 由于在bind之前调用 因此需要使用findbyId
         * ref:https://stackoverflow.com/questions/40583239/androiduse-butterknife-in-activity-extending-from-another-activity
         */
        FrameLayout contentContainer = ButterKnife.findById(this, R.id.base_content);
        contentContainer.addView(view);
        /**
         * 由于需要子类上传注入basecontent的具体布局 需要最后绑定 防止出现找不到resouceId的情况
         */
        ButterKnife.bind(this);
        Log.i("toolbarInstance", toolbar.toString());
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
     * 获取toolbar
     *
     * @return
     */
    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * 设置主标题
     *
     * @param resourceId
     */
    public void setMainTitle(int resourceId) {
        toolbar.setTitle(Compat.getString(this, resourceId));
    }

    /**
     * 设置子类标题
     *
     * @param resourceId
     */
    public void setSubTitle(int resourceId) {
        toolbar.setSubtitle(Compat.getString(this, resourceId));
    }

    /**
     * 设置主标题字体颜色
     *
     * @param resourceId
     */
    public void setMainTitleColor(int resourceId) {
        toolbar.setTitleTextColor(Compat.getColor(this, resourceId));
    }

    /**
     * 设置子标题字体颜色
     *
     * @param resourceId
     */
    public void setSubTitleColor(int resourceId) {
        toolbar.setSubtitleTextColor(Compat.getColor(this, resourceId));
    }

    /**
     * 设置logoIcon
     *
     * @param resId
     */

    public void setLogoIcon(int resId) {
        toolbar.setLogo(resId);
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
     * 设置左边标题图标
     *
     * @param iconRes
     */
    public void setTitleNavigationIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }

    /**
     * 隐藏标题栏
     */
    protected void hideToolbar() {
        if (toolbar.getVisibility() == View.VISIBLE)
            toolbar.setVisibility(View.GONE);
    }

    /**
     * 不显示 NavigationButton
     */
    public void hideTitleNavigationButton() {
        toolbar.setNavigationIcon(null);
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
     * menu点击回调，默认无，子类可重写
     *
     * @param item
     * @return
     */
    protected boolean callbackOnMenuAction(MenuItem item) {
        return false;
    }


    /**
     * 为toolbar设置menu项
     */
    private void setInflateMenu() {
        if (getMenuLayoutId() > 0)
            toolbar.inflateMenu(getMenuLayoutId());
    }

    /**
     * 获取菜单资源id，默认无，子类可重写
     *
     * @return
     */
    protected int getMenuLayoutId() {
        return 0;
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
    public void initViews() {
        toolbar = ButterKnife.findById(this, R.id.tool_bar);
        setInflateMenu();
        Log.i("toolbar ", (toolbar.getVisibility() == View.VISIBLE) + "");
        setTitleBgColor(R.color.white);
        new StatusBarManager.builder(this)
                .setStatusBarColor(R.color.white)//状态栏颜色
                .setTintType(StatusBarManager.TintType.PURECOLOR)
                .setAlpha(0)//不透明度
                .create();
    }



}
