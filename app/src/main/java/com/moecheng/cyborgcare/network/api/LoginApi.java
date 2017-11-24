package com.moecheng.cyborgcare.network.api;



import com.moecheng.cyborgcare.network.bean.request.LoginRequest;
import com.moecheng.cyborgcare.network.bean.response.LoginResponse;

import retrofit2.Call;


/**
 * Created by wangchengcheng on 2017/11/24.
 */

public class LoginApi extends BaseApi<LoginRequest, LoginResponse> {


    @Override
    protected Call<LoginResponse> getCall(LoginRequest loginRequest) {
        return service.getLoginResponse(loginRequest);
    }

}
