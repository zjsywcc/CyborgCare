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
import com.moecheng.cyborgcare.view.chart.provider.MyChartAdapter;
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
    @BindView(R.id.emg_chart)
    ShadowLineChart mEMGDataChart;
    public static ArrayList<Float> mEMGDataArrayList;
    public static LineChartAdapter mEMGDataAdapter;
    private static final int EMG_DATA_COUNT = 10;

    @BindView(R.id.rr_chart)
    ShadowLineChart mRRDataChart;
    public static ArrayList<Float> mRRDataArrayList;
    public static LineChartAdapter mRRDataAdapter;
    private static final int RR_DATA_COUNT = 10;

    @BindView(R.id.eeg_delta_chart)
    ShadowLineChart mEEGDeltaDataChart;
    public static ArrayList<Float> mEEGDeltaDataArrayList;
    public static LineChartAdapter mEEGDeltaDataAdapter;
    private static final int EEG_DATA_COUNT = 10;

    @BindView(R.id.eeg_theta_chart)
    ShadowLineChart mEEGThetaDataChart;
    public static ArrayList<Float> mEEGThetaDataArrayList;
    public static LineChartAdapter mEEGThetaDataAdapter;

    @BindView(R.id.eeg_lowalpha_chart)
    ShadowLineChart mEEGLowalphaDataChart;
    public static ArrayList<Float> mEEGLowalphaDataArrayList;
    public static LineChartAdapter mEEGLowalphaDataAdapter;

    @BindView(R.id.eeg_highalpha_chart)
    ShadowLineChart mEEGHighalphaDataChart;
    public static ArrayList<Float> mEEGHighalphaDataArrayList;
    public static LineChartAdapter mEEGHighalphaDataAdapter;

    @BindView(R.id.eeg_lowbeta_chart)
    ShadowLineChart mEEGLowbetaDataChart;
    public static ArrayList<Float> mEEGLowbetaDataArrayList;
    public static LineChartAdapter mEEGLowbetaDataAdapter;

    @BindView(R.id.eeg_highbeta_chart)
    ShadowLineChart mEEGHighbetaDataChart;
    public static ArrayList<Float> mEEGHighbetaDataArrayList;
    public static LineChartAdapter mEEGHighbetaDataAdapter;

    @BindView(R.id.eeg_lowgamma_chart)
    ShadowLineChart mEEGLowgammaDataChart;
    public static ArrayList<Float> mEEGLowgammaDataArrayList;
    public static LineChartAdapter mEEGLowgammaDataAdapter;

    @BindView(R.id.eeg_midgamma_chart)
    ShadowLineChart mEEGMidgammaDataChart;
    public static ArrayList<Float> mEEGMidgammaDataArrayList;
    public static LineChartAdapter mEEGMidgammaDataAdapter;

    @BindView(R.id.eeg_attention_chart)
    ShadowLineChart mEEGAttentionDataChart;
    public static ArrayList<Float> mEEGAttentionDataArrayList;
    public static LineChartAdapter mEEGAttentionDataAdapter;

    @BindView(R.id.eeg_mediation_chart)
    ShadowLineChart mEEGMediationDataChart;
    public static ArrayList<Float> mEEGMediationDataArrayList;
    public static LineChartAdapter mEEGMediationDataAdapter;

    @BindView(R.id.temp_chart)
    ShadowLineChart mTEMPDataChart;
    public static ArrayList<Float> mTEMPDataArrayList;
    public static LineChartAdapter mTEMPDataAdapter;
    private static final int TEMP_DATA_COUNT = 10;

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
        /**
         * 初始化emg图表
         */
        mEMGDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEMGDataArrayList = new ArrayList<>();
        mEMGDataAdapter = new MyChartAdapter(1, mEMGDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EMG_DATA_COUNT);
        mEMGDataChart.setAdapter(mEMGDataAdapter);
        /**
         * 初始化呼吸数据图表
         */
        mRRDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mRRDataArrayList = new ArrayList<>();
        mRRDataAdapter = new MyChartAdapter(1, mRRDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, RR_DATA_COUNT);
        mRRDataChart.setAdapter(mRRDataAdapter);
        /**
         * 初始化脑电Delta数据图表
         */
        mEEGDeltaDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEEGDeltaDataArrayList = new ArrayList<>();
        mEEGDeltaDataAdapter = new MyChartAdapter(1, mEEGDeltaDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EEG_DATA_COUNT);
        mEEGDeltaDataChart.setAdapter(mEEGDeltaDataAdapter);
        /**
         * 初始化脑电Theta数据图表
         */
        mEEGThetaDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEEGThetaDataArrayList = new ArrayList<>();
        mEEGThetaDataAdapter = new MyChartAdapter(1, mEEGThetaDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EEG_DATA_COUNT);
        mEEGThetaDataChart.setAdapter(mEEGThetaDataAdapter);
        /**
         * 初始化脑电Lowalpha数据图表
         */
        mEEGLowalphaDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEEGLowalphaDataArrayList = new ArrayList<>();
        mEEGLowalphaDataAdapter = new MyChartAdapter(1, mEEGLowalphaDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EEG_DATA_COUNT);
        mEEGLowalphaDataChart.setAdapter(mEEGLowalphaDataAdapter);
        /**
         * 初始化脑电Highalpha数据图表
         */
        mEEGHighalphaDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEEGHighalphaDataArrayList = new ArrayList<>();
        mEEGHighalphaDataAdapter = new MyChartAdapter(1, mEEGHighalphaDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EEG_DATA_COUNT);
        mEEGHighalphaDataChart.setAdapter(mEEGHighalphaDataAdapter);
        /**
         * 初始化脑电Lowbeta数据图表
         */
        mEEGLowbetaDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEEGLowbetaDataArrayList = new ArrayList<>();
        mEEGLowbetaDataAdapter = new MyChartAdapter(1, mEEGLowbetaDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EEG_DATA_COUNT);
        mEEGLowbetaDataChart.setAdapter(mEEGLowbetaDataAdapter);
        /**
         * 初始化脑电Highbeta数据图表
         */
        mEEGHighbetaDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEEGHighbetaDataArrayList = new ArrayList<>();
        mEEGHighbetaDataAdapter = new MyChartAdapter(1, mEEGHighbetaDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EEG_DATA_COUNT);
        mEEGHighbetaDataChart.setAdapter(mEEGHighbetaDataAdapter);
        /**
         * 初始化脑电Lowgamma数据图表
         */
        mEEGLowgammaDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEEGLowgammaDataArrayList = new ArrayList<>();
        mEEGLowgammaDataAdapter = new MyChartAdapter(1, mEEGLowgammaDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EEG_DATA_COUNT);
        mEEGLowgammaDataChart.setAdapter(mEEGLowgammaDataAdapter);
        /**
         * 初始化脑电Highgamma数据图表
         */
        mEEGMidgammaDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEEGMidgammaDataArrayList = new ArrayList<>();
        mEEGMidgammaDataAdapter = new MyChartAdapter(1, mEEGMidgammaDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EEG_DATA_COUNT);
        mEEGMidgammaDataChart.setAdapter(mEEGMidgammaDataAdapter);
        /**
         * 初始化脑电Attention数据图表
         */
        mEEGAttentionDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEEGAttentionDataArrayList = new ArrayList<>();
        mEEGAttentionDataAdapter = new MyChartAdapter(1, mEEGAttentionDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EEG_DATA_COUNT);
        mEEGAttentionDataChart.setAdapter(mEEGAttentionDataAdapter);
        /**
         * 初始化脑电Mediation数据图表
         */
        mEEGMediationDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mEEGMediationDataArrayList = new ArrayList<>();
        mEEGMediationDataAdapter = new MyChartAdapter(1, mEEGMediationDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, EEG_DATA_COUNT);
        mEEGMediationDataChart.setAdapter(mEEGMediationDataAdapter);
        /**
         * 初始化体温数据图表
         */
        mTEMPDataChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                formatter = new DecimalFormat("###.##");
                return formatter.format(v);
            }
        });
        mTEMPDataArrayList = new ArrayList<>();
        mTEMPDataAdapter = new MyChartAdapter(1, mTEMPDataArrayList, R.color.colorPrimary, R.color.colorPrimaryLight, TEMP_DATA_COUNT);
        mTEMPDataChart.setAdapter(mTEMPDataAdapter);

        if (mHandler == null && BluetoothControlActivity.getInstance() != null)
