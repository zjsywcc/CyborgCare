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
import com.moecheng.cyborgcare.db.DataAccess;
import com.moecheng.cyborgcare.db.entity.User;
import com.moecheng.cyborgcare.event.Event;
import com.moecheng.cyborgcare.profile.Dialog.LoginDialog;
import com.moecheng.cyborgcare.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangchengcheng on 2017/11/20.
 */

public class ProfileFragment extends Fragment implements RippleView.OnRippleCompleteListener {

    private final int REQUEST_PERSONAL_ACTIVITY = 0x31;


    @BindView(R.id.profile_person_rel)
    RippleView mPersonCell;
    @BindView(R.id.profile_personal_indicator)
    ImageView mPersonNext;
    @BindView(R.id.profile_notify_rel)
    RippleView mNotifyCell;
    @BindView(R.id.profile_upload_rel)
    RippleView mSyncCell;
    @BindView(R.id.profile_reset_rel)
    RippleView mResetCell;
    @BindView(R.id.profile_login_rel)
    RippleView mLoginCell;
    @BindView(R.id.profile_server_connection_rel)
    RippleView mServerConnectionCell;
    @BindView(R.id.profile_bluetooth_connection_rel)
    RippleView mBluetoothConnectionCell;
    @BindView(R.id.profile_monitor_gap_rel)
    RippleView mMonitorSpeedCell;
    @BindView(R.id.profile_avatar_imageview)
    ImageView mAvatarImageView;
    @BindView(R.id.profile_person_name)
    TextView mUserNameTextView;
    @BindView(R.id.profile_server_connection_textview)
    TextView mServerConnectionTextView;
    @BindView(R.id.profile_bluetooth_connection_textview)
    TextView mBluetoothConnectionTextView;
    @BindView(R.id.profile_monitor_gap_textview)
    TextView mMonitorSpeedTextView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);
        initView(rootView);
        refreshContents();
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    // This method will be called when a SomeOtherEvent is posted
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void logoutCallback(Event event) {
        if(event.eventInfo.equals("logout")) {
            refreshContents();
        }
    }

    private void initView(View view) {
        mLoginCell.setOnRippleCompleteListener(this);
        mPersonCell.setOnRippleCompleteListener(this);
        mPersonCell.setEnabled(false);
        mBluetoothConnectionCell.setOnRippleCompleteListener(this);
    }

    private void refreshContents() {
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
        if (DataAccess.getUser().getUid() == -1) {
            mUserNameTextView.setText("请登录");
            // 隐藏next图标
            mPersonNext.setVisibility(View.INVISIBLE);
            // 显示登录按钮
            mLoginCell.setVisibility(View.VISIBLE);
            // 无效点击
            mPersonCell.setEnabled(false);
        } else {
            Log.i("uid", DataAccess.getUser().getUid() + "");
            mUserNameTextView.setText(DataAccess.getUser().getUsername());
            mPersonNext.setVisibility(View.VISIBLE);
            mLoginCell.setVisibility(View.GONE);
            mPersonCell.setEnabled(true);
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        if (mLoginCell.equals(rippleView)) {

            login();

        } else if (mPersonCell.equals(rippleView)) {

            jumpToDetail();

        } else if(mBluetoothConnectionCell.equals(rippleView)) {

            openBluetoothDeviceList();

        }
    }

    private void login() {
        LoginDialog loginDialog = new LoginDialog(getActivity(), true);
        loginDialog.setOnLoginRegisterCompleteListener(new LoginDialog.OnLoginRegisterCompleteListener() {
            @Override
            public void onLoginRegisterComplete(boolean isLogin, User bean) {
                refreshContents();
            }

            @Override
            public void onFail(boolean isLogin, int errorFlag) {

            }
        });
        loginDialog.show();
    }

    private void jumpToDetail() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ProfileDetailActivity.class);
        startActivityForResult(intent, REQUEST_PERSONAL_ACTIVITY);
    }

    private void openBluetoothDeviceList() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), BluetoothControlActivity.class);
        startActivityForResult(intent, REQUEST_PERSONAL_ACTIVITY);
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
