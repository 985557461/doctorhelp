package com.xiaoyu.DoctorHelp.ui.mycenter.order_history;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2015/7/21.
 */
public class ActivityOrderHistory extends ActivityBase implements View.OnClickListener {
    private FrameLayout back;
    private TextView notCompleted;
    private TextView completed;
    private View bottomLineOne;
    private View bottomLineTwo;
    private ViewPager viewPager;

    /**
     * viewpager about
     * *
     */
    private OrderNotCompView orderNotCompView;
    private OrderCompView orderCompView;
    private List<View> views = new ArrayList<View>();
    private MyPagerAdapter myPagerAdapter;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivityOrderHistory.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_history);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getViews() {
        back = (FrameLayout) findViewById(R.id.back);
        notCompleted = (TextView) findViewById(R.id.notCompleted);
        completed = (TextView) findViewById(R.id.completed);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        bottomLineOne = findViewById(R.id.bottomLineOne);
        bottomLineTwo = findViewById(R.id.bottomLineTwo);
    }

    @Override
    protected void initViews() {
        notCompleted.setSelected(true);
        bottomLineOne.setVisibility(View.VISIBLE);
        bottomLineTwo.setVisibility(View.GONE);
        orderNotCompView = new OrderNotCompView(this);
        orderCompView = new OrderCompView(this);
        views.add(orderNotCompView);
        views.add(orderCompView);

        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orderNotCompView != null) {
            orderNotCompView.onRefresh();
        }
        if (orderCompView != null) {
            orderCompView.onRefresh();
        }
    }

    @Override
    protected void setListeners() {
        back.setOnClickListener(this);
        notCompleted.setOnClickListener(this);
        completed.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.notCompleted:
                notCompleted.setSelected(true);
                completed.setSelected(false);
                bottomLineOne.setVisibility(View.VISIBLE);
                bottomLineTwo.setVisibility(View.GONE);
                viewPager.setCurrentItem(0);
                break;
            case R.id.completed:
                notCompleted.setSelected(false);
                completed.setSelected(true);
                bottomLineOne.setVisibility(View.GONE);
                bottomLineTwo.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(1);
                break;
        }
    }

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            switch (i) {
                case 0:
                    notCompleted.setSelected(true);
                    completed.setSelected(false);
                    bottomLineOne.setVisibility(View.VISIBLE);
                    bottomLineTwo.setVisibility(View.GONE);
                    break;
                case 1:
                    notCompleted.setSelected(false);
                    completed.setSelected(true);
                    bottomLineOne.setVisibility(View.GONE);
                    bottomLineTwo.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
}
