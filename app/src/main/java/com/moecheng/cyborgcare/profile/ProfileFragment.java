package com.moecheng.cyborgcare.profile;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.moecheng.cyborgcare.Configurations;
import com.moecheng.cyborgcare.R;
import com.moecheng.cyborgcare.local.User;
import com.moecheng.cyborgcare.network.bean.UserInfoBean;
import com.moecheng.cyborgcare.profile.Dialog.LoginDialog;
import com.moecheng.cyborgcare.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wangchengcheng on 2017/11/20.
 */

public class ProfileFragment extends Fragment implements RippleView.OnRippleCompleteListener {

    private final int REQUEST_PERSONAL_ACTIVITY = 0x31;


    @Bind(R.id.profile_person_rel)
    RippleView mPersonCell;
    @Bind(R.id.profile_notify_rel)
    RippleView mNotifyCell;
    @Bind(R.id.profile_upload_rel)
    RippleView mSyncCell;
    @Bind(R.id.profile_reset_rel)
    RippleView mResetCell;
    @Bind(R.id.profile_login_rel)
    RippleView mLoginCell;
    @Bind(R.id.profile_server_connection_rel)
    RippleView mServerConnectionCell;
    @Bind(R.id.profile_bluetooth_connection_rel)
    RippleView mBluetoothConnectionCell;
    @Bind(R.id.profile_monitor_gap_rel)
    RippleView mMonitorSpeedCell;
    @Bind(R.id.profile_avatar_imageview)
    ImageView mAvatarImageView;
    @Bind(R.id.profile_person_name)
    TextView mUserNameTextView;
    @Bind(R.id.profile_server_connection_textview)
    TextView mServerConnectionTextView;
    @Bind(R.id.profile_bluetooth_connection_textview)
    TextView mBluetoothConnectionTextView;
    @Bind(R.id.profile_monitor_gap_textview)
    TextView mMonitorSpeedTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);
        initView(rootView);
        refreshContents();
        return rootView;
    }

    private void initView(View view) {
        mLoginCell.setOnRippleCompleteListener(this);
    }

    private void refreshContents() {

        mUserNameTextView.setText(User.getInstance().getUserName());

        String home = getActivity().getFilesDir().getAbsolutePath();
        File avatarFile = new File(home + Configurations.AVATAR_FILE_PATH);
        if (avatarFile.exists()) {
            try {
                InputStream is = new FileInputStream(avatarFile);
                mAvatarImageView.setImageBitmap(BitmapFactory.decodeStream(is));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mAvatarImageView.setImageResource(R.drawable.default_avatar);
        }

        if (User.getInstance().getUid() == -1) {
            mLoginCell.setVisibility(View.VISIBLE);
        } else {
            Log.i("uid", User.getInstance().getUid() + "");
            mLoginCell.setVisibility(View.GONE);
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        if (mLoginCell.equals(rippleView)) {

            login();

        } else {

        }
    }

    private void login() {
        LoginDialog loginDialog = new LoginDialog(getActivity(), true);
        loginDialog.setOnLoginRegisterCompleteListener(new LoginDialog.OnLoginRegisterCompleteListener() {
            @Override
            public void onLoginRegisterComplete(boolean isLogin, UserInfoBean bean) {

            }

            @Override
            public void onFail(boolean isLogin, int errorFlag) {

            }
        });
        loginDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PERSONAL_ACTIVITY) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                refreshContents();
            }
        }
    }


}
