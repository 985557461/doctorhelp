package com.xiaoyu.DoctorHelp.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.meilishuo.gson.annotations.SerializedName;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.background.Account;
import com.xiaoyu.DoctorHelp.background.HCApplicaton;
import com.xiaoyu.DoctorHelp.background.config.ServerConfig;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import com.xiaoyu.DoctorHelp.util.*;
import com.xiaoyu.DoctorHelp.widget.cropimage.ActivityCropImage;
import com.xiaoyu.DoctorHelp.widget.photo.PhotoActivity;
import com.xiaoyu.DoctorHelp.widget.photo.PhotoAlbumActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyuPC on 2015/6/7.
 */
public class ActivityRegister extends ActivityBase implements View.OnClickListener {

    private Account account;
    /**
     * init views
     * *
     */
    private FrameLayout backFL;
    private TextView commit;
    private RelativeLayout avatarRL;
    private ImageView avatar;
    private EditText phoneNumber;
    private EditText password;
    private EditText name;
    private EditText doctorTitle;
    private EditText goodAt;
    private EditText brief;
    private EditText hospital;
    private FrameLayout xueLiFL;
    private ImageView xueLiImage;
    private FrameLayout ziXunShiFL;
    private ImageView ziXunShiImage;
    private FrameLayout shenFenFL;
    private ImageView shenFenImage;
    private TextView readXieYi;

