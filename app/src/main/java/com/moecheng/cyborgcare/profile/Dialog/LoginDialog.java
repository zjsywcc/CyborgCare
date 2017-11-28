package com.moecheng.cyborgcare.profile.Dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.moecheng.cyborgcare.R;
import com.moecheng.cyborgcare.db.DataAccess;
import com.moecheng.cyborgcare.db.entity.User;
import com.moecheng.cyborgcare.network.api.BaseApi;
import com.moecheng.cyborgcare.network.api.LoginApi;
import com.moecheng.cyborgcare.network.api.RegisterApi;
import com.moecheng.cyborgcare.network.bean.request.LoginRequest;
import com.moecheng.cyborgcare.network.bean.request.RegisterRequest;
import com.moecheng.cyborgcare.network.bean.response.LoginResponse;
import com.moecheng.cyborgcare.network.bean.response.RegisterResponse;
import com.moecheng.cyborgcare.util.DialogBuilder;
import com.moecheng.cyborgcare.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangchengcheng on 2017/11/22.
 */

public class LoginDialog extends Dialog implements View.OnClickListener {

    private Context context;
    @BindView(R.id.login_dialog_login_header)
    TextView mLoginHeader;
    @BindView(R.id.login_dialog_register_header)
    TextView mRegisterHeader;
    @BindView(R.id.login_dialog_login_header_mask)
    View mLoginMask;
    @BindView(R.id.login_dialog_register_header_mask)
    View mRegisterMask;
    @BindView(R.id.login_dialog_name_input)
    EditText mUserNameEdit;
    @BindView(R.id.login_dialog_pwd_input)
    EditText mPwdEdit;
    @BindView(R.id.login_dialog_pwd_confirm_input)
    EditText mPwdConfirmEdit;
    @BindView(R.id.login_dialog_login_btn)
    TextView mLoginButton;
    @BindView(R.id.login_dialog_confirm_rel)
    LinearLayout mConfirmLayout;

    private boolean isLogin;

    private OnLoginRegisterCompleteListener onLoginRegisterCompleteListener = new OnLoginRegisterCompleteListener() {
        @Override
        public void onLoginRegisterComplete(boolean isLogin, User bean) {

        }

        @Override
        public void onFail(boolean isLogin, int errorFlag) {

        }
    };

    public LoginDialog(Context context, boolean isLogin) {
        super(context);
        this.context = context;
        this.isLogin = isLogin;
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_login_dialog);
        ButterKnife.bind(this);
        initView();
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        mLoginButton.setOnClickListener(this);
        mLoginHeader.setOnClickListener(this);
        mRegisterHeader.setOnClickListener(this);

