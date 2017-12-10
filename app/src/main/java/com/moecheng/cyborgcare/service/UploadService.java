package com.moecheng.cyborgcare.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.moecheng.cyborgcare.network.api.BaseApi;
import com.moecheng.cyborgcare.network.api.UploadApi;
import com.moecheng.cyborgcare.network.bean.request.UploadRequest;
import com.moecheng.cyborgcare.network.bean.response.UploadResponse;
import com.moecheng.cyborgcare.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangchengcheng on 2017/12/10.
 */

public class UploadService extends Service {

    private LinkedBlockingDeque<UploadRequest.ValuePair> valuePairDeque;
    private Activity mActivity;
    private AtomicInteger runState;

    public AtomicInteger getRunState() {
        return runState;
    }

    private void setRunState(int state) {
        this.runState.getAndSet(state);
    }

    public static void startUpload(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        context.startService(intent);
    }


    public UploadService(Activity activity, LinkedBlockingDeque<UploadRequest.ValuePair> valuePairDeque) {
        this.valuePairDeque = valuePairDeque;
        this.mActivity = activity;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void handleUpload() {
        setRunState(1);
        while (!valuePairDeque.isEmpty() && getRunState().get() == 1) {
            List<UploadRequest.ValuePair> valuePairs = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                UploadRequest.ValuePair valuePair = null;
                try {
                    valuePair = valuePairDeque.take();
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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "onDestroy");
    }

//    public static class UploadThread extends Thread {
//
//        private AtomicInteger runState;
//
//        public UploadThread() {
//            runState = new AtomicInteger(0);
//        }
//
//        public AtomicInteger getRunState() {
//            return runState;
//        }
//
//        private void setRunState(int state) {
//            this.runState.getAndSet(state);
//        }
//
//        public void stopUploading() {
//            setRunState(0);
//        }
//
//        @Override
//        public void run() {
//            runState.set(1);
//            while(!valuePairQueue.isEmpty() && getRunState().get() == 1) {
//                List<UploadRequest.ValuePair> valuePairs = new ArrayList<>();
//                for(int i = 0; i < 60; i++) {
//                    UploadRequest.ValuePair valuePair = null;
//                    try {
//                        valuePair = valuePairQueue.take();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if (valuePair != null) {
//                        valuePairs.add(valuePair);
//                    }
//                }
//                UploadRequest request = new UploadRequest();
//                request.setAction("upload");
//                request.setAndroid_version(android.os.Build.VERSION.RELEASE);
//                request.setDevice(android.os.Build.MODEL);
//                request.setValuePairs(valuePairs);
//                UploadApi uploadApi = new UploadApi();
//                uploadApi.getResponse(request, new BaseApi.Handler<UploadResponse>() {
//                    @Override
//                    public void onSuccess(UploadResponse response) {
//                        if (response != null) {
//                            Log.i("uploadAction", response.getMsg());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(UploadResponse response, int errorFlag) {
//                        if (response != null) {
//                            Log.i("uploadAction", response.getMsg());
//                        } else {
//                            Log.i("uploadAction", String.format("错误信息 %s:,上传失败", errorFlag));
//                        }
//                    }
//                }, mActivity);
//
//            }
//            runState.set(0);
//        }
//    }



}
