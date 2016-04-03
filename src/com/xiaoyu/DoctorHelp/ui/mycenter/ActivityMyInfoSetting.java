package com.xiaoyu.DoctorHelp.ui.mycenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.meilishuo.gson.annotations.SerializedName;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import com.xiaoyu.DoctorHelp.util.ImageLoaderUtil;
import com.xiaoyu.DoctorHelp.util.IntentUtils;
import com.xiaoyu.DoctorHelp.util.Request;
import com.xiaoyu.DoctorHelp.util.ToastUtil;
import com.xiaoyu.DoctorHelp.widget.cropimage.ActivityCropImage;
import com.xiaoyu.DoctorHelp.widget.photo.PhotoActivity;
import com.xiaoyu.DoctorHelp.widget.photo.PhotoAlbumActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2015/7/6.
 */
public class ActivityMyInfoSetting extends ActivityBase implements View.OnClickListener {
    private LinearLayout back;
    private RelativeLayout avatarRL;
    private ImageView avatar;
    private TextView notSetAvatar;
    private RelativeLayout nickNameRL;
    private TextView nickName;
    private TextView notSetNickname;
    private RelativeLayout phoneNumberRL;
    private TextView phoneNumber;
    private RelativeLayout pwdRL;


    /**
     * account
     * *
     */
    private Account account;

    /**
     * imageloader
     * *
     */
    private ImageLoader imageLoader;

    /**
     * modify avatar about
     * *
     */
    private static final int kActivitySettingSelectPicRequest = 101;
    private static final int kPhotoCropImageRequest = 102;
    private String avatarPath = "";

    /**
     * modify nickname
     * *
     */
    private static final int kActivitySettingModifyNickName = 103;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivityMyInfoSetting.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_myinfo_setting);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getViews() {
        imageLoader = HCApplicaton.getInstance().getImageLoader();
        account = HCApplicaton.getInstance().getAccount();
        back = (LinearLayout) findViewById(R.id.back);
        avatarRL = (RelativeLayout) findViewById(R.id.avatarRL);
        avatar = (ImageView) findViewById(R.id.avatar);
        notSetAvatar = (TextView) findViewById(R.id.notSetAvatar);
        nickNameRL = (RelativeLayout) findViewById(R.id.nickNameRL);
        nickName = (TextView) findViewById(R.id.nickName);
        notSetNickname = (TextView) findViewById(R.id.notSetNickname);
        phoneNumberRL = (RelativeLayout) findViewById(R.id.phoneNumberRL);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        pwdRL = (RelativeLayout) findViewById(R.id.pwdRL);
    }

    @Override
    protected void initViews() {
        if (!TextUtils.isEmpty(account.avatar)) {
            imageLoader.displayImage(account.avatar, avatar, ImageLoaderUtil.Options_Memory_Rect_Avatar);
            notSetAvatar.setVisibility(View.INVISIBLE);
        } else {
            imageLoader.displayImage("", avatar, ImageLoaderUtil.Options_Memory_Rect_Avatar);
            notSetAvatar.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(account.userName)) {
            nickName.setText(getString(R.string.nickName) + account.userName);
            notSetNickname.setVisibility(View.INVISIBLE);
        } else {
            nickName.setText(getString(R.string.nickName));
        }
        phoneNumber.setText(account.phoneNumber);
    }

    @Override
    protected void setListeners() {
        back.setOnClickListener(this);
        avatarRL.setOnClickListener(this);
        nickNameRL.setOnClickListener(this);
        pwdRL.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.avatarRL:
                Intent intent = IntentUtils.goToAlbumIntent(new ArrayList<String>(), 1, getResources().getString(R.string.confirm), true, ActivityMyInfoSetting.this);
                startActivityForResult(intent, kActivitySettingSelectPicRequest);
                break;
            case R.id.nickNameRL:
                ActivityModifyNickname.open(this, kActivitySettingModifyNickName);
                break;
            case R.id.pwdRL:
                ActivityModifyPassword.open(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == kActivitySettingSelectPicRequest && resultCode == RESULT_OK) {
            String[] paths = data.getStringArrayExtra(PhotoAlbumActivity.Key_SelectPaths);
            if (paths != null && paths.length <= 0) {
                return;
            }
            if (data.getStringExtra(PhotoActivity.kWhereFrom).equals(PhotoActivity.kFromAlbum)) {
                if (!TextUtils.isEmpty(paths[0])) {
                    ActivityCropImage.openForResult(this, paths[0], 750, 750, true, kPhotoCropImageRequest);
                    return;
                }
            } else if (data.getStringExtra(PhotoActivity.kWhereFrom).equals(PhotoActivity.kFromCamera)) {
                if (!TextUtils.isEmpty(paths[0])) {
                    avatarPath = paths[0];
                    uploadImage();
                }
            }
        } else if (requestCode == kPhotoCropImageRequest && resultCode == RESULT_OK) {
            avatarPath = data.getStringExtra(ActivityCropImage.kCropImagePath);
            uploadImage();
            return;
        } else if (requestCode == kActivitySettingModifyNickName && resultCode == RESULT_OK) {
            if (!TextUtils.isEmpty(account.userName)) {
                nickName.setText(getString(R.string.nickName) + account.userName);
                notSetNickname.setVisibility(View.INVISIBLE);
            } else {
                nickName.setText(getString(R.string.nickName));
            }
        }
    }

    private void uploadImage() {
        showDialog();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("phoneNumber", account.phoneNumber));
        Request.doFileUploadRequest(this, nameValuePairs, new File(avatarPath), ServerConfig.URL_MODIFY_AVATAR, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                dismissDialog();
                ToastUtil.makeShortText(getString(R.string.modifyField));
            }

            @Override
            public void onComplete(String response) {
                dismissDialog();
                ModifyAvatarModel modifyAvatarModel = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, ModifyAvatarModel.class);
                if (modifyAvatarModel != null && !TextUtils.isEmpty(modifyAvatarModel.result)) {
                    account.avatar = modifyAvatarModel.result;
                    account.saveMeInfoToPreference();
                    if (!TextUtils.isEmpty(account.avatar)) {
                        imageLoader.displayImage(account.avatar, avatar, ImageLoaderUtil.Options_Memory_Rect_Avatar);
                        notSetAvatar.setVisibility(View.INVISIBLE);
                    } else {
                        imageLoader.displayImage("", avatar, ImageLoaderUtil.Options_Memory_Rect_Avatar);
                        notSetAvatar.setVisibility(View.VISIBLE);
                    }
                    ToastUtil.makeShortText(getString(R.string.modifySuccessful));
                } else {
                    ToastUtil.makeShortText(getString(R.string.modifyField));
                }
            }
        });
    }

    class ModifyAvatarModel {
        @SerializedName("message")
        public String message;
        @SerializedName("result")
        public String result;
    }
}
