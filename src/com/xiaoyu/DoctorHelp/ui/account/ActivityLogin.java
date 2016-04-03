package com.xiaoyu.DoctorHelp.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.chat.chatuidemo.Constant;
import com.xiaoyu.DoctorHelp.chat.chatuidemo.db.UserDao;
import com.xiaoyu.DoctorHelp.chat.chatuidemo.domain.User;
import com.xiaoyu.DoctorHelp.chat.chatuidemo.utils.CommonUtils;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import com.xiaoyu.DoctorHelp.ui.ActivityMain;
import com.xiaoyu.DoctorHelp.util.Request;
import com.xiaoyu.DoctorHelp.util.ToastUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaoyuPC on 2015/6/7.
 */
public class ActivityLogin extends ActivityBase implements View.OnClickListener {
    private FrameLayout backFL;
    private TextView register;
    private EditText phoneNumber;
    private EditText password;
    private TextView login;
    private TextView forgetPwd;

    private Account account;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivityLogin.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        account = HCApplicaton.getInstance().getAccount();
        setContentView(R.layout.activity_login_hc);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getViews() {
        backFL = (FrameLayout) findViewById(R.id.backFL);
        register = (TextView) findViewById(R.id.register);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);
        login = (TextView) findViewById(R.id.login);
        forgetPwd = (TextView) findViewById(R.id.forgetPwd);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void setListeners() {
        backFL.setOnClickListener(this);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        forgetPwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backFL:
                finish();
                break;
            case R.id.register:
                ActivityRegister.open(this);
                break;
            case R.id.login:
                tryToLogin();
                break;
            case R.id.forgetPwd:
                break;
        }
    }

    private void tryToLogin() {
        if (!CommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        final String phoneNumberStr = phoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumberStr)) {
            ToastUtil.makeShortText(R.string.phoneNotNul);
            return;
        }
        final String pwdStr = password.getText().toString();
        if (TextUtils.isEmpty(pwdStr)) {
            ToastUtil.makeShortText(R.string.passwordNotNull);
            return;
        }
        showDialog(getString(R.string.logining));
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("phoneNumber", phoneNumberStr));
        nameValuePairs.add(new BasicNameValuePair("password", pwdStr));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_LOGIN, Request.POST, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                dismissDialog();
                ToastUtil.makeShortText(getString(R.string.loginFiled));
            }

            @Override
            public void onComplete(String response) {
                LoginModel loginModel = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, LoginModel.class);
                if (loginModel != null && loginModel.result == 1) {
                    if(loginModel.status == 1){
                        account.phoneNumber = phoneNumberStr;
                        account.password = pwdStr;
                        account.userId = loginModel.user_id;
                        account.saveMeInfoToPreference();
                        toLoginChatServer();
                    }else{
                        ToastUtil.makeShortText(getString(R.string.login_after_shenhe_ok));
                    }
                } else {
                    dismissDialog();
                    if (loginModel == null || TextUtils.isEmpty(loginModel.message)) {
                        ToastUtil.makeShortText(R.string.loginFiled);
                    } else {
                        ToastUtil.makeShortText(loginModel.message);
                    }
                }
            }
        });
    }

    private void toLoginChatServer() {
        EMChatManager.getInstance().login(account.userId, account.password, new EMCallBack() {
            @Override
            public void onSuccess() {
                HCApplicaton.getInstance().setUserName(account.userId);
                HCApplicaton.getInstance().setPassword(account.password);

                try {
                    EMChatManager.getInstance().loadAllLocalGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    processContactsAndGroups();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            HCApplicaton.getInstance().logout(null);
                            Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                        HCApplicaton.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                dismissDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.makeShortText(getString(R.string.loginSuccessful));
                    }
                });
                startActivity(new Intent(ActivityLogin.this, ActivityMain.class));
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        dismissDialog();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void processContactsAndGroups() throws EaseMobException {
        List<String> usernames = EMContactManager.getInstance().getContactUserNames();
        EMLog.d("roster", "contacts size: " + usernames.size());
        Map<String, User> userlist = new HashMap<String, User>();
        for (String username : usernames) {
            User user = new User();
            user.setUsername(username);
            setUserHearder(username, user);
            userlist.put(username, user);
        }

        User addFriends = new User();
        String addFriendStr = getString(R.string.add_friend);
        addFriends.setUsername(Constant.ADD_FRIEND);
        addFriends.setNick(addFriendStr);
        addFriends.setHeader("");
        userlist.put(Constant.ADD_FRIEND, addFriends);

        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getString(R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);

        User groupUser = new User();
        String strGroup = getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        User chatRoomItem = new User();
        String strChatRoom = getString(R.string.chat_room);
        chatRoomItem.setUsername(Constant.CHAT_ROOM);
        chatRoomItem.setNick(strChatRoom);
        chatRoomItem.setHeader("");
        userlist.put(Constant.CHAT_ROOM, chatRoomItem);

        HCApplicaton.getInstance().setContactList(userlist);
        System.out.println("----------------" + userlist.values().toString());
        UserDao dao = new UserDao(ActivityLogin.this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);

        List<String> blackList = EMContactManager.getInstance().getBlackListUsernamesFromServer();
        EMContactManager.getInstance().saveBlackList(blackList);

        EMChatManager.getInstance().fetchJoinedGroupsFromServer();
    }

    protected void setUserHearder(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }

    class LoginModel {
        public String message;
        public int result;
        public int status;
        public String nickname;
        public String problem_num;
        public String news_num;
        public String user_id;
        public String imageurl;
    }
}
