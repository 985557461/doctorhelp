package com.xiaoyu.DoctorHelp.ui.community;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.ui.account.ActivityLogin;
import com.xiaoyu.DoctorHelp.ui.community.post_desc.ActivityPostDesc;
import com.xiaoyu.DoctorHelp.util.Request;
import com.xiaoyu.DoctorHelp.util.ToastUtil;
import com.xiaoyu.DoctorHelp.widget.RefreshListView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2015/7/1.
 */
public class CommunityFragment extends Fragment implements View.OnClickListener, RefreshListView.OnLoadMoreListener, RefreshListView.OnRefreshListener {
    private LinearLayout postQuestion;
    private LinearLayout searchPost;
    private RefreshListView refreshListView;

    private int p = 0;
    private final int SIZE = 50;
    private List<PostModel> postModels = new ArrayList<PostModel>();
    private PostAdapter postAdapter;
    private Account account;

    /**
     * broadcastReceiver
     * **
     */
    private RefreshBroadcastReceiver receiver;
    public static String RefreshActionFromServer = "refresh_action_from_server";
    public static String RefreshActionFromLocal = "refresh_action_from_local";
    private boolean refreshLocal = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community_fragment, container, false);
        initViewsAndListeners(view);
        registerReceiver();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshLocal) {
            refreshLocal = false;
            if (postAdapter != null) {
                postAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        super.onDestroy();
    }

    private void initViewsAndListeners(View view) {
        account = HCApplicaton.getInstance().getAccount();
        postQuestion = (LinearLayout) view.findViewById(R.id.postQuestion);
        searchPost = (LinearLayout) view.findViewById(R.id.searchPost);
        refreshListView = (RefreshListView) view.findViewById(R.id.refreshListView);
        refreshListView.setCanRefresh(true);
        refreshListView.setCanLoadMore(false);
        refreshListView.setOnLoadListener(this);
        refreshListView.setOnRefreshListener(this);

        postQuestion.setOnClickListener(this);
        searchPost.setOnClickListener(this);
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PostItemView itemView = (PostItemView) view;
                if (itemView.postModel != null) {
                    ActivityPostDesc.open(getActivity(), itemView.postModel, itemView.postModel.postId);
                }
            }
        });

        postAdapter = new PostAdapter();
        refreshListView.setAdapter(postAdapter);
        onRefresh();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.postQuestion:
                if (TextUtils.isEmpty(account.userId)) {
                    ActivityLogin.open(getActivity());
                    return;
                }
                ActivityPostQuestion.open(getActivity());
                break;
            case R.id.searchPost:
                ActivitySearchPost.open(getActivity());
                break;
        }
    }

    @Override
    public void onLoadMore() {
        p++;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("communityid", "0"));
        nameValuePairs.add(new BasicNameValuePair("start_num", p + ""));
        nameValuePairs.add(new BasicNameValuePair("limit", SIZE + ""));
        Request.doRequest(getActivity(), nameValuePairs, ServerConfig.URL_GET_POSTS, Request.GET, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                p--;
                ToastUtil.makeShortText(getActivity().getString(R.string.refreshFiled));
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
                    ToastUtil.makeShortText(getActivity().getString(R.string.noData));
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        p = 0;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("communityid", "0"));
        nameValuePairs.add(new BasicNameValuePair("start_num", p + ""));
        nameValuePairs.add(new BasicNameValuePair("limit", SIZE + ""));
        Request.doRequest(getActivity(), nameValuePairs, ServerConfig.URL_GET_POSTS, Request.GET, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                ToastUtil.makeShortText(getActivity().getString(R.string.refreshFiled));
                refreshListView.onRefreshComplete();
            }

            @Override
            public void onComplete(String response) {
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
                    ToastUtil.makeShortText(getActivity().getString(R.string.noData));
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
                itemView = new PostItemView(getActivity());
            } else {
                itemView = (PostItemView) view;
            }
            itemView.setData(postModels.get(i));
            return itemView;
        }
    }

    private void registerReceiver() {
        receiver = new RefreshBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RefreshActionFromServer);
        filter.addAction(RefreshActionFromLocal);
        getActivity().registerReceiver(receiver, filter);
    }

    private void unRegisterReceiver() {
        getActivity().unregisterReceiver(receiver);
    }

    class RefreshBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(RefreshActionFromServer)) {
                onRefresh();
            } else if (!TextUtils.isEmpty(action) && action.equals(RefreshActionFromLocal)) {
                refreshLocal = true;
            }
        }
    }
}
