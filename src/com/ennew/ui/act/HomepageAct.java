package com.ennew.ui.act;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ennew.R;
import com.ennew.model.HomePage;
import com.ennew.ui.adapter.HomePageAdapter;
import com.ennew.ui.adapter.HotRecommendAdapter;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.ToastUtil;
import com.ennew.widget.FlowGallery;
import com.ennew.widget.MyGridView;
import com.ennew.widget.MyListView;
import com.ennew.widget.SearchEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 * 
 * @author jianglihui
 * 
 */
public class HomepageAct extends BaseActivity {

	private InputMethodManager inputManager; // 软键盘
	private SearchEditText maintop_search; // 搜索输入框
	private MyGridView homepage_gv;
	List<HomePage> mList;
	HomePage homePage;
	private MyListView hot_recommend_lv; // 推荐列表
	HomePageAdapter homePageAdapter;
	HotRecommendAdapter hotRecommendAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homepage);
		initData();
		setTitle();
		initView();
	}

	// 设置Title
	public void setTitle() {
		RelativeLayout searchLayout = (RelativeLayout) findViewById(R.id.search_layout);
		searchLayout.setVisibility(View.VISIBLE);

		Button message = (Button) findViewById(R.id.message_btn);
		message.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(HomepageAct.this, ConversationActivity.class);
				startActivity(intent);
			}
		});
	}

	// 初始化view

	private void initView() {
		FlowGallery flow = (FlowGallery) findViewById(R.id.adgallery);
		LinearLayout ovalLayout = (LinearLayout) findViewById(R.id.ovalLayout);
		maintop_search = (SearchEditText) findViewById(R.id.maintop_search);
		homepage_gv = (MyGridView) findViewById(R.id.homepage_gv);
		hot_recommend_lv = (MyListView) findViewById(R.id.hot_recommend_lv);

		inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		ArrayList<String> urls = new ArrayList<String>();
		urls.add("http://e.hiphotos.baidu.com/image/w%3D400/sign=26a54028f0d3572c66e29ddcba126352/500fd9f9d72a60590959f6db2a34349b023bbaa1.jpg");
		urls.add("http://e.hiphotos.baidu.com/image/w%3D400/sign=275abdfbd762853592e0d321a0ef76f2/bd3eb13533fa828b1a145aeaff1f4134970a5a80.jpg");
		urls.add("http://c.hiphotos.baidu.com/image/w%3D400/sign=df63aede4410b912bfc1f7fef3fcfcb5/72f082025aafa40f598927b8a864034f78f01903.jpg");
		urls.add("http://h.hiphotos.baidu.com/image/w%3D400/sign=5785ecb8b8a1cd1105b673208913c8b0/d043ad4bd11373f02d5b6717a60f4bfbfaed04d5.jpg");

		flow.start(HomepageAct.this, 3000, ovalLayout, R.drawable.baix, R.drawable.bais, urls);

		homePageAdapter = new HomePageAdapter(HomepageAct.this, mList);
		homepage_gv.setAdapter(homePageAdapter);

		hotRecommendAdapter = new HotRecommendAdapter(HomepageAct.this, mList);
		hot_recommend_lv.setAdapter(hotRecommendAdapter);
	}

	/**
	 * 屏幕点击事件 点击屏幕 隐藏软键盘
	 */

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				inputManager.hideSoftInputFromWindow(maintop_search.getWindowToken(), 0);
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}

		return onTouchEvent(ev);
	}

	/**
	 * 输入框的位置
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof SearchEditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			maintop_search.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + maintop_search.getHeight();
			int right = left + maintop_search.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
			inputManager.hideSoftInputFromWindow(maintop_search.getWindowToken(), 0);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mList = new ArrayList<HomePage>();
		homePage = new HomePage();
		homePage.setImg(BitmapFactory.decodeResource(getResources(), R.drawable.homepage_film));
		homePage.setName("恩纽电影");
		mList.add(homePage);

		homePage = new HomePage();
		homePage.setImg(BitmapFactory.decodeResource(getResources(), R.drawable.homepage_hotel));
		homePage.setName("恩纽酒楼");
		mList.add(homePage);

		homePage = new HomePage();
		homePage.setImg(BitmapFactory.decodeResource(getResources(), R.drawable.homepage_plane));
		homePage.setName("飞机");
		mList.add(homePage);

		homePage = new HomePage();
		homePage.setImg(BitmapFactory.decodeResource(getResources(), R.drawable.homepage_recharge));
		homePage.setName("恩纽充值");
		mList.add(homePage);

		homePage = new HomePage();
		homePage.setImg(BitmapFactory.decodeResource(getResources(), R.drawable.homepage_travel));
		homePage.setName("恩纽旅行");
		mList.add(homePage);
	}
	// 连续两次点击返回键退出程序
	long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 3000) {
				ToastUtil.showToast("再按一次退出程序",0);
				exitTime = System.currentTimeMillis();
			} else {
				finish();
//				System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
