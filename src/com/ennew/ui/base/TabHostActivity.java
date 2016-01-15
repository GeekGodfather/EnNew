package com.ennew.ui.base;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.ennew.R;
import com.ennew.app.AppManager;
import com.ennew.config.MyConstant;
import com.ennew.ui.act.LoginAct;
import com.ennew.utils.SharedPrefUtil;

@SuppressWarnings("deprecation")
public abstract class TabHostActivity extends TabActivity {

	private TabHost mTabHost;
	private TabWidget mTabWidget;
	private LayoutInflater mLayoutflater;
	private AppManager appManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set theme because we do not want the shadow
		setTheme(R.style.Theme_Tabhost);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 取消标题栏
		setContentView(R.layout.main_tab_host);
		if (appManager == null) {
			appManager = AppManager.getAppManager();
		}
		appManager.addActivity(this);
		mLayoutflater = getLayoutInflater();

		mTabHost = getTabHost();
		mTabWidget = getTabWidget();
		// mTabWidget.setStripEnabled(false); // need android2.2

		prepare();

		initTop();
		initTabSpec();
		setTab(3, true);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				View mView0 = mTabHost.getTabWidget().getChildAt(0);
				TextView tvTabItem0 = (TextView) mView0.findViewById(R.id.tab_item_tv);

				View mView1 = mTabHost.getTabWidget().getChildAt(1);
				TextView tvTabItem1 = (TextView) mView1.findViewById(R.id.tab_item_tv);

				View mView2 = mTabHost.getTabWidget().getChildAt(2);
				TextView tvTabItem2 = (TextView) mView2.findViewById(R.id.tab_item_tv);

				View mView3 = mTabHost.getTabWidget().getChildAt(3);
				TextView tvTabItem3 = (TextView) mView3.findViewById(R.id.tab_item_tv);

				if (tabId.equalsIgnoreCase(getString(R.string.main_homepage))) {
					tvTabItem0.setTextColor(getResources().getColor(R.color.default_blue_color));
					tvTabItem0.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_homepage_sel, 0, 0);
					tvTabItem1.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_shopping_nor, 0, 0);
					tvTabItem2.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_shoppingcart_nor, 0, 0);
					tvTabItem3.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_my_nor, 0, 0);

				} else if (tabId.equalsIgnoreCase(getString(R.string.main_shopping))) {
					tvTabItem0.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem0.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_homepage_nor, 0, 0);
					tvTabItem1.setTextColor(getResources().getColor(R.color.default_blue_color));
					tvTabItem1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_shopping_sel, 0, 0);
					tvTabItem2.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_shoppingcart_nor, 0, 0);
					tvTabItem3.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_my_nor, 0, 0);

				} else if (tabId.equalsIgnoreCase(getString(R.string.main_shoppingcart))) {
					tvTabItem0.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem0.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_homepage_nor, 0, 0);
					tvTabItem1.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_shopping_nor, 0, 0);
					tvTabItem2.setTextColor(getResources().getColor(R.color.default_blue_color));
					tvTabItem2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_shoppingcart_sel, 0, 0);
					tvTabItem3.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_my_nor, 0, 0);

				} else if (tabId.equalsIgnoreCase(getString(R.string.main_my))) {
					tvTabItem0.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem0.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_homepage_nor, 0, 0);
					tvTabItem1.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_shopping_nor, 0, 0);
					tvTabItem2.setTextColor(getResources().getColor(R.color.default_light_grey_color));
					tvTabItem2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_shoppingcart_nor, 0, 0);
					tvTabItem3.setTextColor(getResources().getColor(R.color.default_blue_color));
					tvTabItem3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_my_sel, 0, 0);

				}
			}
		});
	}

	private void initTop() {
		// View child = getTop();
		// LinearLayout layout = (LinearLayout) findViewById(R.id.tab_top);
		// layout.addView(child);
	}

	private void initTabSpec() {

		int count = getTabItemCount();

		for (int i = 0; i < count; i++) {
			// set text view
			View tabItem = mLayoutflater.inflate(R.layout.main_tab_item, null);

			TextView tvTabItem = (TextView) tabItem.findViewById(R.id.tab_item_tv);
			tvTabItem.setTextColor(getResources().getColor(R.color.default_light_grey_color));
			setTabItemTextView(tvTabItem, i);
			// set id
			String tabItemId = getTabItemId(i);
			// set tab spec
			TabSpec tabSpec = mTabHost.newTabSpec(tabItemId);
			tabSpec.setIndicator(tabItem);
			tabSpec.setContent(getTabItemIntent(i));
			mTabHost.addTab(tabSpec);
		}

	}

	/** 在初始化界面之前调用 */
	protected void prepare() {
		// do nothing or you override it
	}

	/** 自定义头部布局 */
	/*
	 * protected View getTop() { // do nothing or you override it return null; }
	 */

	protected int getTabCount() {
		return mTabHost.getTabWidget().getTabCount();
	}

	/** 设置TabItem的图标和标题等 */
	abstract protected void setTabItemTextView(TextView textView, int position);

	abstract protected String getTabItemId(int position);

	abstract protected Intent getTabItemIntent(int position);

	abstract protected int getTabItemCount();

	protected void setCurrentTab(int index) {
		View mView = mTabHost.getTabWidget().getChildAt(index);
		TextView tvTabItem = (TextView) mView.findViewById(R.id.tab_item_tv);
		tvTabItem.setTextColor(getResources().getColor(R.color.default_blue_color));
		tvTabItem.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_homepage_sel, 0, 0);
		mTabHost.setCurrentTab(index);
	}

	protected void focusCurrentTab(int index) {
		View mView = mTabHost.getTabWidget().getChildAt(index);
		TextView tvTabItem = (TextView) mView.findViewById(R.id.tab_item_tv);
		tvTabItem.setTextColor(getResources().getColor(R.color.default_blue_color));
		tvTabItem.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.maintab_homepage_sel, 0, 0);
		mTabWidget.focusCurrentTab(index);
	}
	
	private void setTab(int id, boolean flag) {
		switch (id) {

		case 3:
			mTabWidget.getChildAt(3).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!"first".equals(getState("login"))) {
						Intent intent = new Intent(TabHostActivity.this, LoginAct.class);
						startActivityForResult(intent, MyConstant.LOGIN_RESULT);
					} else {
						setCurrentTab(3);
					}
				}
			});

		}
	}
	
	private String getState(String key) {
		return (String) SharedPrefUtil.getState(TabHostActivity.this, String.class, null, key);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MyConstant.LOGIN_RESULT:
			if (data != null) {
				Bundle b = data.getExtras();
				String loginResult = b.getString("loginResult");
				if ("success".equals(loginResult)) {
					setCurrentTab(3);
				}

			}

			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			appManager.AppExit(this);
			return true;
		}
		return false;
	}

}
