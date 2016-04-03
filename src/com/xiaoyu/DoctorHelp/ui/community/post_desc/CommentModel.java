package com.xiaoyu.DoctorHelp.ui.community.post_desc;

import com.meilishuo.gson.annotations.SerializedName;

/**
 * Created by xiaoyu on 2015/7/4.
 */
public class CommentModel {
    @SerializedName("communityid")
    public String communityid;

    @SerializedName("content")
    public String content;

    @SerializedName("createTimeMs")
    public String createTimeMs;

    @SerializedName("userAvatar")
    public String userAvatar;

    @SerializedName("userName")
    public String userName;
}
