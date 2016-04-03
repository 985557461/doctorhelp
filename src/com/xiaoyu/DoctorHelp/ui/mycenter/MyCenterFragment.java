package com.xiaoyu.DoctorHelp.ui.mycenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.easemob.EMCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.ui.account.ActivityLogin;
import com.xiaoyu.DoctorHelp.ui.mycenter.order_history.ActivityOrderHistory;
import com.xiaoyu.DoctorHelp.util.ImageLoaderUtil;
import com.xiaoyu.DoctorHelp.widget.HCAlertDlgNoTitle;

/**
 * Created by xiaoyu on 2015/7/1.
 */
public class MyCenterFragment extends Fragment implements View.OnClickListener {
    private ImageView avatar;
    private TextView phoneNumber;
    private LinearLayout questionHistory;
    private LinearLayout yuYueHistory;
    private LinearLayout myInfoSetting;
    private TextView logout;

    private Account account;
    private ImageLoader imageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_center_fragment, container, false);
        initViewAndListener(view);
        return view;
    }

    private void initViewAndListener(View view) {
        account = HCApplicaton.getInstance().getAccount();
        imageLoader = HCApplicaton.getInstance().getImageLoader();
        avatar = (ImageView) view.findViewById(R.id.avatar);
        phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
        questionHistory = (LinearLayout) view.findViewById(R.id.questionHistory);
        yuYueHistory = (LinearLayout) view.findViewById(R.id.yuYueHistory);
        myInfoSetting = (LinearLayout) view.findViewById(R.id.myInfoSetting);
        logout = (TextView) view.findViewById(R.id.logout);

        questionHistory.setOnClickListener(this);
        yuYueHistory.setOnClickListener(this);
        myInfoSetting.setOnClickListener(this);
        logout.setOnClickListener(this);

        if (!TextUtils.isEmpty(account.avatar)) {
            imageLoader.displayImage(account.avatar, avatar, ImageLoaderUtil.Options_Memory_Rect_Avatar);
        } else {
            imageLoader.displayImage("", avatar, ImageLoaderUtil.Options_Memory_Rect_Avatar);
        }
        if (!TextUtils.isEmpty(account.phoneNumber)) {
            phoneNumber.setText(getActivity().getString(R.string.accountStr) + account.phoneNumber);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.questionHistory:
                ActivityMyPost.open(getActivity());
                break;
            case R.id.yuYueHistory:
                ActivityOrderHistory.open(getActivity());
                break;
            case R.id.myInfoSetting:
                ActivityMyInfoSetting.open(getActivity());
                break;
            case R.id.logout:
                tryToLogout();
                break;
        }
    }

    private void tryToLogout() {
        String stre = getString(R.string.areYouSureLogout);
        HCAlertDlgNoTitle.showDlg("", stre, getActivity(), new HCAlertDlgNoTitle.HCAlertDlgClickListener() {
            @Override
            public void onAlertDlgClicked(boolean isConfirm) {
                if (isConfirm) {
                    HCApplicaton.getInstance().logout(new EMCallBack() {

                        @Override
                        public void onSuccess() {
                            account.clearMeInfo();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), ActivityLogin.class);
                                    getActivity().startActivity(intent);
                                }
                            });
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }

                        @Override
                        public void onError(int code, String message) {

                        }
                    });
                }
            }
        });
    }
}
