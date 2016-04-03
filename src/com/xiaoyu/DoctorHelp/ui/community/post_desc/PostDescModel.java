package com.xiaoyu.DoctorHelp.ui.community.post_desc;

import com.meilishuo.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xiaoyu on 2015/7/4.
 */
public class PostDescModel {
    @SerializedName("type")
    public String type;

    @SerializedName("createTimeMs")
    public String createTimeMs;

    @SerializedName("message")
    public String message;

    @SerializedName("postContent")
    public String postContent;

    @SerializedName("postFloorAvatar")
    public String postFloorAvatar;

    @SerializedName("postFloorId")
    public String postFloorId;

    @SerializedName("postFloorName")
    public String postFloorName;

    @SerializedName("replyNum")
    public String replyNum;

    @SerializedName("replyPosts")
    public List<CommentModel> replyPosts;

    @SerializedName("result")
    public int result;
}
