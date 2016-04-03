package com.xiaoyu.DoctorHelp.ui.mycenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
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
 * Created by xiaoyu on 2015/7/11.
 */
public class ActivityModifyNickname extends ActivityBase implements View.OnClickListener {
    private FrameLayout backFL;
    private EditText nickName;
    private TextView commit;

    private Account account;

    public static void open(Activity activity,int requestCode){
        Intent intent = new Intent(activity,ActivityModifyNickname.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        account = HCApplicaton.getInstance().getAccount();
        setContentView(R.layout.activity_modify_nickname);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getViews() {
        backFL = (FrameLayout) findViewById(R.id.backFL);
        commit = (TextView) findViewById(R.id.commit);
        nickName = (EditText) findViewById(R.id.nickName);
    }

    @Override
    protected void initViews() {
        if (!TextUtils.isEmpty(account.userName)) {
            nickName.setText(account.userName);
            nickName.setSelection(account.userName.length());
        }
    }

    @Override
    protected void setListeners() {
        backFL.setOnClickListener(this);
        commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        } else if (view.getId() == R.id.commit) {
            tryToCommit();
        }
    }

    private void tryToCommit() {
        final String nickNameStr = nickName.getText().toString();
        if (TextUtils.isEmpty(nickNameStr)) {
            ToastUtil.makeShortText(getString(R.string.please_input_nickname));
            return;
        }
        showDialog();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("nickname", nickNameStr));
        nameValuePairs.add(new BasicNameValuePair("userid", account.userId));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_MODIFY_NICKNAME, Request.POST, new Request.RequestListener() {
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
                    account.userName = nickNameStr;
                    account.saveMeInfoToPreference();
                    ToastUtil.makeShortText(getString(R.string.modifySuccessful));
                    ActivityModifyNickname.this.setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.makeShortText(getString(R.string.modifyPwdField));
                }
            }
        });
    }
}
