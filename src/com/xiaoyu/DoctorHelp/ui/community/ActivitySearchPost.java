package com.xiaoyu.DoctorHelp.ui.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import com.xiaoyu.DoctorHelp.util.Request;
import com.xiaoyu.DoctorHelp.util.ToastUtil;
import com.xiaoyu.DoctorHelp.widget.HCPopListView;
import com.xiaoyu.DoctorHelp.widget.RefreshListView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2015/7/5.
 */
public class ActivitySearchPost extends ActivityBase implements View.OnClickListener, RefreshListView.OnLoadMoreListener, RefreshListView.OnRefreshListener {
    private LinearLayout back;
    private LinearLayout questionType;
    private TextView typeStr;
    private RefreshListView refreshListView;

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

    /**
     * adapter about
     * *
     */
    private int p = 0;
    private final int SIZE = 50;
    private List<PostModel> postModels = new ArrayList<PostModel>();
    private PostAdapter postAdapter;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivitySearchPost.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_post);
        super.onCreate(savedInstanceState);
        onRefresh();
    }

    @Override
    protected void getViews() {
        account = HCApplicaton.getInstance().getAccount();
        back = (LinearLayout) findViewById(R.id.back);
        questionType = (LinearLayout) findViewById(R.id.questionType);
        typeStr = (TextView) findViewById(R.id.typeStr);
        refreshListView = (RefreshListView) findViewById(R.id.refreshListView);
    }

    @Override
    protected void initViews() {
        items.add(getResources().getString(R.string.zhili));
        items.add(getResources().getString(R.string.xuexi));
        items.add(getResources().getString(R.string.qinzi));
        items.add(getResources().getString(R.string.shejiao));
        items.add(getResources().getString(R.string.qingchunqi));

        postAdapter = new PostAdapter();
        refreshListView.setAdapter(postAdapter);
    }

    @Override
    protected void setListeners() {
        back.setOnClickListener(this);
        questionType.setOnClickListener(this);
        refreshListView.setCanRefresh(true);
        refreshListView.setCanLoadMore(false);
        refreshListView.setOnLoadListener(this);
        refreshListView.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.questionType:
                showPopListViewDlg();
                break;
        }
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
        new HCPopListView(this, getString(R.string.searchType), getString(R.string.cancel), items, new HCPopListView.HCPopListViewListener() {
            @Override
            public void onItemClicked(int index, String content) {
                communityid = index;
                typeStr.setText(content);
                onRefresh();
            }

            @Override
            public void onCancelClicked() {

            }
        }).show();
    }

    @Override
    public void onLoadMore() {
        p++;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("communityid", communityid + ""));
        nameValuePairs.add(new BasicNameValuePair("start_num", p + ""));
        nameValuePairs.add(new BasicNameValuePair("limit", SIZE + ""));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_GET_POSTS, Request.GET, new Request.RequestListener() {
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
        nameValuePairs.add(new BasicNameValuePair("communityid", communityid + ""));
        nameValuePairs.add(new BasicNameValuePair("start_num", p + ""));
        nameValuePairs.add(new BasicNameValuePair("limit", SIZE + ""));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_GET_POSTS, Request.GET, new Request.RequestListener() {
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
                itemView = new PostItemView(ActivitySearchPost.this);
            } else {
                itemView = (PostItemView) view;
            }
            itemView.setData(postModels.get(i));
            return itemView;
        }
    }
}
