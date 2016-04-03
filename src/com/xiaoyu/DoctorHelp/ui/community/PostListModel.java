package com.xiaoyu.DoctorHelp.ui.community;

import com.meilishuo.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xiaoyu on 2015/7/4.
 */
public class PostListModel {
    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public int result;

    @SerializedName("posts")
    public List<PostModel> posts;
}
