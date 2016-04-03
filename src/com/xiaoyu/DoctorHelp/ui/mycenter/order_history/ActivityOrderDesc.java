package com.xiaoyu.DoctorHelp.ui.mycenter.order_history;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.CommonModel;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import com.xiaoyu.DoctorHelp.util.ImageLoaderUtil;
import com.xiaoyu.DoctorHelp.util.Request;
import com.xiaoyu.DoctorHelp.util.ToastUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2015/7/25.
 */
public class ActivityOrderDesc extends ActivityBase implements View.OnClickListener {
    private FrameLayout back;
    private ImageView avatar;
    private TextView doctorName;
    private TextView jianJie;
    private TextView userName;
    private TextView phoneNumber;
    private TextView address;
    private TextView yuYueTime;
    private ImageLoader imageLoader;
    private TextView orderStatus;
    private TextView jieDan;

    private static OrderItemModel destOrderItemModel;
    private OrderItemModel orderItemModel;

    public static void open(Activity activity, OrderItemModel orderItemModel) {
        destOrderItemModel = orderItemModel;
        Intent intent = new Intent(activity, ActivityOrderDesc.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        orderItemModel = destOrderItemModel;
        destOrderItemModel = null;
        setContentView(R.layout.activity_order_desc);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getViews() {
        imageLoader = HCApplicaton.getInstance().getImageLoader();
        back = (FrameLayout) findViewById(R.id.back);
        orderStatus = (TextView) findViewById(R.id.orderStatus);
        avatar = (ImageView) findViewById(R.id.avatar);
        doctorName = (TextView) findViewById(R.id.doctorName);
        jianJie = (TextView) findViewById(R.id.jianJie);
        userName = (TextView) findViewById(R.id.userName);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        address = (TextView) findViewById(R.id.address);
        yuYueTime = (TextView) findViewById(R.id.yuYueTime);
        jieDan = (TextView) findViewById(R.id.jieDan);
    }

    @Override
    protected void initViews() {
        if (orderItemModel != null) {
            if (!TextUtils.isEmpty(orderItemModel.doctorimg)) {
                imageLoader.displayImage(orderItemModel.doctorimg, avatar, ImageLoaderUtil.Options_Common_memory_Pic);
            } else {
                imageLoader.displayImage("", avatar, ImageLoaderUtil.Options_Common_Disc_Pic);
            }
            if (!TextUtils.isEmpty(orderItemModel.dotorname)) {
                doctorName.setText(orderItemModel.dotorname);
            }
            if (!TextUtils.isEmpty(orderItemModel.doctorbrief)) {
                jianJie.setText(orderItemModel.doctorbrief);
            } else {
                jianJie.setText(getString(R.string.noJianJie));
            }
            if (!TextUtils.isEmpty(orderItemModel.username)) {
                userName.setText(orderItemModel.username);
            }
            if (!TextUtils.isEmpty(orderItemModel.userphone)) {
                phoneNumber.setText(orderItemModel.userphone);
            }
            if (!TextUtils.isEmpty(orderItemModel.address)) {
                address.setText(orderItemModel.address);
            }
            if (!TextUtils.isEmpty(orderItemModel.apptime)) {
                yuYueTime.setText(orderItemModel.apptime);
            }
            if (!TextUtils.isEmpty(orderItemModel.status) && orderItemModel.status.equals("0")) {
                orderStatus.setText(getString(R.string.orderstatusone));
            } else if (!TextUtils.isEmpty(orderItemModel.status) && orderItemModel.status.equals("1")) {
                orderStatus.setText(getString(R.string.orderstatustwo));
            } else if (!TextUtils.isEmpty(orderItemModel.status) && orderItemModel.status.equals("2")) {
                orderStatus.setText(getString(R.string.orderstatusthree));
            }
            if (!TextUtils.isEmpty(orderItemModel.status) && orderItemModel.status.equals("0")) {
                jieDan.setVisibility(View.VISIBLE);
            } else {
                jieDan.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void setListeners() {
        back.setOnClickListener(this);
        jieDan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.jieDan:
                tryToJieDan();
                break;
        }
    }

    private void tryToJieDan() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("id", orderItemModel.id));
        nameValuePairs.add(new BasicNameValuePair("type", "1"));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_ORDER_UPDATE_STATUS, Request.GET, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                ToastUtil.makeShortText(getString(R.string.jiedanfiled));
            }

            @Override
            public void onComplete(String response) {
                CommonModel model = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, CommonModel.class);
                if (model != null && model.result == 1) {
                    ToastUtil.makeShortText(getString(R.string.jiedansucc));
                    finish();
                } else {
                    ToastUtil.makeShortText(getString(R.string.jiedanfiled));
                }
            }
        });
    }
}
