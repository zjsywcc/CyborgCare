package com.moecheng.cyborgcare.network.api;

import com.moecheng.cyborgcare.network.bean.request.RegisterRequest;
import com.moecheng.cyborgcare.network.bean.response.RegisterResponse;

import retrofit2.Call;

/**
 * Created by wangchengcheng on 2017/11/24.
 */

public class RegisterApi extends BaseApi<RegisterRequest, RegisterResponse> {

    @Override
    protected Call<RegisterResponse> getCall(RegisterRequest registerRequest) {
        return service.getRegisterResponse(registerRequest);
    }
}
