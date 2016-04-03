package com.xiaoyu.DoctorHelp.ui;

import android.os.Bundle;
import com.xiaoyu.DoctorHelp.R;
import com.xiaoyu.DoctorHelp.util.Debug;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * Created by xiaoyu on 2015/7/13.
 */
public class ActivityTest extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_test);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getViews() {
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void setListeners() {

    }
}
