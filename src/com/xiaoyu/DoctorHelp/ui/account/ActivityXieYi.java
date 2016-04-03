package com.xiaoyu.DoctorHelp.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.ui.ActivityBase;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xiaoyu on 2015/8/4.
 */
public class ActivityXieYi extends ActivityBase implements View.OnClickListener {
    private FrameLayout back;
    private TextView content;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, ActivityXieYi.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_xieyi);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getViews() {
        back = (FrameLayout) findViewById(R.id.back);
        content = (TextView) findViewById(R.id.content);
    }

    @Override
    protected void initViews() {
        try {
            InputStream inputStream = getAssets().open("xieyi.doc");
            WordExtractor extractor = new WordExtractor(inputStream);
            content.setText(extractor.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setListeners() {
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        }
    }
}
