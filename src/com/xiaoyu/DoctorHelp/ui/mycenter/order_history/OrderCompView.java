package com.xiaoyu.DoctorHelp.ui.mycenter.order_history;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import com.xiaoyu.DoctorHelp.util.Request;
import com.xiaoyu.DoctorHelp.util.ToastUtil;
import com.xiaoyu.DoctorHelp.widget.RefreshListView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2015/7/8.
 */
public class OrderCompView extends FrameLayout implements RefreshListView.OnLoadMoreListener, RefreshListView.OnRefreshListener{
    private RefreshListView refreshListView;

    private int p = 0;
    private final int SIZE = 50;
    private List<OrderItemModel> models = new ArrayList<OrderItemModel>();
    private MyAdapter myAdapter;

    private ActivityBase activity;
    private Account account;

    public OrderCompView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public OrderCompView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderCompView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        account = HCApplicaton.getInstance().getAccount();
        this.activity = (ActivityBase) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.order_comp_view, this, true);
        refreshListView = (RefreshListView) findViewById(R.id.refreshListView);
        refreshListView.setCanRefresh(true);
        refreshListView.setCanLoadMore(false);
        refreshListView.setOnLoadListener(this);
        refreshListView.setOnRefreshListener(this);
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OrderCompItemView itemView = (OrderCompItemView) view;
                OrderItemModel model = itemView.model;
                if (model != null) {
                    ActivityOrderDesc.open(activity,model);
                }
            }
        });
        myAdapter = new MyAdapter();
        refreshListView.setAdapter(myAdapter);
    }

    @Override
    public void onLoadMore() {
        p++;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("userId", ""));
        nameValuePairs.add(new BasicNameValuePair("doctorid", account.userId));
        nameValuePairs.add(new BasicNameValuePair("type", "1"));
        nameValuePairs.add(new BasicNameValuePair("start_num", p + ""));
        nameValuePairs.add(new BasicNameValuePair("limit", SIZE + ""));
        Request.doRequest(activity, nameValuePairs, ServerConfig.URL_GET_ORDERS, Request.GET, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                p--;
                ToastUtil.makeShortText(activity.getString(R.string.refreshFiled));
                refreshListView.onLoadMoreComplete();
            }

            @Override
            public void onComplete(String response) {
                refreshListView.onLoadMoreComplete();
                OrderListModel model = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, OrderListModel.class);
                if (model != null && model.result == 1) {
                    if (model.list != null && model.list.size() > 0) {
                        if (model.list.size() < SIZE) {//说明没有了
                            refreshListView.setCanLoadMore(false);
                        } else {
                            refreshListView.setCanLoadMore(true);
                        }
                        models.addAll(model.list);
                        myAdapter.notifyDataSetChanged();
                    }
                } else {
                    p--;
                    ToastUtil.makeShortText(activity.getString(R.string.noData));
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        p = 0;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("userId", ""));
        nameValuePairs.add(new BasicNameValuePair("doctorid", account.userId));
        nameValuePairs.add(new BasicNameValuePair("type", "1"));
        nameValuePairs.add(new BasicNameValuePair("start_num", p + ""));
        nameValuePairs.add(new BasicNameValuePair("limit", SIZE + ""));
        Request.doRequest(activity, nameValuePairs, ServerConfig.URL_GET_ORDERS, Request.GET, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                ToastUtil.makeShortText(activity.getString(R.string.refreshFiled));
                refreshListView.onRefreshComplete();
            }

            @Override
            public void onComplete(String response) {
                refreshListView.onRefreshComplete();
                OrderListModel model = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, OrderListModel.class);
                if (model != null && model.result == 1) {
                    if (model.list != null && model.list.size() > 0) {
                        if (model.list.size() < SIZE) {//说明没有了
                            refreshListView.setCanLoadMore(false);
                        } else {
                            refreshListView.setCanLoadMore(true);
                        }
                        models.clear();
                        models.addAll(model.list);
                        myAdapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.makeShortText(activity.getString(R.string.noData));
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return models.size();
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
            OrderCompItemView itemView = null;
            if (view == null) {
                itemView = new OrderCompItemView(activity);
            } else {
                itemView = (OrderCompItemView) view;
            }
            itemView.setData(models.get(i));
            return itemView;
        }
    }
}
