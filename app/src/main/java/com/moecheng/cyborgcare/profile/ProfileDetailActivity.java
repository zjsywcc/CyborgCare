package com.moecheng.cyborgcare.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.moecheng.cyborgcare.R;
import com.moecheng.cyborgcare.db.DataAccess;
import com.moecheng.cyborgcare.db.entity.User;
import com.moecheng.cyborgcare.event.Event;
import com.moecheng.cyborgcare.statusbar.StatusBarManager;
import com.moecheng.cyborgcare.ui.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wangchengcheng on 2017/11/27.
 */

public class ProfileDetailActivity extends BaseActivity implements RippleView.OnRippleCompleteListener {

    @BindView(R.id.collapsingtoolbar)
    Toolbar collapsingToolbar;
    @BindView(R.id.personal_info_avatar_imageview)
    CircleImageView profile_avatar;
    @BindView(R.id.profile_person_name)
    TextView person_name;
    @BindView(R.id.tool_bar_title)
    TextView toolbar_tilte;
    @BindView(R.id.personal_logout_tv)
    RippleView logout_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected View getContentView() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.activity_profiledetail, null);
        return root;
    }

    @Override
    public void initViews() {

        hideToolbar();
        new StatusBarManager.builder(this)
                .setStatusBarColor(R.color.white)//状态栏颜色
                .setTintType(StatusBarManager.TintType.PURECOLOR)
                .setAlpha(0)//不透明度
                .create();
        toolbar_tilte.setText(DataAccess.getUser().getUsername());
        collapsingToolbar.setLogo(R.mipmap.icon_title_back);
        profile_avatar.setImageResource(R.drawable.default_avatar);
        logout_button.setOnRippleCompleteListener(this);
//        person_name.setText(DataAccess.getUser().getUsername());
//        setSupportActionBar(collapsingToolbar);
    }

    @Override
    public void onComplete(RippleView rippleView) {
        if(rippleView.equals(logout_button)) {
            logout();
        }
    }

    private void logout() {
        User currentUser = DataAccess.getUser();
        currentUser.setUid(-1);
        DataAccess.updateUser(currentUser);
        EventBus.getDefault().post(new Event(10, "logout"));
        onBackPressed();
    }
}