    /**
     * path
     * *
     */
    private String avatarStr = null;
    private String xueLiStr = null;
    private String ziXunShiStr = null;
    private String shenFenStr = null;
    private String avatarServerPath = null;
    private String xueLiServerPath = null;
    private String zixunShiServerPath = null;
    private String shenFenServerStr = null;
    private Bitmap avatarBmp;
    private Bitmap xueLiBmp;
    private Bitmap ziXunShiBmp;
    private Bitmap shenFenBmp;
    public static int requestCodeAvatar = 1001;
    public static int requestCodeXueLi = 1002;
    public static int requestCodeZiXunShi = 1003;
    public static int requestCodeShenFen = 1004;
    public static int requestCodeAvatarCrop = 1005;
    public static int requestCodeXueLiCrop = 1006;
    public static int requestCodeZiXunShiCrop = 1007;
    public static int requestCodeShenFenCrop = 1008;


    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivityRegister.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register_hc);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getViews() {
        account = HCApplicaton.getInstance().getAccount();
        backFL = (FrameLayout) findViewById(R.id.backFL);
        commit = (TextView) findViewById(R.id.commit);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        doctorTitle = (EditText) findViewById(R.id.doctorTitle);
        goodAt = (EditText) findViewById(R.id.goodAt);
        brief = (EditText) findViewById(R.id.brief);
        hospital = (EditText) findViewById(R.id.hospital);

        avatarRL = (RelativeLayout) findViewById(R.id.avatarRL);
        avatar = (ImageView) findViewById(R.id.avatar);
        xueLiFL = (FrameLayout) findViewById(R.id.xueLiFL);
        xueLiImage = (ImageView) findViewById(R.id.xueLiImage);
        ziXunShiFL = (FrameLayout) findViewById(R.id.ziXunShiFL);
        ziXunShiImage = (ImageView) findViewById(R.id.ziXunShiImage);
        shenFenFL = (FrameLayout) findViewById(R.id.shenFenFL);
        shenFenImage = (ImageView) findViewById(R.id.shenFenImage);
        readXieYi = (TextView) findViewById(R.id.readXieYi);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void setListeners() {
        backFL.setOnClickListener(this);
        commit.setOnClickListener(this);
        avatarRL.setOnClickListener(this);
        xueLiFL.setOnClickListener(this);
        ziXunShiFL.setOnClickListener(this);
        shenFenFL.setOnClickListener(this);
        readXieYi.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backFL:
                finish();
                break;
            case R.id.commit:
                tryToRegister();
                break;
            case R.id.avatarRL:
                Intent intent = IntentUtils.goToAlbumIntent(new ArrayList<String>(), 1, getResources().getString(R.string.confirm), true, ActivityRegister.this);
                startActivityForResult(intent, requestCodeAvatar);
                break;
            case R.id.xueLiFL:
                Intent intent1 = IntentUtils.goToAlbumIntent(new ArrayList<String>(), 1, getResources().getString(R.string.confirm), true, ActivityRegister.this);
                startActivityForResult(intent1, requestCodeXueLi);
                break;
            case R.id.ziXunShiFL:
                Intent intent2 = IntentUtils.goToAlbumIntent(new ArrayList<String>(), 1, getResources().getString(R.string.confirm), true, ActivityRegister.this);
                startActivityForResult(intent2, requestCodeZiXunShi);
                break;
            case R.id.shenFenFL:
                Intent intent3 = IntentUtils.goToAlbumIntent(new ArrayList<String>(), 1, getResources().getString(R.string.confirm), true, ActivityRegister.this);
                startActivityForResult(intent3, requestCodeShenFen);
                break;
            case R.id.readXieYi:
                ActivityXieYi.open(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //avatar
        if (requestCode == requestCodeAvatar && resultCode == RESULT_OK) {
            String[] paths = data.getStringArrayExtra(PhotoAlbumActivity.Key_SelectPaths);
            if (paths != null && paths.length <= 0) {
                return;
            }
            if (data.getStringExtra(PhotoActivity.kWhereFrom).equals(PhotoActivity.kFromAlbum)) {
                if (!TextUtils.isEmpty(paths[0])) {
                    ActivityCropImage.openForResult(this, paths[0], 750, 750, true, requestCodeAvatarCrop);
                    return;
                }
            } else if (data.getStringExtra(PhotoActivity.kWhereFrom).equals(PhotoActivity.kFromCamera)) {
                if (!TextUtils.isEmpty(paths[0])) {
                    avatarStr = paths[0];
                    if (avatarBmp != null && !avatarBmp.isRecycled()) {
                        avatarBmp.recycle();
                        avatarBmp = null;
                    }
                    avatarBmp = BitmapUtil.loadBitmap(avatarStr, 1600);
                    avatar.setImageBitmap(avatarBmp);
                }
            }
        } else if (requestCode == requestCodeAvatarCrop && resultCode == RESULT_OK) {
            avatarStr = data.getStringExtra(ActivityCropImage.kCropImagePath);
            if (avatarBmp != null && !avatarBmp.isRecycled()) {
                avatarBmp.recycle();
                avatarBmp = null;
            }
            avatarBmp = BitmapUtil.loadBitmap(avatarStr, 1600);
            avatar.setImageBitmap(avatarBmp);
            return;
        } else if (requestCode == requestCodeXueLi && resultCode == RESULT_OK) {//xueli
            String[] paths = data.getStringArrayExtra(PhotoAlbumActivity.Key_SelectPaths);
            if (paths != null && paths.length <= 0) {
                return;
            }
            if (data.getStringExtra(PhotoActivity.kWhereFrom).equals(PhotoActivity.kFromAlbum)) {
                if (!TextUtils.isEmpty(paths[0])) {
                    ActivityCropImage.openForResult(this, paths[0], 750, 750, true, requestCodeXueLiCrop);
                    return;
                }
            } else if (data.getStringExtra(PhotoActivity.kWhereFrom).equals(PhotoActivity.kFromCamera)) {
                if (!TextUtils.isEmpty(paths[0])) {
                    xueLiStr = paths[0];
                    if (xueLiBmp != null && !xueLiBmp.isRecycled()) {
                        xueLiBmp.recycle();
                        xueLiBmp = null;
                    }
                    xueLiBmp = BitmapUtil.loadBitmap(xueLiStr, 1600);
                    xueLiImage.setImageBitmap(xueLiBmp);
                }
            }
        } else if (requestCode == requestCodeXueLiCrop && resultCode == RESULT_OK) {
            xueLiStr = data.getStringExtra(ActivityCropImage.kCropImagePath);
            if (xueLiBmp != null && !xueLiBmp.isRecycled()) {
                xueLiBmp.recycle();
                xueLiBmp = null;
            }
            xueLiBmp = BitmapUtil.loadBitmap(xueLiStr, 1600);
            xueLiImage.setImageBitmap(xueLiBmp);
            return;
        } else if (requestCode == requestCodeZiXunShi && resultCode == RESULT_OK) {//zixunshi
            String[] paths = data.getStringArrayExtra(PhotoAlbumActivity.Key_SelectPaths);
            if (paths != null && paths.length <= 0) {
                return;
            }
            if (data.getStringExtra(PhotoActivity.kWhereFrom).equals(PhotoActivity.kFromAlbum)) {
                if (!TextUtils.isEmpty(paths[0])) {
                    ActivityCropImage.openForResult(this, paths[0], 750, 750, true, requestCodeZiXunShiCrop);
                    return;
                }
            } else if (data.getStringExtra(PhotoActivity.kWhereFrom).equals(PhotoActivity.kFromCamera)) {
                if (!TextUtils.isEmpty(paths[0])) {
                    ziXunShiStr = paths[0];
                    if (ziXunShiBmp != null && !ziXunShiBmp.isRecycled()) {
                        ziXunShiBmp.recycle();
                        ziXunShiBmp = null;
                    }
                    ziXunShiBmp = BitmapUtil.loadBitmap(ziXunShiStr, 1600);
                    ziXunShiImage.setImageBitmap(ziXunShiBmp);
                }
            }
        } else if (requestCode == requestCodeZiXunShiCrop && resultCode == RESULT_OK) {
            ziXunShiStr = data.getStringExtra(ActivityCropImage.kCropImagePath);
            if (ziXunShiBmp != null && !ziXunShiBmp.isRecycled()) {
                ziXunShiBmp.recycle();
                ziXunShiBmp = null;
            }
            ziXunShiBmp = BitmapUtil.loadBitmap(ziXunShiStr, 1600);
            ziXunShiImage.setImageBitmap(ziXunShiBmp);
            return;
        } else if (requestCode == requestCodeShenFen && resultCode == RESULT_OK) {//shenfenzheng
            String[] paths = data.getStringArrayExtra(PhotoAlbumActivity.Key_SelectPaths);
            if (paths != null && paths.length <= 0) {
                return;
            }
            if (data.getStringExtra(PhotoActivity.kWhereFrom).equals(PhotoActivity.kFromAlbum)) {
                if (!TextUtils.isEmpty(paths[0])) {
                    ActivityCropImage.openForResult(this, paths[0], 750, 750, true, requestCodeShenFenCrop);
                    return;
                }
            } else if (data.getStringExtra(PhotoActivity.kWhereFrom).equals(PhotoActivity.kFromCamera)) {
                if (!TextUtils.isEmpty(paths[0])) {
                    shenFenStr = paths[0];
                    if (shenFenBmp != null && !shenFenBmp.isRecycled()) {
                        shenFenBmp.recycle();
                        shenFenBmp = null;
                    }
                    shenFenBmp = BitmapUtil.loadBitmap(shenFenStr, 1600);
                    shenFenImage.setImageBitmap(shenFenBmp);
                }
            }
        } else if (requestCode == requestCodeShenFenCrop && resultCode == RESULT_OK) {
            shenFenStr = data.getStringExtra(ActivityCropImage.kCropImagePath);
            if (shenFenBmp != null && !shenFenBmp.isRecycled()) {
                shenFenBmp.recycle();
                shenFenBmp = null;
            }
            shenFenBmp = BitmapUtil.loadBitmap(shenFenStr, 1600);
            shenFenImage.setImageBitmap(shenFenBmp);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (avatarBmp != null && !avatarBmp.isRecycled()) {
            avatarBmp.recycle();
            avatarBmp = null;
        }
        if (xueLiBmp != null && !xueLiBmp.isRecycled()) {
            xueLiBmp.recycle();
            xueLiBmp = null;
        }
        if (ziXunShiBmp != null && !ziXunShiBmp.isRecycled()) {
            ziXunShiBmp.recycle();
            ziXunShiBmp = null;
        }
        if (shenFenBmp != null && !shenFenBmp.isRecycled()) {
            shenFenBmp.recycle();
            shenFenBmp = null;
        }
    }

    private void tryToRegister() {
        final String phoneNumberStr = phoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumberStr)) {
            ToastUtil.makeShortText(getString(R.string.phoneNotNul));
            return;
        }
        if (!CommonUtil.isPhoneNumberValid(phoneNumberStr)) {
            ToastUtil.makeShortText(getString(R.string.phoneNorRight));
            return;
        }

        final String pwdStr = password.getText().toString();
        if (TextUtils.isEmpty(pwdStr)) {
            ToastUtil.makeShortText(getString(R.string.passwordNotNull));
            return;
        }

        final String nameStr = name.getText().toString();
        if (TextUtils.isEmpty(nameStr)) {
            ToastUtil.makeShortText(getString(R.string.inputName));
            return;
        }

        final String doctorTitleStr = doctorTitle.getText().toString();
        if (TextUtils.isEmpty(doctorTitleStr)) {
            ToastUtil.makeShortText(getString(R.string.inputTouXian));
            return;
        }

        final String goodAtStr = goodAt.getText().toString();
        if (TextUtils.isEmpty(goodAtStr)) {
            ToastUtil.makeShortText(getString(R.string.inputShanChang));
            return;
        }

        final String briefStr = brief.getText().toString();
        if (TextUtils.isEmpty(briefStr)) {
            ToastUtil.makeShortText(getString(R.string.inputJianJie));
            return;
        }

        final String hospitalStr = hospital.getText().toString();
        if (TextUtils.isEmpty(hospitalStr)) {
            ToastUtil.makeShortText(getString(R.string.inputHospital));
            return;
        }
        //avatar
        if (TextUtils.isEmpty(avatarStr)) {
            ToastUtil.makeShortText(getString(R.string.selectAvatar));
            return;
        }
        if (TextUtils.isEmpty(xueLiStr)) {
            ToastUtil.makeShortText(getString(R.string.selectxueli));
            return;
        }
        if (TextUtils.isEmpty(ziXunShiStr)) {
            ToastUtil.makeShortText(getString(R.string.selectzixunshi));
            return;
        }
        if (TextUtils.isEmpty(shenFenStr)) {
            ToastUtil.makeShortText(getString(R.string.selectshenfen));
            return;
        }
        showDialog();
        uploadImage(avatarStr, 1);
    }

    private void register() {
        final String phoneNumberStr = phoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumberStr)) {
            ToastUtil.makeShortText(getString(R.string.phoneNotNul));
            return;
        }
        if (!CommonUtil.isPhoneNumberValid(phoneNumberStr)) {
            ToastUtil.makeShortText(getString(R.string.phoneNorRight));
            return;
        }

        final String pwdStr = password.getText().toString();
        if (TextUtils.isEmpty(pwdStr)) {
            ToastUtil.makeShortText(getString(R.string.passwordNotNull));
            return;
        }

        final String nameStr = name.getText().toString();
        if (TextUtils.isEmpty(nameStr)) {
            ToastUtil.makeShortText(getString(R.string.inputName));
            return;
        }

        final String doctorTitleStr = doctorTitle.getText().toString();
        if (TextUtils.isEmpty(doctorTitleStr)) {
            ToastUtil.makeShortText(getString(R.string.inputTouXian));
            return;
        }

        final String goodAtStr = goodAt.getText().toString();
        if (TextUtils.isEmpty(goodAtStr)) {
            ToastUtil.makeShortText(getString(R.string.inputShanChang));
            return;
        }

        final String briefStr = brief.getText().toString();
        if (TextUtils.isEmpty(briefStr)) {
            ToastUtil.makeShortText(getString(R.string.inputJianJie));
            return;
        }

        final String hospitalStr = hospital.getText().toString();
        if (TextUtils.isEmpty(hospitalStr)) {
            ToastUtil.makeShortText(getString(R.string.inputHospital));
            return;
        }
        if (TextUtils.isEmpty(avatarServerPath)) {
            ToastUtil.makeShortText(getString(R.string.avatar_load_filed));
            return;
        }

        if (TextUtils.isEmpty(xueLiServerPath)) {
            ToastUtil.makeShortText(getString(R.string.xueli_upload_filed));
            return;
        }
        if (TextUtils.isEmpty(zixunShiServerPath)) {
            ToastUtil.makeShortText(getString(R.string.zixunshi_upload_filed));
            return;
        }
        if (TextUtils.isEmpty(shenFenServerStr)) {
            ToastUtil.makeShortText(getString(R.string.shenfen_upload_filed));
            return;
        }
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("phoneNumber", phoneNumberStr));
        nameValuePairs.add(new BasicNameValuePair("password", pwdStr));
        nameValuePairs.add(new BasicNameValuePair("doctor_name", nameStr));
        nameValuePairs.add(new BasicNameValuePair("doctor_title", doctorTitleStr));
        nameValuePairs.add(new BasicNameValuePair("good_at", goodAtStr));
        nameValuePairs.add(new BasicNameValuePair("brief", briefStr));
        nameValuePairs.add(new BasicNameValuePair("hospital", hospitalStr));
        nameValuePairs.add(new BasicNameValuePair("image", avatarServerPath));
        nameValuePairs.add(new BasicNameValuePair("image1", xueLiServerPath));
        nameValuePairs.add(new BasicNameValuePair("image2", zixunShiServerPath));
        nameValuePairs.add(new BasicNameValuePair("image3", shenFenServerStr));
        Request.doRequest(this, nameValuePairs, ServerConfig.URL_REGISTER, Request.POST, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                dismissDialog();
                ToastUtil.showErrorMessage(e, "");
            }

            @Override
            public void onComplete(String response) {
                dismissDialog();
                RegisterModel registerModel = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, RegisterModel.class);
                if (registerModel != null && registerModel.result == 1) {
                    account.phoneNumber = phoneNumberStr;
                    account.password = pwdStr;
                    account.userId = registerModel.userId;
                    account.avatar = avatarServerPath;
                    account.saveMeInfoToPreference();
                    tryToRegisterChatServer();
                } else {
                    if (registerModel == null || TextUtils.isEmpty(registerModel.message)) {
                        ToastUtil.makeShortText(getString(R.string.registerFiled));
                    } else {
                        ToastUtil.makeShortText(registerModel.message);
                    }
                }
            }
        });
    }

    private void uploadImage(String path, final int index) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        Request.doFileUploadRequest(this, nameValuePairs, new File(path), ServerConfig.URL_EXPORT_UPLOAD, new Request.RequestListener() {
            @Override
            public void onException(Request.RequestException e) {
                dismissDialog();
                ToastUtil.makeShortText(getString(R.string.registerfiled));
            }

            @Override
            public void onComplete(String response) {
                ModifyAvatarModel modifyAvatarModel = HCApplicaton.getInstance().getGson().fromJsonWithNoException(response, ModifyAvatarModel.class);
                if (modifyAvatarModel != null && !TextUtils.isEmpty(modifyAvatarModel.result)) {
                    if (index == 1) {
                        avatarServerPath = modifyAvatarModel.result;
                        uploadImage(xueLiStr, 2);
                    } else if (index == 2) {
                        xueLiServerPath = modifyAvatarModel.result;
                        uploadImage(ziXunShiStr, 3);
                    } else if (index == 3) {
                        zixunShiServerPath = modifyAvatarModel.result;
                        uploadImage(shenFenStr, 4);
                    } else if (index == 4) {
                        shenFenServerStr = modifyAvatarModel.result;
                        register();
                    }
                } else {
                    ToastUtil.makeShortText(getString(R.string.registerfiled));
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

    private void tryToRegisterChatServer() {
        final String st7 = getResources().getString(R.string.network_anomalies);
        final String st8 = getResources().getString(R.string.User_already_exists);
        final String st9 = getResources().getString(R.string.registration_failed_without_permission);
        final String st10 = getResources().getString(R.string.Registration_failed);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMChatManager.getInstance().createAccountOnServer(account.userId, account.password);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            HCApplicaton.getInstance().setUserName(account.userId);
                            ToastUtil.makeShortText(getString(R.string.registerSuccessful));
                            finish();
                        }
                    });
                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NONETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                Toast.makeText(getApplicationContext(), st8, Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.UNAUTHORIZED) {
                                Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), st10 + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    class RegisterModel {
        public String message;
        public int result;
        public String userId;
    }
}
