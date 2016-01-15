package com.ennew.ui.act;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.ennew.R;
import com.ennew.ui.base.TabHostActivity;
import com.ennew.ui.base.TabItem;
import com.ennew.umengsetting.UmengSet;
import com.ennew.utils.LogUtil;


/**
 * <p>
 * 整个流程就像使用ListView自定BaseAdapter一样
 * </p>
 * 
 * <p>
 * 如果要自定义TabHostActivity的Theme，并且不想要头部阴影 一定要添加这个android:windowContentOverlay =
 * null
 * </p>
 * 
 * */
public class MainTabActivity extends TabHostActivity {

	List<TabItem> mItems;
	private LayoutInflater mLayoutInflater;

	/**
	 * 在初始化TabWidget前调用 和TabWidget有关的必须在这里初始化
	 */
	@Override
	protected void prepare() {
		TabItem homepage = new TabItem(getString(R.string.main_homepage), // title
				R.drawable.maintab_homepage_nor, // icon
				R.drawable.main_tab_bk, // background
				new Intent(this, HomepageAct.class)); // intent

		TabItem shopping = new TabItem(getString(R.string.main_shopping), R.drawable.maintab_shopping_nor, R.drawable.main_tab_bk, new Intent(this, ShoppingAct.class));
		TabItem shoppingcart = new TabItem(getString(R.string.main_shoppingcart), R.drawable.maintab_shoppingcart_nor, R.drawable.main_tab_bk, new Intent(this, ShoppingCartAct.class));
		TabItem my = new TabItem(getString(R.string.main_my), R.drawable.maintab_my_nor, R.drawable.main_tab_bk, new Intent(this, MyAct.class));

		mItems = new ArrayList<TabItem>();
		mItems.add(homepage);
		mItems.add(shopping);
		mItems.add(shoppingcart);
		mItems.add(my);

		// 设置分割线
//		TabWidget tabWidget = getTabWidget();
//		tabWidget.setDividerDrawable(R.drawable.tab_divider);

		mLayoutInflater = getLayoutInflater();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//开启友盟推送服务
		UmengSet.SingleInstace().enablePush(this);
		Log.i("device",  UmengSet.SingleInstace().deviceToken(this));
		setCurrentTab(0);
	}

	/** tab的title，icon，边距设定等等 */
	@Override
	protected void setTabItemTextView(TextView textView, int position) {
		textView.setPadding(5, 5, 5, 5);
		textView.setText(mItems.get(position).getTitle());
//		textView.setBackgroundResource(mItems.get(position).getBg());
		textView.setCompoundDrawablesWithIntrinsicBounds(0, mItems.get(position).getIcon(), 0, 0);

	}

	/** tab唯一的id */
	@Override
	protected String getTabItemId(int position) {
		return mItems.get(position).getTitle(); // 我们使用title来作为id，你也可以自定
	}

	/** 点击tab时触发的事件 */
	@Override
	protected Intent getTabItemIntent(int position) {
		return mItems.get(position).getIntent();
	}

	@Override
	protected int getTabItemCount() {
		return mItems.size();
	}

	/** 自定义头部文件 */
	/*@Override
	protected View getTop() {
		return mLayoutInflater.inflate(R.layout.main_top, null);
//		return mLayoutInflater.inflate(R.layout.main_top, null);
	}*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
	public  void changePhonenum(int num){
		setCurrentTab(num);
	}



	public static String getDeviceInfo(Context context) {
		try{
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if( TextUtils.isEmpty(device_id) ){
				device_id = mac;
			}

			if( TextUtils.isEmpty(device_id) ){
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}


}
