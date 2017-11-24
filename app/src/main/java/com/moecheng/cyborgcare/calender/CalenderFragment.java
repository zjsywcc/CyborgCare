package com.moecheng.cyborgcare.calender;

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

public class CalenderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calender, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View view) {

    }
}
