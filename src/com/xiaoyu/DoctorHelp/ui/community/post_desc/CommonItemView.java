package com.xiaoyu.DoctorHelp.ui.community.post_desc;

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
public class CommonItemView extends FrameLayout {
    private ImageView avatar;
    private TextView content;
    private TextView name;
    private TextView time;

    private CommentModel commentModel;
    private ImageLoader imageLoader;

    public CommonItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public CommonItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        imageLoader = HCApplicaton.getInstance().getImageLoader();
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.common_item_view, this, true);

        avatar = (ImageView) findViewById(R.id.avatar);
        content = (TextView) findViewById(R.id.content);
        name = (TextView) findViewById(R.id.name);
        time = (TextView) findViewById(R.id.time);
    }

    public void setData(CommentModel commentModel) {
        this.commentModel = commentModel;
        if (commentModel == null) {
            return;
        }
        if (!TextUtils.isEmpty(commentModel.userAvatar)) {
            imageLoader.displayImage(commentModel.userAvatar, avatar, ImageLoaderUtil.Options_Memory_Rect_Avatar);
        }
        if (!TextUtils.isEmpty(commentModel.content)) {
            content.setText(getContext().getString(R.string.replyTo)+commentModel.content);
        }
        if (!TextUtils.isEmpty(commentModel.userName)) {
            name.setText(commentModel.userName);
        }
        if (!TextUtils.isEmpty(commentModel.createTimeMs)) {
            time.setText(StringUtil.getDateToString3(commentModel.createTimeMs));
        }
    }
}
