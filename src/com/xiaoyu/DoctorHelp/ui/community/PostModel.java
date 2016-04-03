package com.xiaoyu.DoctorHelp.ui.community;

import com.meilishuo.gson.annotations.SerializedName;

/**
 * Created by xiaoyu on 2015/7/4.
 */
public class PostModel {
    @SerializedName("content")
    public String content;

    @SerializedName("createTimeMs")
    public String createTimeMs;

    @SerializedName("postId")
    public String postId;

    @SerializedName("postImages")
    public String postImages;

    @SerializedName("postTitle")
    public String postTitle;

    @SerializedName("replyNum")
    public String replyNum;

    @SerializedName("type")
    public String type;

    @SerializedName("userAvatar")
    public String userAvatar;

    @SerializedName("userId")
    public String userId;

    @SerializedName("userName")
    public String userName;

    @SerializedName("userPhoneNum")
    public String userPhoneNum;
}
