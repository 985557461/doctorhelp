package com.xiaoyu.DoctorHelp.ui;

import android.content.Intent;
import android.os.Bundle;
import com.easemob.chat.EMChatManager;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.chat.chatuidemo.DemoHXSDKHelper;
import com.xiaoyu.DoctorHelp.ui.account.ActivityLogin;

/**
 * Created by xiaoyuPC on 2015/6/7.
 */
public class ActivitySplash extends ActivityBase {
    private Account account;
    private long sleepTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        account = HCApplicaton.getInstance().getAccount();
        setContentView(R.layout.activity_splash);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            public void run() {
                if (DemoHXSDKHelper.getInstance().isLogined()) {
                    // ** 免登陆情况 加载所有本地群和会话
                    //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                    //加上的话保证进了主页面会话和群组都已经load完毕
                    long start = System.currentTimeMillis();
                    EMChatManager.getInstance().loadAllLocalGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;
                    //等待sleeptime时长
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //进入主页面
                    startActivity(new Intent(ActivitySplash.this, ActivityMain.class));
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                    finish();
                }
            }
        }).start();
    }

    @Override
    protected void getViews() {
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void setListeners() {

    }
}
