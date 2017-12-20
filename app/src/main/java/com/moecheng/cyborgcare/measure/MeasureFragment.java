package com.moecheng.cyborgcare.measure;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moecheng.cyborgcare.Configurations;
import com.moecheng.cyborgcare.R;
import com.moecheng.cyborgcare.bluetooth.DeviceConnector;
import com.moecheng.cyborgcare.network.api.BaseApi;
import com.moecheng.cyborgcare.network.api.UploadApi;
import com.moecheng.cyborgcare.network.bean.request.UploadRequest;
import com.moecheng.cyborgcare.network.bean.response.UploadResponse;
import com.moecheng.cyborgcare.profile.BluetoothControlActivity;
import com.moecheng.cyborgcare.util.ByteUtil;
import com.moecheng.cyborgcare.util.Log;
import com.moecheng.cyborgcare.view.chart.Chart;
import com.moecheng.cyborgcare.view.chart.ShadowLineChart;
import com.moecheng.cyborgcare.view.chart.provider.LineChartAdapter;
import com.moecheng.cyborgcare.view.chart.provider.SimpleChartAdapter;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static com.moecheng.cyborgcare.ui.BaseActivity.MESSAGE_DEVICE_NAME;
import static com.moecheng.cyborgcare.ui.BaseActivity.MESSAGE_STATE_CHANGE;


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
    public static final LinkedBlockingDeque<UploadRequest.ValuePair> valuePairQueue = new LinkedBlockingDeque<>();

    private static ByteBuf buffer = Unpooled.buffer(1024 * 1000);


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
        if (mHandler == null && BluetoothControlActivity.getInstance() != null)
            mHandler = new BluetoothResponseHandler(mECGDataArrayList, mECGDataAdapter, valuePairQueue, uploadThread, BluetoothControlActivity.getInstance());
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
                    if (uploadThread.runState.get() == 0) {
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

        private WeakReference<BluetoothControlActivity> mActivity;

        public void setTarget(BluetoothControlActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<BluetoothControlActivity>(target);
        }

        private List<Float> list;
        private LineChartAdapter adapter;
        private LinkedBlockingDeque<UploadRequest.ValuePair> valuePairs;
        private UploadThread uploadThread;

        public BluetoothResponseHandler(List<Float> list, LineChartAdapter adapter, LinkedBlockingDeque<UploadRequest.ValuePair> valuePairs, UploadThread uploadThread
                , BluetoothControlActivity activity) {
            this.list = list;
            this.adapter = adapter;
            this.valuePairs = valuePairs;
            this.uploadThread = uploadThread;
            this.mActivity = new WeakReference<BluetoothControlActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BluetoothControlActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:

                        Log.i("MESSAGE_STATE_CHANGE: ", msg.arg1 + "");
                        final Toolbar bar = activity.getToolbar();
                        switch (msg.arg1) {
                            case DeviceConnector.STATE_CONNECTED:
                                bar.setSubtitle(BluetoothControlActivity.MSG_CONNECTED);
                                break;
                            case DeviceConnector.STATE_CONNECTING:
                                bar.setSubtitle(BluetoothControlActivity.MSG_CONNECTING);
                                break;
                            case DeviceConnector.STATE_NONE:
                                bar.setSubtitle(BluetoothControlActivity.MSG_NOT_CONNECTED);
                                break;
                        }
                        activity.invalidateOptionsMenu();
                        break;
                    case MESSAGE_READ:
                        final byte[] readBytes = (byte[]) msg.obj;
                        if (readBytes != null && readBytes.length > 0) {
                            /**
                             * 处理源源不断的字符数组 通过netty ByteBuf解决蓝牙粘包丢包问题
                             */
                            buffer.writeBytes(readBytes);     //将蓝牙收到的byte数组放入bytebuf缓冲区中
                            //   Log.i("tag", "缓冲区大小" + buffer.readableBytes() + "");
                            byte[] validData = new byte[Configurations.FRAME_LENGTH];
                            //重新组帧
                            while (buffer.readableBytes() > Configurations.FRAME_LENGTH) {     //判断缓冲区大小大于一帧长度，就可以进行解帧
                                ByteBuf bufTemp = buffer.readBytes(1);    //先取第一个字节，判断是不是帧头的第一个字节0xFF
                                byte[] bytesTemp = new byte[1];
                                bufTemp.readBytes(bytesTemp);
                                if (bytesTemp[0] == (byte) 0xFF) {        //判断第一个字节是不是0xFF，如果不是，直接丢弃，如果是，则进入if判断
                                    ByteBuf bufTemp2 = buffer.readBytes(Configurations.FRAME_LENGTH - 1);
                                    byte[] bytesTemp2 = new byte[Configurations.FRAME_LENGTH - 1];
                                    bufTemp2.readBytes(bytesTemp2);
                                    /**
                                     * TODO 判断帧尾部分
                                     */
                                    //取出帧的后续部分，还需要判断帧尾是不是包含一个奇偶校验位的0x00；如果不是0x00，说明这个帧不完整，即需要重新进入第二个字节搜索帧头
//                                    if (bytesTemp2[bytesTemp2.length - 1] > 1) {
//                                        buffer.resetReaderIndex();   //指针回滚，回滚到只是取出第一个数
//                                        continue;       //重新进入while循环
//                                    }
                                    //重新组帧
                                    byte[] bytesTemp3 = new byte[Configurations.FRAME_LENGTH];
                                    bytesTemp3[0] = (byte) 0xFF;
                                    System.arraycopy(bytesTemp2, 0, bytesTemp3, 1, bytesTemp2.length);
                                    //如果是，则放入list
                                    validData = bytesTemp3;     //放入byteListInbuf链表的帧shiw
                                    buffer.discardReadBytes();  //将取出来的这一帧数据在buffer的内存进行清除，释放内存
                                    break;
                                } else {       //第一个字节不是0xff情况
                                    buffer.resetReaderIndex();   //指针回滚，回滚到只是取出第一个数
                                    continue;
                                }
                            }
                            float value = validData[1];
                            Log.i("bluetoothMsg", value + "");
                            if (value % 16 != 0) {
                                Log.i("BluetoothMsgErrValue", ByteUtil.byteArrayToHexStr(validData));
                            } else {
                                Log.i("BluetoothMsgValidValue", ByteUtil.byteArrayToHexStr(validData));
                            }
                            list.add(value);
                            if (list.size() > ECG_DATA_COUNT) {
                                list.remove(0);
                            }
                            adapter.notifyDataSetChanged();
                            valuePairQueue.add(new UploadRequest.ValuePair(new Date().getTime(), value));
                            if (uploadThread.runState.get() == 0) {
                                uploadThread.start();
                            }
                        }
                        break;
                    case MESSAGE_DEVICE_NAME:
                        activity.setDeviceName((String) msg.obj);
                        break;

                }
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
            while (!valuePairQueue.isEmpty() && getRunState().get() == 1) {
                List<UploadRequest.ValuePair> valuePairs = new ArrayList<>();
                for (int i = 0; i < 60; i++) {
                    UploadRequest.ValuePair valuePair = null;
                    try {
                        synchronized (valuePairQueue) {
                            valuePair = valuePairQueue.take();
                        }
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
