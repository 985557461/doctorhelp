package com.xiaoyu.DoctorHelp.ui.mycenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import com.xiaoyu.DoctorHelp.ui.community.PostItemView;
import com.xiaoyu.DoctorHelp.ui.community.PostListModel;
import com.xiaoyu.DoctorHelp.ui.community.PostModel;
import com.xiaoyu.DoctorHelp.ui.community.post_desc.ActivityPostDesc;
import com.xiaoyu.DoctorHelp.util.Request;
import com.xiaoyu.DoctorHelp.util.ToastUtil;
import com.xiaoyu.DoctorHelp.widget.RefreshListView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2015/7/6.
 */
public class ActivityMyPost extends ActivityBase implements View.OnClickListener, RefreshListView.OnLoadMoreListener, RefreshListView.OnRefreshListener{
    private LinearLayout back;
    private RefreshListView refreshListView;

    /**
     * account
     * *
     */
    private Account account;

    /**
     * adapter about
     * *
     */
    private int p = 0;
    private final int SIZE = 50;
    private List<PostModel> postModels = new ArrayList<PostModel>();
    private PostAdapter postAdapter;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivityMyPost.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_post);
        super.onCreate(savedInstanceState);
        onRefresh();
    }

    @Override
    protected void getViews() {
        account = HCApplicaton.getInstance().getAccount();
        back = (LinearLayout) findViewById(R.id.back);
        refreshListView = (RefreshListView) findViewById(R.id.refreshListView);
    }

    @Override
    protected void initViews() {
        postAdapter = new PostAdapter();
        refreshListView.setAdapter(postAdapter);
    }

    @Override
    protected void setListeners() {
        back.setOnClickListener(this);
        refreshListView.setCanRefresh(true);
        refreshListView.setCanLoadMore(false);
        refreshListView.setOnLoadListener(this);
        refreshListView.setOnRefreshListener(this);
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PostItemView itemView = (PostItemView)view;
                PostModel postModel = itemView.postModel;
                if(!TextUtils.isEmpty(postModel.postId)){
                    ActivityPostDesc.open(ActivityMyPost.this, postModel, postModel.postId);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onLoadMore() {
        p++;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("userid", account.userId));
        nameValuePairs.add(new BasicNameValuePair("start_num", p + ""));
        nameValuePairs.add(new BasicNameValuePair("limit", SIZE + ""));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_MY_POST, Request.GET, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                p--;
                ToastUtil.makeShortText(getString(R.string.refreshFiled));
                refreshListView.onLoadMoreComplete();
            }

            @Override
            public void onComplete(String response) {
                refreshListView.onLoadMoreComplete();
                PostListModel model = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, PostListModel.class);
                if (model != null && model.result == 1) {
                    if (model.posts != null && model.posts.size() > 0) {
                        if (model.posts.size() < SIZE) {//说明没有了
                            refreshListView.setCanLoadMore(false);
                        } else {
                            refreshListView.setCanLoadMore(true);
                        }
                        postModels.addAll(model.posts);
                        postAdapter.notifyDataSetChanged();
                    }
                } else {
                    p--;
                    ToastUtil.makeShortText(getString(R.string.noData));
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        p = 0;
        showDialog();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("userid", account.userId));
        nameValuePairs.add(new BasicNameValuePair("start_num", p + ""));
        nameValuePairs.add(new BasicNameValuePair("limit", SIZE + ""));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_MY_POST, Request.GET, new Request.RequestListener() {
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
                PostListModel model = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, PostListModel.class);
                if (model != null && model.result == 1) {
                    if (model.posts != null && model.posts.size() > 0) {
                        if (model.posts.size() < SIZE) {//说明没有了
                            refreshListView.setCanLoadMore(false);
                        } else {
                            refreshListView.setCanLoadMore(true);
                        }
                        postModels.clear();
                        postModels.addAll(model.posts);
                        postAdapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.makeShortText(getString(R.string.noData));
                }
            }
        });
    }

    class PostAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return postModels.size();
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
            PostItemView itemView = null;
            if (view == null) {
                itemView = new PostItemView(ActivityMyPost.this);
            } else {
                itemView = (PostItemView) view;
            }
            itemView.setData(postModels.get(i));
            return itemView;
        }
    }
}
