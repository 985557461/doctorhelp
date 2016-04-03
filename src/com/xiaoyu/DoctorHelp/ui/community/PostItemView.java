package com.xiaoyu.DoctorHelp.ui.community;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.util.ImageLoaderUtil;
import com.xiaoyu.DoctorHelp.util.StringUtil;

/**
 * Created by xiaoyu on 2015/7/4.
 */
public class PostItemView extends FrameLayout {
    private ImageView avatar;
    private TextView name;
    private TextView time;
    private TextView content;
    private TextView type;//智力 1   学习 2  亲子 3 社交 4  青春期 5
    private TextView commentCount;

    public PostModel postModel;
    private ImageLoader imageLoader;

    public PostItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PostItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PostItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        imageLoader = HCApplicaton.getInstance().getImageLoader();
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.post_item_view, this, true);

        avatar = (ImageView) findViewById(R.id.avatar);
        name = (TextView) findViewById(R.id.name);
        time = (TextView) findViewById(R.id.time);
        content = (TextView) findViewById(R.id.content);
        type = (TextView) findViewById(R.id.type);
        commentCount = (TextView) findViewById(R.id.commentCount);
    }

    public void setData(PostModel postModel) {
        this.postModel = postModel;

        if (postModel == null) {
            return;
        }

        if (!TextUtils.isEmpty(postModel.userAvatar)) {
            imageLoader.displayImage(postModel.userAvatar, avatar, ImageLoaderUtil.Options_Memory_Rect_Avatar);
        }
        if (!TextUtils.isEmpty(postModel.userName)) {
            name.setText(postModel.userName);
        }
        if (!TextUtils.isEmpty(postModel.createTimeMs)) {
            time.setText(StringUtil.getTimeLineTime(postModel.createTimeMs));
        }
        if (!TextUtils.isEmpty(postModel.content)) {
            content.setText(postModel.content);
        }
        type.setText(setTypeDesc(postModel.type));
        if (!TextUtils.isEmpty(postModel.replyNum)) {
            commentCount.setText(postModel.replyNum);
        }
    }

    private String setTypeDesc(String type) {
        if (TextUtils.isEmpty(type)) {
            return "";
        }
        if (type.equals("1")) {
            return getContext().getString(R.string.zhili);
        } else if (type.equals("2")) {
            return getContext().getString(R.string.xuexi);
        } else if (type.equals("3")) {
            return getContext().getString(R.string.qinzi);
        } else if (type.equals("4")) {
            return getContext().getString(R.string.shejiao);
        } else if (type.equals("5")) {
            return getContext().getString(R.string.qingchunqi);
        }
        return "";
    }
}
