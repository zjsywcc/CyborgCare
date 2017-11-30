package com.moecheng.cyborgcare;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.moecheng.cyborgcare.calender.CalenderFragment;
import com.moecheng.cyborgcare.measure.MeasureFragment;
import com.moecheng.cyborgcare.monitor.MonitorFragment;
import com.moecheng.cyborgcare.profile.ProfileFragment;
import com.moecheng.cyborgcare.ui.BaseActivity;
import com.moecheng.cyborgcare.viewcontroller.TabViewController;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private TabViewController tabViewController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    protected View getContentView() {
        MeasureFragment measureFragment = new MeasureFragment();
        MonitorFragment monitorFragment = new MonitorFragment();
        CalenderFragment calenderFragment = new CalenderFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(measureFragment);
        fragmentArrayList.add(monitorFragment);
        fragmentArrayList.add(calenderFragment);
        fragmentArrayList.add(profileFragment);
        ArrayList<Integer> iconArrayList = new ArrayList<>();
        iconArrayList.add(R.mipmap.tab_measure_unselected);
        iconArrayList.add(R.mipmap.tab_monitor_unselected);
        iconArrayList.add(R.mipmap.tab_graph_unselected);
        iconArrayList.add(R.mipmap.tab_settings_unselected);
        iconArrayList.add(R.mipmap.tab_measure_selected);
        iconArrayList.add(R.mipmap.tab_monitor_selected);
        iconArrayList.add(R.mipmap.tab_graph_selected);
        iconArrayList.add(R.mipmap.tab_settings_selected);
        ArrayList<String> titles = new ArrayList<>();
        titles.add(getResources().getString(R.string.main_measure_title));
        titles.add(getResources().getString(R.string.main_monitor_title));
        titles.add(getResources().getString(R.string.main_calender_title));
        titles.add(getResources().getString(R.string.main_profile_title));
        tabViewController = new TabViewController(this, fragmentArrayList, iconArrayList, titles);
        tabViewController.setScrollable(true);
        tabViewController.disableOverScrollable();

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
        contentLayout.addView(tabViewController);
        return contentLayout;
    }

    @Override
    public void initViews() {
        super.initViews();
        setToolbarTitleTv(R.string.app_name);
    }
}
