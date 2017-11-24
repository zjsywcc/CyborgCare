package com.moecheng.cyborgcare.monitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moecheng.cyborgcare.R;

/**
 * Created by wangchengcheng on 2017/11/20.
 */

public class MonitorFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_monitor, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View view) {

    }
}
