package com.moecheng.cyborgcare.measure;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moecheng.cyborgcare.R;
import com.moecheng.cyborgcare.util.Log;
import com.moecheng.cyborgcare.view.chart.Chart;
import com.moecheng.cyborgcare.view.chart.LineChart;
import com.moecheng.cyborgcare.view.chart.ShadowLineChart;
import com.moecheng.cyborgcare.view.chart.provider.LineChartAdapter;
import com.moecheng.cyborgcare.view.chart.provider.SimpleChartAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangchengcheng on 2017/11/20.
 */

public class MeasureFragment extends Fragment {

    /**
     * 测量曲线图
     */
    @BindView(R.id.measure_chart)
    ShadowLineChart mECGDataChart;
    private ArrayList<Float> mECGDataArrayList;
    private LineChartAdapter mECGDataAdapter;

    private final int ECG_DATA_COUNT = 10;

    private DecimalFormat formatter;
    private FakeECGThread fakeECGThread;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_measure, container, false);
        ButterKnife.bind(this, rootView);
        initView(rootView);
        return rootView;
    }

    private void initView(View view) {
        formatter = new DecimalFormat("###.##");
        mECGDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                return formatter.format(v);
            }
        });
        mECGDataArrayList = new ArrayList<>();
        mECGDataAdapter = new SimpleChartAdapter() {
            @Override
            public int getLineCount() {
                return 1;
            }

            @Override
            public ArrayList<Float> getLineData(int index) {
                return mECGDataArrayList;
            }

            @Override
            public int getLineColorId(int index) {
                return R.color.colorPrimary;
            }

            @Override
            public int getShadowColor(int position) {
                return R.color.colorPrimaryLight;
            }

            @Override
            public int getXLabelsCount() {
                return ECG_DATA_COUNT;
            }

            @Override
            public String getXLabel(int position) {
                return position+"";
            }
        };
        mECGDataChart.setAdapter(mECGDataAdapter);
        fakeECGThread = new FakeECGThread(mECGDataArrayList, mECGDataAdapter);
        fakeECGThread.start();
    }


    @Override
    public void onDestroyView() {
        if(fakeECGThread != null) {
            fakeECGThread.stopThread();
        }
        super.onDestroyView();
    }


    class FakeECGThread extends Thread {

        private List<Float> list;
        private LineChartAdapter adapter;
        private volatile boolean mIsStopped = false;


        public FakeECGThread(List<Float> list, LineChartAdapter adapter) {
            this.list = list;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            setStopped(false);
            while (!mIsStopped) {
                try {
                    float value = getRandomECG();
                    list.add(value);
                    if (list.size() > ECG_DATA_COUNT) {
                        list.remove(0);
                    }
                    adapter.notifyDataSetChanged();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.i("fakeECGThreadError", e.toString());
                }
            }
        }

        public boolean isStopped() {
            return mIsStopped;
        }

        private void setStopped(boolean isStop) {
            if (mIsStopped != isStop)
                mIsStopped = isStop;
        }

        public void stopThread() {
            setStopped(true);
        }
    }

    private float getRandomECG() {
        float randomECG = (float) (50 + Math.random() * 50);
        return randomECG;
    }
}
