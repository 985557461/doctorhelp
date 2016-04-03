package com.xiaoyu.DoctorHelp.ui.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.meilishuo.gson.annotations.SerializedName;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import com.xiaoyu.DoctorHelp.util.Request;
import com.xiaoyu.DoctorHelp.util.ToastUtil;
import com.xiaoyu.DoctorHelp.widget.HCPopListView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2015/7/4.
 */
public class ActivityPostQuestion extends ActivityBase implements View.OnClickListener {
    private LinearLayout back;
    private LinearLayout postQuestion;
    private LinearLayout questionType;
    private TextView typeStr;
    private EditText question;

    /**
     * pop_listview about
     * *
     */
    private ArrayList<String> items = new ArrayList<String>();
    private int communityid = 0;

    /**
     * account
     * *
     */
    private Account account;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivityPostQuestion.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_post_question);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getViews() {
        account = HCApplicaton.getInstance().getAccount();
        back = (LinearLayout) findViewById(R.id.back);
        postQuestion = (LinearLayout) findViewById(R.id.postQuestion);
        questionType = (LinearLayout) findViewById(R.id.questionType);
        typeStr = (TextView) findViewById(R.id.typeStr);
        question = (EditText) findViewById(R.id.question);
    }

    @Override
    protected void initViews() {
        items.add(getResources().getString(R.string.zhili));
        items.add(getResources().getString(R.string.xuexi));
        items.add(getResources().getString(R.string.qinzi));
        items.add(getResources().getString(R.string.shejiao));
        items.add(getResources().getString(R.string.qingchunqi));
    }

    @Override
    protected void setListeners() {
        back.setOnClickListener(this);
        postQuestion.setOnClickListener(this);
        questionType.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.postQuestion:
                toPostQuestion();
                break;
            case R.id.questionType:
                showPopListViewDlg();
                break;
        }
    }

    private void toPostQuestion() {
        if (communityid == 0) {
            ToastUtil.makeShortText(getString(R.string.choseQuestionType));
            return;
        }
        String content = question.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.makeShortText(getString(R.string.inputQuestion));
            return;
        }
        showDialog(getString(R.string.posting));
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("userid", account.userId));
        nameValuePairs.add(new BasicNameValuePair("communityid", communityid + ""));
        nameValuePairs.add(new BasicNameValuePair("content", content));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_POST_TIEZI, Request.POST, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                dismissDialog();
                ToastUtil.makeShortText(getString(R.string.postFiled));
            }

            @Override
            public void onComplete(String response) {
                dismissDialog();
                PostResultModel model = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, PostResultModel.class);
                if (model != null && model.result == 1) {
                    ToastUtil.makeShortText(getString(R.string.postSuccessful));
                    Intent intent = new Intent();
                    intent.setAction(CommunityFragment.RefreshActionFromServer);
                    sendBroadcast(intent);
                    finish();
                } else {
                    ToastUtil.makeShortText(getString(R.string.postFiled));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (HCPopListView.isShowing(this)) {
            HCPopListView.onBackPressed(this);
            return;
        }
        super.onBackPressed();
    }

    private void showPopListViewDlg() {
        new HCPopListView(this, getString(R.string.questionType), getString(R.string.cancel), items, new HCPopListView.HCPopListViewListener() {
            @Override
            public void onItemClicked(int index, String content) {
                communityid = index;
                typeStr.setText(content);
            }

            @Override
            public void onCancelClicked() {

            }
        }).show();
    }

    class PostResultModel {
        @SerializedName("message")
        public String message;

        @SerializedName("result")
        public int result;

        @SerializedName("topicid")
        public String topicid;
    }
}
