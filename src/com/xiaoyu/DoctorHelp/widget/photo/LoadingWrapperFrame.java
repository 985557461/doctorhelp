package com.xiaoyu.DoctorHelp.widget.photo;import android.content.Context;import android.util.AttributeSet;import android.view.View;import android.view.View.OnClickListener;import android.widget.FrameLayout;import android.widget.LinearLayout;import android.widget.ProgressBar;import android.widget.TextView;import com.xiaoyu.DoctorHelp.R;/** *  * @title:数据获取失败重新载入提示view * @description: * @company: 美丽说（北京）网络科技有限公司 * @author yinxinya * @version 1.0 * @created * @changeRecord */public class LoadingWrapperFrame extends FrameLayout implements OnClickListener,		LoadingWrapperLayout {	private View mLoadingLayerView;	private ProgressBar mProgressBar;	private TextView mTextView_msg;	private TextView mTextView_retryText;	private OnRetryListener mRetryListener;	private boolean mLoadingLayerShowing = false;	public LoadingWrapperFrame(Context context) {		super(context);		initUI();	}	public LoadingWrapperFrame(Context context, AttributeSet attrs) {		super(context, attrs);		initUI();	}	public LoadingWrapperFrame(Context context, AttributeSet attrs, int defStyle) {		super(context, attrs, defStyle);		initUI();	}	private void initUI() {		mLoadingLayerView = inflate(getContext(), R.layout.loading_view, null);		mLoadingLayerView.setVisibility(View.GONE);		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(				LinearLayout.LayoutParams.FILL_PARENT,				LinearLayout.LayoutParams.FILL_PARENT);		addView(mLoadingLayerView, layoutParams);		mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);		mTextView_msg = (TextView) findViewById(R.id.loading_msg);		mTextView_retryText = (TextView) findViewById(R.id.loading_retry);		mTextView_retryText.setOnClickListener(this);	}	public void setOnRetryListener(OnRetryListener retryListener) {		mRetryListener = retryListener;	}	/**	 * 显示数据载入界面	 * 	 * @param msg	 *            提示信息（如 用户评论载入中...）	 */	public void showLoadingView(String msg) {		showLoadingView();		mTextView_msg.setText(msg);		mTextView_msg.setVisibility(View.VISIBLE);	}	public void showLoadingView() {		showLoadingLayer();		mProgressBar.setVisibility(View.VISIBLE);		mTextView_msg.setVisibility(View.GONE);		mTextView_retryText.setVisibility(View.GONE);	}	/**	 * 显示重新载入界面	 */	public void showRetryVeiw() {		showLoadingLayer();		mProgressBar.setVisibility(View.GONE);		mTextView_msg.setVisibility(View.GONE);		mTextView_retryText.setVisibility(View.VISIBLE);	}	public void showLoadingLayer() {		if (mLoadingLayerShowing) {			return;		}		mLoadingLayerShowing = true;		for (int i = 0; i < getChildCount(); i++) {			View view = getChildAt(i);			if (view == mLoadingLayerView) {				mLoadingLayerView.setVisibility(View.VISIBLE);			} else {				view.setTag(R.id.action_settings, view.getVisibility());				view.setVisibility(View.GONE);			}		}	}	public void hideLoadingLayer() {		if (!mLoadingLayerShowing) {			return;		}		mLoadingLayerShowing = false;		for (int i = 0; i < getChildCount(); i++) {			View view = getChildAt(i);			if (view == mLoadingLayerView) {				mLoadingLayerView.setVisibility(View.GONE);			} else {				if (view.getTag(R.id.action_settings) != null) {					int visibility = (Integer) view.getTag(R.id.action_settings);					view.setVisibility(visibility);				}			}		}	}	@Override	public void onClick(View v) {		if (mRetryListener != null) {			if (mTextView_retryText.isShown()) {				mRetryListener.onRetry();			}		}	}}