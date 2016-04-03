package com.xiaoyu.DoctorHelp.ui.mycenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.CommonModel;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import com.xiaoyu.DoctorHelp.util.Request;
import com.xiaoyu.DoctorHelp.util.ToastUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2015/6/14.
 */
public class ActivityModifyPassword extends ActivityBase implements View.OnClickListener {
    private FrameLayout backFL;
    private TextView commit;
    private EditText oldPwd;
    private EditText newPwd;
    private LinearLayout showPwdLL;
    private ImageView showPwd;

    private boolean isShowPwd = false;

    /**
     * account
     * *
     */
    private Account account;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivityModifyPassword.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_modify_password);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getViews() {
        account = HCApplicaton.getInstance().getAccount();
        backFL = (FrameLayout) findViewById(R.id.backFL);
        commit = (TextView) findViewById(R.id.commit);
        oldPwd = (EditText) findViewById(R.id.oldPwd);
        newPwd = (EditText) findViewById(R.id.newPwd);
        showPwdLL = (LinearLayout) findViewById(R.id.showPwdLL);
        showPwd = (ImageView) findViewById(R.id.showPwd);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void setListeners() {
        backFL.setOnClickListener(this);
        commit.setOnClickListener(this);
        showPwdLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backFL:
                finish();
                break;
            case R.id.commit:
                tryToCommit();
                break;
            case R.id.showPwdLL:
                showPwd();
                break;
        }
    }

    private void showPwd() {
        if (!isShowPwd) {
            showPwd.setBackgroundColor(getResources().getColor(R.color.red));
            oldPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            newPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isShowPwd = true;
        } else {
            showPwd.setBackgroundColor(getResources().getColor(R.color.gray_normal));
            oldPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            newPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isShowPwd = false;
        }
    }

    private void tryToCommit() {
        String oldPwdStr = oldPwd.getText().toString();
        if (TextUtils.isEmpty(oldPwdStr)) {
            ToastUtil.makeShortText(getString(R.string.inputOldPwd));
            return;
        }
        final String newPwdStr = newPwd.getText().toString();
        if (TextUtils.isEmpty(newPwdStr)) {
            ToastUtil.makeShortText(getString(R.string.inputNewPwd));
            return;
        }
        showDialog();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("phoneNum", account.phoneNumber));
        nameValuePairs.add(new BasicNameValuePair("oldPassword", oldPwdStr));
        nameValuePairs.add(new BasicNameValuePair("newPassword", newPwdStr));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_MODIFY_PWD, Request.POST, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                dismissDialog();
                ToastUtil.showErrorMessage(e, "");
            }

            @Override
            public void onComplete(String response) {
                dismissDialog();
                CommonModel commonModel = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, CommonModel.class);
                if (commonModel != null && commonModel.result == 1) {
                    account.password = newPwdStr;
                    account.saveMeInfoToPreference();
                    ToastUtil.makeShortText(getString(R.string.modifySuccessful));
                    finish();
                }else{
                    ToastUtil.makeShortText(getString(R.string.modifyPwdField));
                }
            }
        });
    }
}
