package com.xiaoyu.DoctorHelp.ui.mycenter.order_history;

import com.meilishuo.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xiaoyu on 2015/7/8.
 */
public class OrderListModel {
    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public int result;

    @SerializedName("list")
    public List<OrderItemModel> list;
}
