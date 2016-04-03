package com.xiaoyu.DoctorHelp.ui.mycenter.order_history;

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

/**
 * Created by xiaoyu on 2015/7/8.
 */
public class OrderNotCompItemView extends FrameLayout {
    private ImageView avatar;
    private TextView title;
    private TextView name;
    private ImageLoader imageLoader;

    public OrderItemModel model;

    public OrderNotCompItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public OrderNotCompItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderNotCompItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        imageLoader = HCApplicaton.getInstance().getImageLoader();
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.order_not_comp_item_view, this, true);

        avatar = (ImageView) findViewById(R.id.avatar);
        title = (TextView) findViewById(R.id.title);
        name = (TextView) findViewById(R.id.name);
    }

    public void setData(OrderItemModel model) {
        this.model = model;
        if (model == null) {
            return;
        }
        if (!TextUtils.isEmpty(model.doctorimg)) {
            imageLoader.displayImage(model.doctorimg, avatar, ImageLoaderUtil.Options_Common_Disc_Pic);
        }
        if (!TextUtils.isEmpty(model.apptime)) {
            title.setText(model.apptime);
        } else {
            title.setText(getContext().getString(R.string.order));
        }
        if (!TextUtils.isEmpty(model.dotorname)) {
            name.setText(model.dotorname);
        }
    }
}
