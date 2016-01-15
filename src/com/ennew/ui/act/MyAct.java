package com.ennew.ui.act;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ennew.R;
import com.ennew.annation.ViewInject;
import com.ennew.app.AppManager;
import com.ennew.config.MyConstant;
import com.ennew.net.LoginRequest;
import com.ennew.net.NetWorkUtils;
import com.ennew.net.OnPostListener;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.SharedPrefUtil;
import com.ennew.utils.ToastUtil;

/**
 * 我的
 * 
 * @author jianglihui
 *
 */
public class MyAct extends BaseActivity implements OnClickListener {

	private TextView login_out; // 登出
	private TextView personinfo; // 登出
	@ViewInject(R.id.getpwd)
	private TextView pwd;
	private AppManager appManager;
	@ViewInject(R.id.setpwd)
	private TextView setPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("tag1", "========MyAct=======onCreate=============");
		setContentView(R.layout.activity_my);
		setTitle();
		initView();
		if (appManager == null) {
			appManager = AppManager.getAppManager();
			appManager.addActivity(this);
		}
	}
	
	private void setTitle() {
		ImageView title_left = (ImageView) findViewById(R.id.title_left);
		title_left.setVisibility(View.GONE);
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.v("tag1", "========MyAct=======onResume=============");
	}
	@Override
	protected void onStart() {
		super.onStart();
		Log.v("tag1", "==========MyAct=====onStart=============");
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.v("tag1", "=========MyAct======onRestart=============");
	}
	@Override
	protected void onPause() {
		super.onPause();
		Log.v("tag1", "========MyAct=======onPause=============");
	}
	@Override
	protected void onStop() {
		super.onStop();
		Log.v("tag1", "========MyAct=======onStop=============");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("tag1", "=======MyAct========onDestroy=============");
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		login_out = (TextView) findViewById(R.id.login_out);
		personinfo=(TextView) findViewById(R.id.personinfo);
		personinfo.setOnClickListener(this);
		login_out.setOnClickListener(this);
		pwd.setOnClickListener(this);
		setPwd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_out:
//			Intent intent = new Intent(MyAct.this, Setting.class);
//			startActivityForResult(intent, 1007);
			SharedPrefUtil.remove(this, "login");
			NetWorkUtils.jsonPost(MyConstant.LOGOUT, null, onPostListener, MyConstant.LOGOUT_REQUEST_CODE);
			break;
		case R.id.personinfo:
			 toActivity(PerDetailsAct.class);
			break;
		case R.id.getpwd:
			 toActivity(SmsCodeGetAct.class);
			break;
		case R.id.setpwd:
			 toActivity(PasswordModify.class);
			break;

		}

	}
	
	OnPostListener onPostListener = new OnPostListener() {

		@Override
		public void onSuccess(String result, int requestTpye) {
			switch (requestTpye) {
			case MyConstant.LOGOUT_REQUEST_CODE:
				ToastUtil.showToast("登出成功", 0);
				NetWorkUtils.mQueue = null;
				NetWorkUtils.cookieStore.clear();
				LoginRequest.httpUtils = null;
//				MainTabActivity main = (MainTabActivity) MyAct.this.getParent();
//				main.changePhonenum(0);
//				appManager.finishActivity(MyAct.this);
//				Intent intent = new Intent(MyAct.this, LoginAct.class);
//				startActivity(intent);
				finish();
				System.exit(0);
				break;

			}

		}

		@Override
		public void onFailure(String result, int requestTpye) {
			switch (requestTpye) {
			case MyConstant.LOGOUT_REQUEST_CODE:
				ToastUtil.showToast("登录失败", 0);
				break;
			}
		}
	};
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
//					System.exit(0);
				}

				return true;
			}
			return super.onKeyDown(keyCode, event);
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			switch (requestCode) {
			case 1007:
				MainTabActivity main = (MainTabActivity) MyAct.this.getParent();
				main.changePhonenum(0);

				break;
			}
		}
}
