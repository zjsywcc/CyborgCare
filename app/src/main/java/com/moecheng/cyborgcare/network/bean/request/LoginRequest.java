package com.moecheng.cyborgcare.network.bean.request;

/**
 * Created by wangchengcheng on 2017/11/24.
 */

public class LoginRequest extends BaseRequest {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static class Builder {

        private LoginRequest loginRequest = new LoginRequest();

        public Builder setUsername(String username) {
            loginRequest.setUsername(username);
            return this;
        }

        public Builder setPassword(String password) {
            loginRequest.setPassword(password);
            return this;
        }

        public LoginRequest build() {
            loginRequest.setAction("login");
            loginRequest.setAndroid_version(android.os.Build.VERSION.RELEASE);
            loginRequest.setDevice(android.os.Build.MODEL);
            return loginRequest;
        }
    }
}
