package com.moecheng.cyborgcare.network.httpservice;

import com.moecheng.cyborgcare.Configurations;
import com.moecheng.cyborgcare.network.bean.request.LoginRequest;
import com.moecheng.cyborgcare.network.bean.request.RegisterRequest;
import com.moecheng.cyborgcare.network.bean.request.UploadRequest;
import com.moecheng.cyborgcare.network.bean.response.LoginResponse;
import com.moecheng.cyborgcare.network.bean.response.RegisterResponse;
import com.moecheng.cyborgcare.network.bean.response.UploadResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by wangchengcheng on 2017/11/24.
 */

public interface HttpService {

    @POST(Configurations.REQUEST_URL + Configurations.USER_URL + "/login")
    Call<LoginResponse> getLoginResponse(@Body LoginRequest loginRequest);

    @POST(Configurations.REQUEST_URL + Configurations.USER_URL + "/register")
    Call<RegisterResponse> getRegisterResponse(@Body RegisterRequest registerRequest);

    @POST(Configurations.REQUEST_URL + Configurations.UPLOAD_URL + "/upload")
    Call<UploadResponse> getUploadResponse(@Body UploadRequest uploadRequest);

}