//            mHandler = new BluetoothResponseHandler(mEMGDataArrayList, mEMGDataAdapter, valuePairQueue, uploadThread, BluetoothControlActivity.getInstance());
            mHandler = new BluetoothResponseHandler(BluetoothControlActivity.getInstance());
//        fakeECGThread = new FakeECGThread(mEMGDataArrayList, mEMGDataAdapter);
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

        private List<String> list;
        private LineChartAdapter adapter;
        private volatile boolean mIsStopped = false;


        public FakeECGThread(List<String> list, LineChartAdapter adapter) {
            this.list = list;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            setStopped(false);
            while (!mIsStopped) {
                try {
//                    float value = getRandomECG();
                    String packet = "";
                    list.add(packet);
                    valuePairQueue.add(new UploadRequest.ValuePair(new Date().getTime(), packet));
                    if (list.size() > EMG_DATA_COUNT) {
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

//        private List<Float> list;
//        private LineChartAdapter adapter;
//        private LinkedBlockingDeque<UploadRequest.ValuePair> valuePairs;

        public BluetoothResponseHandler(BluetoothControlActivity activity) {
//            this.list = list;
//            this.adapter = adapter;
//            this.valuePairs = valuePairs;
//            this.uploadThread = uploadThread;
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
                            int errLoop = 0;
                            while (buffer.readableBytes() > Configurations.FRAME_LENGTH) {     //判断缓冲区大小大于一帧长度，就可以进行解帧
                                ByteBuf bufTemp = buffer.readBytes(1);    //先取第一个字节，判断是不是帧头的第一个字节0xFF
                                byte[] bytesTemp = new byte[1];
                                byte[] bytesTail = new byte[1];
                                bufTemp.readBytes(bytesTemp);

                                //打印buffer中包的内容
                                buffer.markReaderIndex();
                                byte[] bytesInBuf = new byte[buffer.readableBytes()];
                                buffer.readBytes(bytesInBuf);
                                Log.i("printBufHex", ByteUtil.byteArrayToHexStr(bytesInBuf));
                                buffer.getBytes(Configurations.FRAME_LENGTH - 1, bytesTail);
                                buffer.resetReaderIndex();
                                Log.i("printTailHex", ByteUtil.byteArrayToHexStr(bytesTail));
                                //判断第一个字节是不是0xFF，判断最后包尾是不是校验位，如果不是，直接丢弃，如果是，则进入if判断
                                if (bytesTemp[0] == (byte) 0xFF && bytesTail[0] <= 1) {
                                    buffer.markReaderIndex();
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
                                    Log.i("printValidByteArray", ByteUtil.byteArrayToHexStr(validData));
                                    //将取出来的这一帧数据在buffer的内存进行清除，释放内存
                                    buffer.discardReadBytes();
                                    break;
                                }
//                                else {       //第一个字节不是0xff情况
//                                      //指针回滚，回滚到只是取出第一个数
//                                    errLoop++;
//                                    if(errLoop > 1000) {
//                                        Log.i("PACKET_ERROR", "packet has wrong header");
//                                        ToastUtil.showToast(MainActivity.getInstance(), "包头错误");
//                                        break;
//                                    }
//                                }
                            }
                            if (validData[0] != 0x00) {
                                /**
                                 * 取出下标为1的肌电信号的值
                                 */
                                float emgValue1 = validData[37] & 0xFF;
                                float emgValue2 = validData[38] & 0xFF;
                                float emgValue3 = validData[39] & 0xFF;
                                float emgValue4 = validData[40] & 0xFF;
                                float emgValue = (emgValue1 + emgValue2 + emgValue3 + emgValue4) / 4;
//                                value = value + (float) Math.random() * 10;
                                Log.i("emgValue", emgValue + "");
                                if (emgValue % 16 != 0) {
                                    Log.i("BluetoothMsgErrValue", ByteUtil.byteArrayToHexStr(validData));
                                } else {
                                    Log.i("BluetoothMsgValidValue", ByteUtil.byteArrayToHexStr(validData));
                                }
                                mEMGDataArrayList.add(emgValue);
                                if (mEMGDataArrayList.size() > EMG_DATA_COUNT) {
                                    mEMGDataArrayList.remove(0);
                                }
                                mEMGDataAdapter.notifyDataSetChanged();


                                /**
                                 * 取出下标为5的呼吸信号的值
                                 */
                                float rrValue1 = validData[33] & 0xFF;
                                float rrValue2 = validData[34] & 0xFF;
                                float rrValue3 = validData[35] & 0xFF;
                                float rrValue4 = validData[36] & 0xFF;
                                float rrValue = (rrValue1 + rrValue2 + rrValue3 + rrValue4) / 4;
//                                value = value + (float) Math.random() * 10;
                                Log.i("rrValue", rrValue + "");
                                if (rrValue % 16 != 0) {
                                    Log.i("BluetoothMsgErrValue", ByteUtil.byteArrayToHexStr(validData));
                                } else {
                                    Log.i("BluetoothMsgValidValue", ByteUtil.byteArrayToHexStr(validData));
                                }
                                mRRDataArrayList.add(rrValue);
                                if (mRRDataArrayList.size() > RR_DATA_COUNT) {
                                    mRRDataArrayList.remove(0);
                                }
                                mRRDataAdapter.notifyDataSetChanged();

                                /**
                                 * 取出下标为x的脑电信号的值
                                 */
                                float eegDeltaValue = getRawEEGVoltage(validData[5], validData[6], validData[7]);
                                float eegThetaValue = getRawEEGVoltage(validData[8], validData[9], validData[10]);
                                float eegLowalphaValue = getRawEEGVoltage(validData[11], validData[12], validData[13]);
                                float eegHighalphaValue = getRawEEGVoltage(validData[14], validData[15], validData[16]);
                                float eegLowbetaValue = getRawEEGVoltage(validData[17], validData[18], validData[19]);
                                float eegHighbetaValue = getRawEEGVoltage(validData[20], validData[21], validData[22]);
                                float eegLowgammaValue = getRawEEGVoltage(validData[23], validData[24], validData[25]);
                                float eegMidgammaValue = getRawEEGVoltage(validData[26], validData[27], validData[28]);
                                float eegAttentionValue = validData[30] & 0xFF;
                                float eegMediationValue = validData[32] & 0xFF;

//                                eegDeltaValue = ((eegLowAlphaValue + eegHighAlphaValue) / 2
//                                        + eegLowAlphaValue / eegThetaValue +
//                                        eegLowAlphaValue * 2 / (eegHighBetaValue + eegLowBetaValue)) / 3;
                                Log.i("eegValue", eegDeltaValue + "");
                                if (eegDeltaValue % 16 != 0) {
                                    Log.i("BluetoothMsgErrValue", ByteUtil.byteArrayToHexStr(validData));
                                } else {
                                    Log.i("BluetoothMsgValidValue", ByteUtil.byteArrayToHexStr(validData));
                                }
                                mEEGDeltaDataArrayList.add(eegDeltaValue);
                                if (mEEGDeltaDataArrayList.size() > EEG_DATA_COUNT) {
                                    mEEGDeltaDataArrayList.remove(0);
                                }
                                mEEGDeltaDataAdapter.notifyDataSetChanged();

                                mEEGThetaDataArrayList.add(eegThetaValue);
                                if (mEEGThetaDataArrayList.size() > EEG_DATA_COUNT) {
                                    mEEGThetaDataArrayList.remove(0);
                                }
                                mEEGThetaDataAdapter.notifyDataSetChanged();
                                
                                mEEGLowalphaDataArrayList.add(eegLowalphaValue);
                                if (mEEGLowalphaDataArrayList.size() > EEG_DATA_COUNT) {
                                    mEEGLowalphaDataArrayList.remove(0);
                                }
                                mEEGLowalphaDataAdapter.notifyDataSetChanged();

                                mEEGHighalphaDataArrayList.add(eegHighalphaValue);
                                if (mEEGHighalphaDataArrayList.size() > EEG_DATA_COUNT) {
                                    mEEGHighalphaDataArrayList.remove(0);
                                }
                                mEEGHighalphaDataAdapter.notifyDataSetChanged();

                                mEEGLowbetaDataArrayList.add(eegLowbetaValue);
                                if (mEEGLowbetaDataArrayList.size() > EEG_DATA_COUNT) {
                                    mEEGLowbetaDataArrayList.remove(0);
                                }
                                mEEGLowbetaDataAdapter.notifyDataSetChanged();

                                mEEGHighbetaDataArrayList.add(eegHighbetaValue);
                                if (mEEGHighbetaDataArrayList.size() > EEG_DATA_COUNT) {
                                    mEEGHighbetaDataArrayList.remove(0);
                                }
                                mEEGHighbetaDataAdapter.notifyDataSetChanged();

                                mEEGLowgammaDataArrayList.add(eegLowgammaValue);
                                if (mEEGLowgammaDataArrayList.size() > EEG_DATA_COUNT) {
                                    mEEGLowgammaDataArrayList.remove(0);
                                }
                                mEEGLowgammaDataAdapter.notifyDataSetChanged();

                                mEEGMidgammaDataArrayList.add(eegMidgammaValue);
                                if (mEEGMidgammaDataArrayList.size() > EEG_DATA_COUNT) {
                                    mEEGMidgammaDataArrayList.remove(0);
                                }
                                mEEGMidgammaDataAdapter.notifyDataSetChanged();

                                mEEGAttentionDataArrayList.add(eegAttentionValue);
                                if (mEEGAttentionDataArrayList.size() > EEG_DATA_COUNT) {
                                    mEEGAttentionDataArrayList.remove(0);
                                }
                                mEEGAttentionDataAdapter.notifyDataSetChanged();

                                mEEGMediationDataArrayList.add(eegMediationValue);
                                if (mEEGMediationDataArrayList.size() > EEG_DATA_COUNT) {
                                    mEEGMediationDataArrayList.remove(0);
                                }
                                mEEGMediationDataAdapter.notifyDataSetChanged();

                                /**
                                 * 取出下标为x的体温的值
                                 */
                                float tempValue = getRawBodyTemp(validData[1], validData[2]);
//                                value = value + (float) Math.random() * 10;
                                Log.i("tempValue", tempValue + "");
                                if (tempValue % 16 != 0) {
                                    Log.i("BluetoothMsgErrValue", ByteUtil.byteArrayToHexStr(validData));
                                } else {
                                    Log.i("BluetoothMsgValidValue", ByteUtil.byteArrayToHexStr(validData));
                                }
                                mTEMPDataArrayList.add(tempValue);
                                if (mTEMPDataArrayList.size() > TEMP_DATA_COUNT) {
                                    mTEMPDataArrayList.remove(0);
                                }
                                mTEMPDataAdapter.notifyDataSetChanged();

                                String validPacket = ByteUtil.byteArrayToHexStr(validData);

                                valuePairQueue.add(new UploadRequest.ValuePair(new Date().getTime(), validPacket));
                                if (uploadThread.runState.get() == 0) {
                                    uploadThread.start();
                                }
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

    private static float getRawEEGVoltage(byte high, byte mid, byte low) {
        int h = (int) high;
        int m = ((int)mid) & 0xFF;
        int l = ((int)low) & 0xFF;
        int value = (h << 16) | (m << 8) | l;
        if(value > 32768)
            value -= 65536;
        float rawVoltage = (float)(value * (1.8 / 4096));
//        float rawVoltage = (float)(value);
        return rawVoltage;
    }

    private static float getRawBodyTemp(byte highByte, byte lowByte) {
        int high = (int) highByte;
        int low = ((int) lowByte) & 0xFF;
        int rawTemp = ((high << 8) | low) / 100;
        return rawTemp;
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
            synchronized (valuePairQueue) {
                while (!valuePairQueue.isEmpty() && getRunState().get() == 1) {
                    List<UploadRequest.ValuePair> valuePairs = new ArrayList<>();
                    for (int i = 0; i < 20; i++) {
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
                    try {
                        request.setAndroid_version(android.os.Build.VERSION.RELEASE);
                        request.setDevice(android.os.Build.MODEL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            }
            runState.set(0);
        }
    }


    // ==========================================================================


}
