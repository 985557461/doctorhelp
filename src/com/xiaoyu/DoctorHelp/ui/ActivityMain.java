package com.xiaoyu.DoctorHelp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.ui.community.CommunityFragment;
import com.xiaoyu.DoctorHelp.ui.my_chat.MyChatFragment;
import com.xiaoyu.DoctorHelp.ui.mycenter.MyCenterFragment;
import com.xiaoyu.DoctorHelp.util.ToastUtil;

public class ActivityMain extends ActivityBase implements View.OnClickListener {

    /**
     * init views
     * *
     */
    private LinearLayout helpLL;
    private ImageView helpImageView;
    private TextView helpTextView;

    private LinearLayout communityLL;
    private ImageView communityImageView;
    private TextView communityTextView;

    private LinearLayout myCenterLL;
    private ImageView myCenterImageView;
    private TextView myCenterTextView;

    /**
     * fragment
     * *
     */
    private Fragment mFragmentCurrent;
    public MyChatFragment myChatFragment;
    private CommunityFragment communityFragment;
    private MyCenterFragment myCenterFragment;

    /**
     * double back to finish
     * *
     */
    private static int TIME_LONG = 3 * 1000;
    private long lastTime;

    /**
     * ActivityMain
     * *
     */
    private static ActivityMain activityMain = null;

    /**
     * other people login in your account
     * *
     */
    public static boolean isConflict = false;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivityMain.class);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        activityMain = this;
    }

    public static ActivityMain getInstance() {
        return activityMain;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (myChatFragment != null) {
            myChatFragment.onNewIntent(intent);
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void getViews() {
        helpLL = (LinearLayout) findViewById(R.id.helpLL);
        helpImageView = (ImageView) findViewById(R.id.helpImageView);
        helpTextView = (TextView) findViewById(R.id.helpTextView);

        communityLL = (LinearLayout) findViewById(R.id.communityLL);
        communityImageView = (ImageView) findViewById(R.id.communityImageView);
        communityTextView = (TextView) findViewById(R.id.communityTextView);

        myCenterLL = (LinearLayout) findViewById(R.id.myCenterLL);
        myCenterImageView = (ImageView) findViewById(R.id.myCenterImageView);
        myCenterTextView = (TextView) findViewById(R.id.myCenterTextView);
    }

    @Override
    protected void initViews() {
        helpImageView.setSelected(true);
        helpTextView.setSelected(true);
        setDefaultFragment();
    }

    @Override
    protected void setListeners() {
        helpLL.setOnClickListener(this);
        communityLL.setOnClickListener(this);
        myCenterLL.setOnClickListener(this);
    }

    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        myChatFragment = new MyChatFragment();
        transaction.add(R.id.fragmentLayout, myChatFragment);
        transaction.commit();
        mFragmentCurrent = myChatFragment;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.helpLL:
                changeTab(0);
                break;
            case R.id.communityLL:
                changeTab(1);
                break;
            case R.id.myCenterLL:
                changeTab(2);
                break;
        }
    }

    public void switchContent(Fragment from, Fragment to) {
        if (from != to) {
            mFragmentCurrent = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) {
                if (from != null && from.isAdded()) {
                    transaction.hide(from);
                }
                transaction.add(R.id.fragmentLayout, to).commitAllowingStateLoss();
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss();
            }
        }
    }

    private void changeTab(int index) {
        switch (index) {
            case 0: {
                helpImageView.setImageResource(R.drawable.icon_tab_help_press);
                communityImageView.setImageResource(R.drawable.icon_tab_community_normal);
                myCenterImageView.setImageResource(R.drawable.icon_tab_center_normal);

                helpTextView.setSelected(true);
                communityTextView.setSelected(false);
                myCenterTextView.setSelected(false);

                if (myChatFragment == null) {
                    myChatFragment = new MyChatFragment();
                }
                switchContent(mFragmentCurrent, myChatFragment);
            }
            break;
            case 1: {
                helpImageView.setImageResource(R.drawable.icon_tab_help_normal);
                communityImageView.setImageResource(R.drawable.icon_tab_community_press);
                myCenterImageView.setImageResource(R.drawable.icon_tab_center_normal);

                helpTextView.setSelected(false);
                communityTextView.setSelected(true);
                myCenterTextView.setSelected(false);

                if (communityFragment == null) {
                    communityFragment = new CommunityFragment();
                }
                switchContent(mFragmentCurrent, communityFragment);
            }
            break;
            case 2: {
                helpImageView.setImageResource(R.drawable.icon_tab_help_normal);
                communityImageView.setImageResource(R.drawable.icon_tab_community_normal);
                myCenterImageView.setImageResource(R.drawable.icon_tab_center_press);

                helpTextView.setSelected(false);
                communityTextView.setSelected(false);
                myCenterTextView.setSelected(true);

                if (myCenterFragment == null) {
                    myCenterFragment = new MyCenterFragment();
                }
                switchContent(mFragmentCurrent, myCenterFragment);
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - lastTime < TIME_LONG) {
            killActivity();
        } else {
            ToastUtil.makeShortText("再按一次返回键退出");
            lastTime = t;
            return;
        }
        super.onBackPressed();
    }
}
