package com.xiaoyu.DoctorHelp.background;

import com.meilishuo.gson.annotations.SerializedName;

/**
 * Created by xiaoyu on 2015/10/28.
 */
public class UserInfoModel {
    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public int result;

    @SerializedName("imagepath")
    public String imagepath;

    @SerializedName("nickname")
    public String nickname;

    @SerializedName("userid")
    public String userid;
}
