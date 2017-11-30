package com.moecheng.cyborgcare.network.api;


import com.moecheng.cyborgcare.network.bean.request.UploadRequest;
import com.moecheng.cyborgcare.network.bean.response.UploadResponse;

import retrofit2.Call;

/**
 * Created by wangchengcheng on 2017/11/30.
 */

public class UploadApi extends BaseApi<UploadRequest, UploadResponse> {

    @Override
    protected Call<UploadResponse> getCall(UploadRequest uploadRequest) {
        return service.getUploadResponse(uploadRequest);
    }
}