        if (isLogin) {
            mLoginMask.setVisibility(View.VISIBLE);
            mRegisterMask.setVisibility(View.INVISIBLE);
            mConfirmLayout.setVisibility(View.GONE);
            mLoginButton.setText(context.getResources().getString(R.string.login_dialog_header_login));
        } else {
            mLoginMask.setVisibility(View.INVISIBLE);
            mRegisterMask.setVisibility(View.VISIBLE);
            mConfirmLayout.setVisibility(View.VISIBLE);
            mLoginButton.setText(context.getResources().getString(R.string.login_dialog_header_register));
        }
    }

    @Override
    public void onClick(View v) {
        if (mLoginButton.equals(v)) {
            if (isLogin)    login();
            else            register();
        } else if (mLoginHeader.equals(v)) {
            isLogin = true;
            mLoginMask.setVisibility(View.VISIBLE);
            mRegisterMask.setVisibility(View.INVISIBLE);
            mConfirmLayout.setVisibility(View.GONE);
            mLoginButton.setText(context.getResources().getString(R.string.login_dialog_header_login));
        } else if (mRegisterHeader.equals(v)) {
            isLogin = false;
            mLoginMask.setVisibility(View.INVISIBLE);
            mRegisterMask.setVisibility(View.VISIBLE);
            mConfirmLayout.setVisibility(View.VISIBLE);
            mLoginButton.setText(context.getResources().getString(R.string.login_dialog_header_register));
        }
    }

    private void login() {

        String username = mUserNameEdit.getText().toString();
        String pwd = mPwdEdit.getText().toString();
        if (username.length() == 0 || pwd.length() == 0) {
            mPwdEdit.setText("");
            return;
        }

        final ProgressDialog dialog = new DialogBuilder(context)
                .createProgress(R.string.login_dialog_header_login,
                        context.getResources().getString(R.string.login_ing_message),
                        false);
        dismiss();
        dialog.show();

        LoginRequest.Builder builder = new LoginRequest.Builder();
        LoginRequest request = builder.setPassword(pwd)
                .setUsername(username)
                .build();
        LoginApi loginApi = new LoginApi();
        loginApi.getResponse(request, new BaseApi.Handler<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                try {
                    Gson gson = new Gson();
                    LoginResponse.Data data = gson.fromJson(response.getData(), LoginResponse.Data.class);
                    User user = DataAccess.getUser();
                    user.setUid(data.getUid());
                    user.setUsername(data.getUsername());
                    user.setEmail(data.getEmail());
                    user.setAge(data.getAge());
                    user.setSex(data.getSex());
                    user.setFatigueIndex(data.getFatigue_index());
                    user.setToken(data.getToken());
                    DataAccess.updateUser(user);
                    onLoginRegisterCompleteListener.onLoginRegisterComplete(true, user);
                    dialog.dismiss();
                    ToastUtil.showToast(context, "登陆成功！");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(LoginResponse response, int errorFlag) {
                try {
                    onLoginRegisterCompleteListener.onFail(true, errorFlag);
                    dialog.dismiss();
                    String content = "";
                    if(response != null) {
                        content = response.getMsg();
                    } else {
                        content = "错误代码：" + errorFlag;
                    }
                    new DialogBuilder(context).create()
                            .setTitle(R.string.tips)
                            .setContent(content)
                            .setPositive(R.string.ok)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, context);
    }

    private void register() {
        String email = mUserNameEdit.getText().toString();
        String pwd1 = mPwdEdit.getText().toString();
        String pwd2 = mPwdConfirmEdit.getText().toString();
        if(email.length() == 0 || pwd1.length() == 0 || pwd2.length() == 0 || !pwd1.equals(pwd2)) {
            mPwdEdit.setText("");
            mPwdConfirmEdit.setText("");
            return;
        }

        final ProgressDialog dialog = new DialogBuilder(context)
                .createProgress(R.string.login_dialog_header_register,
                        context.getResources().getString(R.string.register_ing_message),
                        false);
        dismiss();
        dialog.show();

        RegisterRequest.Builder builder = new RegisterRequest.Builder();
        RegisterRequest registerRequest = builder.setEmail(email)
                .setUsername(email)
                .setPassword(pwd1).build();
        RegisterApi registerApi = new RegisterApi();
        registerApi.getResponse(registerRequest, new BaseApi.Handler<RegisterResponse>() {
            @Override
            public void onSuccess(RegisterResponse response) {
                Gson gson = new Gson();
                RegisterResponse.Data data = gson.fromJson(response.getData(), RegisterResponse.Data.class);
                User user = DataAccess.getUser();
                user.setUid(data.getUid());
                user.setUsername(data.getUsername());
                user.setEmail(data.getEmail());
                user.setAge(data.getAge());
                user.setSex(data.getSex());
                user.setFatigueIndex(data.getFatigue_index());
                user.setToken(data.getToken());
                DataAccess.updateUser(user);
                onLoginRegisterCompleteListener.onLoginRegisterComplete(true, user);
                dialog.dismiss();
                ToastUtil.showToast(context, "注册成功！");
            }

            @Override
            public void onFailure(RegisterResponse response, int errorFlag) {
                try {
                    onLoginRegisterCompleteListener.onFail(true, errorFlag);
                    dialog.dismiss();
                    String content = "";
                    if(response != null) {
                        content = response.getMsg();
                    } else {
                        content = "错误代码：" + errorFlag;
                    }
                    new DialogBuilder(context).create()
                            .setTitle(R.string.tips)
                            .setContent(content)
                            .setPositive(R.string.ok)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, context);
    }

    public void setOnLoginRegisterCompleteListener(OnLoginRegisterCompleteListener l) {
        onLoginRegisterCompleteListener = l;
    }

    public interface OnLoginRegisterCompleteListener {
        void onLoginRegisterComplete(boolean isLogin, User user);
        void onFail(boolean isLogin, int errorFlag);
    }
}
