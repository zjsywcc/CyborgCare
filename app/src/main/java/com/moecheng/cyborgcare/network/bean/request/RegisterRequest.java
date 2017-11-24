package com.moecheng.cyborgcare.network.bean.request;

/**
 * Created by wangchengcheng on 2017/11/24.
 */

public class RegisterRequest extends BaseRequest {

    private String username;

    private String password;

    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static class Builder {

        private RegisterRequest registerRequest = new RegisterRequest();

        public Builder setUsername(String username) {
            registerRequest.setUsername(username);
            return this;
        }

        public Builder setPassword(String password) {
            registerRequest.setPassword(password);
            return this;
        }

        public Builder setEmail(String email) {
            registerRequest.setEmail(email);
            return this;
        }


        public RegisterRequest build() {
            registerRequest.setAction("register");
            registerRequest.setAndroid_version(android.os.Build.VERSION.RELEASE);
            registerRequest.setDevice(android.os.Build.MODEL);
            return registerRequest;
        }
    }
}
