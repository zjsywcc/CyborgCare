package com.moecheng.cyborgcare.network.api;

import android.content.Context;

import com.moecheng.cyborgcare.Configurations;
import com.moecheng.cyborgcare.network.bean.request.BaseRequest;
import com.moecheng.cyborgcare.network.bean.response.BaseResponse;
import com.moecheng.cyborgcare.network.httpservice.HttpService;
import com.moecheng.cyborgcare.util.Log;
import com.moecheng.cyborgcare.util.ToastUtil;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 将所有服务通信API抽象成一个基类
 * Created by wangchengcheng on 2017/11/24.
 */
public abstract class BaseApi<REQ extends BaseRequest, RES extends BaseResponse> {

    HttpService service;

    public void getResponse(REQ req, final Handler<RES> handler, final Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Configurations.REQUEST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(HttpService.class);
        Call<RES> call = getCall(req);
        call.enqueue(new Callback<RES>() {
            @Override
            public void onResponse(Call<RES> call, Response<RES> response) {
                Log.i("retrofit response code", response.code() + "");
                if(!response.isSuccessful()) {
                    try {
                        Log.i("retrofit response error body", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ToastUtil.showToast(context, "服务器内部错误！");
                    handler.onFailure(null, 503);
                    return;
                }
                RES content = response.body();
                if(content == null) {
                    Log.i("retrofit response body", "is null");
                    ToastUtil.showToast(context, "JSON解析错误！");
                    handler.onFailure(null, 504);
                    return;
                }
                String msg = content.getMsg();
                int code = content.getCode();
                if(code == Configurations.SUCCESS) {
                    handler.onSuccess(content);
                } else {
                    handler.onFailure(content, code);
                    ToastUtil.showToast(context, msg);
                }
            }

            @Override
            public void onFailure(Call<RES> call, Throwable t) {
                handler.onFailure(null, 404);
                Log.i("retrofit_connection", "failed");
                ToastUtil.showToast(context, "连接到服务器失败");
            }
        });

    }

    protected abstract Call<RES> getCall(REQ req);

    public interface Handler<T extends BaseResponse> {

        /**
         * 响应请求结果
         * @param response okHttp网络请求的结果
         */
        void onSuccess(T response);

        /**
         * 失败处理
         * @param response okHttp网络请求的结果
         */
        void onFailure(T response, int errorFlag);

    }


}
