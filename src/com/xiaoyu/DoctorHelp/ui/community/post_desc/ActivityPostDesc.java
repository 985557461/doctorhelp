package com.xiaoyu.DoctorHelp.ui.community.post_desc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.meilishuo.gson.annotations.SerializedName;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import com.xiaoyu.DoctorHelp.ui.community.CommunityFragment;
import com.xiaoyu.DoctorHelp.ui.community.PostModel;
import com.xiaoyu.DoctorHelp.util.Request;
import com.xiaoyu.DoctorHelp.util.ToastUtil;
import com.xiaoyu.DoctorHelp.widget.RefreshListView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2015/7/4.
 */
public class ActivityPostDesc extends ActivityBase implements View.OnClickListener, RefreshListView.OnLoadMoreListener, RefreshListView.OnRefreshListener {
    private FrameLayout back;
    private RefreshListView refreshListView;
    private PostHeaderView postHeaderView;
    private EditText replyContent;
    private TextView submit;

    private int p = 0;
    private final int SIZE = 50;
    private List<CommentModel> commentModels = new ArrayList<CommentModel>();
    private CommentAdapter commentAdapter;

    /**
     * to refresh postlist data
     * *
     */
    private static PostModel destPostModel;
    private PostModel postModel;

    /**
     * intent data
     * *
     */
    public static String kTopicId = "key_topic_id";
    private String topicid;

    /**
     * account
     * *
     */
    private Account account;

    public static void open(Activity activity, PostModel postModel, String topicid) {
        destPostModel = postModel;
        Intent intent = new Intent(activity, ActivityPostDesc.class);
        intent.putExtra(kTopicId, topicid);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        topicid = getIntent().getStringExtra(kTopicId);
        postModel = destPostModel;
        destPostModel = null;
        setContentView(R.layout.activity_post_desc);
        super.onCreate(savedInstanceState);
        onRefresh();
    }

    @Override
    protected void getViews() {
        account = HCApplicaton.getInstance().getAccount();
        back = (FrameLayout) findViewById(R.id.back);
        refreshListView = (RefreshListView) findViewById(R.id.refreshListView);
        replyContent = (EditText) findViewById(R.id.replyContent);
        submit = (TextView) findViewById(R.id.submit);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void setListeners() {
        back.setOnClickListener(this);
        refreshListView.setCanRefresh(false);
        refreshListView.setCanLoadMore(false);
        refreshListView.setOnLoadListener(this);
        refreshListView.setOnRefreshListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                tryToReply();
                break;
        }
    }

    public void tryToReply() {
        showDialog();
        final String content = replyContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.makeShortText(getString(R.string.replyNotNull));
            return;
        }
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (!TextUtils.isEmpty(account.userId)) {
            nameValuePairs.add(new BasicNameValuePair("userid", account.userId));
        }
        nameValuePairs.add(new BasicNameValuePair("topicid", topicid));
        nameValuePairs.add(new BasicNameValuePair("communityid", postModel.type));
        nameValuePairs.add(new BasicNameValuePair("content", content));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_POST_REPLY, Request.POST, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                dismissDialog();
                ToastUtil.makeShortText(getString(R.string.commentFiled));
            }

            @Override
            public void onComplete(String response) {
                dismissDialog();
                String result = response;
                ReplyModel model = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, ReplyModel.class);
                if (model != null && model.result == 1) {
                    refreshCommons(content);
                } else {
                    ToastUtil.makeShortText(getString(R.string.commentFiled));
                }
            }
        });
    }

    private void refreshCommons(String content) {
        replyContent.setText("");
        CommentModel commentModel = new CommentModel();
        commentModel.content = content;
        commentModel.createTimeMs = System.currentTimeMillis() + "";
        commentModel.userAvatar = account.avatar;
        commentModel.userName = account.userName;
        commentModels.add(0, commentModel);
        commentAdapter.notifyDataSetChanged();
        postHeaderView.addCommentCount();
        if (postModel != null) {
            postModel.replyNum = Integer.valueOf(postModel.replyNum) + "";
        }
        Intent intent = new Intent();
        intent.setAction(CommunityFragment.RefreshActionFromLocal);
        sendBroadcast(intent);
    }

    @Override
    public void onLoadMore() {
        p++;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (!TextUtils.isEmpty(account.userId)) {
            nameValuePairs.add(new BasicNameValuePair("userid", account.userId));
        }
        nameValuePairs.add(new BasicNameValuePair("topicid", topicid));
        nameValuePairs.add(new BasicNameValuePair("start_num", p + ""));
        nameValuePairs.add(new BasicNameValuePair("limit", SIZE + ""));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_POST_DESC, Request.GET, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                dismissDialog();
                ToastUtil.makeShortText(getString(R.string.refreshFiled));
                refreshListView.onLoadMoreComplete();
            }

            @Override
            public void onComplete(String response) {
                dismissDialog();
                refreshListView.onLoadMoreComplete();
                PostDescModel model = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, PostDescModel.class);
                if (model != null && model.result == 1) {
                    if (model.replyPosts != null && model.replyPosts.size() > 0) {
                        if (model.replyPosts.size() < SIZE) {//说明没有了
                            refreshListView.setCanLoadMore(false);
                        } else {
                            refreshListView.setCanLoadMore(true);
                        }
                        commentModels.addAll(model.replyPosts);
                        commentAdapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.makeShortText(getString(R.string.noData));
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        showDialog(getString(R.string.refreshing));
        p = 0;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (!TextUtils.isEmpty(account.userId)) {
            nameValuePairs.add(new BasicNameValuePair("userid", account.userId));
        }
        nameValuePairs.add(new BasicNameValuePair("topicid", topicid));
        nameValuePairs.add(new BasicNameValuePair("start_num", p + ""));
        nameValuePairs.add(new BasicNameValuePair("limit", SIZE + ""));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_POST_DESC, Request.GET, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                dismissDialog();
                ToastUtil.makeShortText(getString(R.string.refreshFiled));
                refreshListView.onRefreshComplete();
            }

            @Override
            public void onComplete(String response) {
                dismissDialog();
                refreshListView.onRefreshComplete();
                PostDescModel model = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, PostDescModel.class);
                if (model != null && model.result == 1) {
                    initHeaderData(model);
                    if (model.replyPosts != null && model.replyPosts.size() > 0) {
                        if (model.replyPosts.size() < SIZE) {//说明没有了
                            refreshListView.setCanLoadMore(false);
                        } else {
                            refreshListView.setCanLoadMore(true);
                        }
                        commentModels.clear();
                        commentModels.addAll(model.replyPosts);
                        commentAdapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.makeShortText(getString(R.string.noData));
                }
            }
        });
    }

    private void initHeaderData(PostDescModel postDescModel) {
        if (postDescModel != null) {
            int headerCount = refreshListView.getHeaderViewsCount();
            if (headerCount > 0) {
                refreshListView.removeHeaderView(postHeaderView);
            }
            postHeaderView = new PostHeaderView(this);
            postHeaderView.setData(postDescModel);
            refreshListView.addHeaderView(postHeaderView);
            commentAdapter = new CommentAdapter();
            refreshListView.setAdapter(commentAdapter);
        }
    }

    class CommentAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return commentModels.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            CommonItemView itemView = null;
            if (view == null) {
                itemView = new CommonItemView(ActivityPostDesc.this);
            } else {
                itemView = (CommonItemView) view;
            }
            itemView.setData(commentModels.get(i));
            return itemView;
        }
    }

    class ReplyModel {
        @SerializedName("message")
        public String message;

        @SerializedName("result")
        public int result;
    }
}
