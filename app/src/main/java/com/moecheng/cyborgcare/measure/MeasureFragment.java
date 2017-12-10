package com.moecheng.cyborgcare.measure;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moecheng.cyborgcare.R;
import com.moecheng.cyborgcare.network.api.BaseApi;
import com.moecheng.cyborgcare.network.api.UploadApi;
import com.moecheng.cyborgcare.network.bean.request.UploadRequest;
import com.moecheng.cyborgcare.network.bean.response.UploadResponse;
import com.moecheng.cyborgcare.util.Log;
import com.moecheng.cyborgcare.view.chart.Chart;
import com.moecheng.cyborgcare.view.chart.ShadowLineChart;
import com.moecheng.cyborgcare.view.chart.provider.LineChartAdapter;
import com.moecheng.cyborgcare.view.chart.provider.SimpleChartAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

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
    public static ArrayList<Float> mECGDataArrayList;
    public static LineChartAdapter mECGDataAdapter;

    private static final int ECG_DATA_COUNT = 10;

    private DecimalFormat formatter;
    private FakeECGThread fakeECGThread;

    public static final int MESSAGE_READ = 2;
    private static BluetoothResponseHandler mHandler;

    private static Activity mActivity;

    public static UploadThread uploadThread = new UploadThread();
    public static LinkedBlockingDeque<UploadRequest.ValuePair> valuePairQueue = new LinkedBlockingDeque<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_measure, container, false);
        ButterKnife.bind(this, rootView);
        mActivity = getActivity();
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
                return position + "";
            }
        };
        mECGDataChart.setAdapter(mECGDataAdapter);
        if (mHandler == null) mHandler = new BluetoothResponseHandler(mECGDataArrayList, mECGDataAdapter, valuePairQueue, uploadThread);
//        fakeECGThread = new FakeECGThread(mECGDataArrayList, mECGDataAdapter);
//        fakeECGThread.start();
    }


    @Override
    public void onDestroyView() {
        if (fakeECGThread != null) {
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
                    valuePairQueue.add(new UploadRequest.ValuePair(new Date().getTime(), value));
                    if (list.size() > ECG_DATA_COUNT) {
                        list.remove(0);
                    }
                    adapter.notifyDataSetChanged();
                    if(uploadThread.runState.get() == 0) {
                        uploadThread.start();
                    }
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

    /**
     * 用于从蓝牙流接收数据的处理器
     */
    public static class BluetoothResponseHandler extends Handler {

        private List<Float> list;
        private LineChartAdapter adapter;
        private LinkedBlockingDeque<UploadRequest.ValuePair> valuePairs;
        private UploadThread uploadThread;

        public BluetoothResponseHandler(List<Float> list, LineChartAdapter adapter, LinkedBlockingDeque<UploadRequest.ValuePair> valuePairs, UploadThread uploadThread) {
            this.list = list;
            this.adapter = adapter;
            this.valuePairs = valuePairs;
            this.uploadThread = uploadThread;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    final byte[] readBytes = (byte[]) msg.obj;
                    if (readBytes != null) {
                        float value = 0;
                        if (readBytes.length > 5) {
                            value = Math.abs(readBytes[0]);

                            if (value > 0) {
                                list.add(value);
                                if (list.size() > ECG_DATA_COUNT) {
                                    list.remove(0);
                                }
                                adapter.notifyDataSetChanged();
                                valuePairQueue.add(new UploadRequest.ValuePair(new Date().getTime(), value));
                                if(uploadThread.runState.get() == 0) {
                                    uploadThread.start();
                                }
                            }
                        }
                        Log.i("bluetoothMsg", value + "");
                    }
                    break;

            }
        }
    }




    public static class UploadThread extends Thread {

        private AtomicInteger runState;

        public UploadThread() {
            runState = new AtomicInteger(0);
        }

        public AtomicInteger getRunState() {
            return runState;
        }

        private void setRunState(int state) {
            this.runState.getAndSet(state);
        }

        public void stopUploading() {
            setRunState(0);
        }

        @Override
        public void run() {
            runState.set(1);
            while(!valuePairQueue.isEmpty() && getRunState().get() == 1) {
                List<UploadRequest.ValuePair> valuePairs = new ArrayList<>();
                for(int i = 0; i < 60; i++) {
                    UploadRequest.ValuePair valuePair = null;
                    try {
                        valuePair = valuePairQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (valuePair != null) {
                        valuePairs.add(valuePair);
                    }
                }
                UploadRequest request = new UploadRequest();
                request.setAction("upload");
                request.setAndroid_version(android.os.Build.VERSION.RELEASE);
                request.setDevice(android.os.Build.MODEL);
                request.setValuePairs(valuePairs);
                UploadApi uploadApi = new UploadApi();
                uploadApi.getResponse(request, new BaseApi.Handler<UploadResponse>() {
                    @Override
                    public void onSuccess(UploadResponse response) {
                        if (response != null) {
                            Log.i("uploadAction", response.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(UploadResponse response, int errorFlag) {
                        if (response != null) {
                            Log.i("uploadAction", response.getMsg());
                        } else {
                            Log.i("uploadAction", String.format("错误信息 %s:,上传失败", errorFlag));
                        }
                    }
                }, mActivity);

            }
            runState.set(0);
        }
    }


    // ==========================================================================


}
