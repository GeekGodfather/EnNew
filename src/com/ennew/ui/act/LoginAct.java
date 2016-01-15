package com.ennew.ui.act;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ennew.R;
import com.ennew.app.AppManager;
import com.ennew.config.MyConstant;
import com.ennew.net.LoginRequest;
import com.ennew.net.NetWorkUtils;
import com.ennew.net.OnPostListener;
import com.ennew.utils.CommonUtil;
import com.ennew.utils.JsonParse;
import com.ennew.utils.SharedPrefUtil;
import com.ennew.utils.StringUtil;
import com.ennew.utils.ToastUtil;
import com.ennew.utils.Utils;

/**
 * 登陆
 * 
 * @author jianglihui
 * 
 */
public class LoginAct extends Activity implements OnClickListener {

	Map<Object, Object> loginMap;
	private Button login_btn; // 登录
	private Button secert; // 加密
	private EditText usernameEd; // 用户名
	private EditText passwordEd; // 密码
	private CheckBox remember_checkbox; // 记住密码
	private String saveName = "";
	private String savePassword = "";
	private String userName, passWord;
	private AppManager appManager;
	Map<String, Object> ofInfoMap = new HashMap<String, Object>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initData();
		setTitle();
		initView();
		if (appManager == null) {
			appManager = AppManager.getAppManager();
			appManager.addActivity(this);
		}
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		login_btn = (Button) findViewById(R.id.login_btn);
		secert = (Button) findViewById(R.id.secert);
		usernameEd = (EditText) findViewById(R.id.username);
		passwordEd = (EditText) findViewById(R.id.password);
		remember_checkbox = (CheckBox) findViewById(R.id.remember_checkbox);

		if (!TextUtils.isEmpty(saveName)) {
			usernameEd.setText(saveName);
			passwordEd.setText(savePassword);
			usernameEd.setSelection(saveName.length());
			passwordEd.setSelection(savePassword.length());
		}

		login_btn.setOnClickListener(this);
		secert.setOnClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		saveName = getState("userName");
		savePassword = getState("passWord");
	}

	/**
	 * 设置标题
	 */
	private void setTitle() {
		ImageView title_left = (ImageView) findViewById(R.id.title_left);
		TextView title_center = (TextView) findViewById(R.id.title_center);
		Button title_right_button = (Button) findViewById(R.id.title_right_button);
		title_right_button.setText("注册");
		title_right_button.setVisibility(View.VISIBLE);
		title_center.setText("返回");

		title_left.setOnClickListener(this);
		title_right_button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn:
			if(Utils.isFastDoubleClick()){
				break;
			}
			saveState(MyConstant.IS_LOGIN_SUCCESS, false);
			userName = usernameEd.getText().toString().trim();
			passWord = passwordEd.getText().toString().trim();
			if (TextUtils.isEmpty(userName)) {
				ToastUtil.showToast("用户名不能为空", 0);
				break;
			}
			if (!StringUtil.isMobileNO(userName)) {
				ToastUtil.showToast("请输入有效的手机号码", 0);
				break;
			}
			if (TextUtils.isEmpty(passWord)) {
				ToastUtil.showToast("密码不能为空", 0);
				break;
			}
			if (remember_checkbox.isChecked()) {

				saveState("userName", userName);
				saveState("passWord", passWord);

			} else {
				SharedPrefUtil.remove(this, "userName");
				SharedPrefUtil.remove(this, "passWord");
			}
			login(userName, passWord);

			break;
		case R.id.title_left:
			skip("fail");
			break;
		case R.id.title_right_button:
			Intent intent = new Intent(LoginAct.this, RegisterAct.class);
			startActivity(intent);
			break;
		case R.id.secert:
			// LoginRequest.serectDoPost();
			String params = "{\"name\":\"lipan\",\"age\":\"12\",\"email\":\"12321321\"}";
			JSONObject obj = null;
			try {
				obj = new JSONObject(params);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String httpUrl = "http://10.4.44.140:8080/jiami/user/reg";
			NetWorkUtils.jsonDoPostSecret(httpUrl, obj, null, 1);
			break;

		}

	}

	private void login(String userName, String pwd) {
		LoginRequest.loginPost(MyConstant.LOGIN, MyConstant.argNameGetCaptcha, new Object[] { "true", MyConstant.LOGIN_INIT }, onPostListener, MyConstant.LOGIN_REQUEST_CODE, userName, passWord);

	}

	/**
	 * 跳转
	 * 
	 * @param result
	 *            返回值
	 */
	private void skip(String result) {
		Intent it = getIntent();
		Bundle bundle = new Bundle();
		bundle.putString("loginResult", result);
		it.putExtras(bundle);
		setResult(MyConstant.LOGIN_RESULT, it);
//		appManager.finishActivity(this);
		finish();
	}

	// 保存值到 SharedPreferences
	private void saveState(String key, Object value) {
		SharedPrefUtil.saveState(LoginAct.this, null, key, value);
	}

	// 从SharedPreferences取值
	private String getState(String key) {
		return (String) SharedPrefUtil.getState(this, String.class, null, key);
	}

	/**
	 * 屏幕点击事件 点击屏幕 隐藏软键盘
	 */

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (CommonUtil.isShouldHideInput(v, ev, usernameEd) && CommonUtil.isShouldHideInput(v, ev, passwordEd)) {
				CommonUtil.hideInput(LoginAct.this, usernameEd);
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}

		return onTouchEvent(ev);
	}

	OnPostListener onPostListener = new OnPostListener() {

		@Override
		public void onSuccess(String result, int requestTpye) {
			switch (requestTpye) {
			case MyConstant.LOGIN_REQUEST_CODE:
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("platform", "3");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				NetWorkUtils.jsonPost(MyConstant.GET_OPENFIRE_INFO, jsonObject, onPostListener, MyConstant.GET_OFINFO_REQUEST_CODE);
				ToastUtil.showToast("登录成功", 0);
				saveState("login", "first");
				saveState("userId", userName);
				skip("success");
				break;
			case MyConstant.GET_OFINFO_REQUEST_CODE:
				ToastUtil.showToast("获取Openfire信息成功", 0);
				Log.v("tag1", "======openfireResult========" + result);
				ofInfoMap = JsonParse.getJsonParse().parseOfInfo(result);
				saveState(MyConstant.ACCOUNT, ofInfoMap.get("username"));
				saveState(MyConstant.PASSWORD, ofInfoMap.get("password"));
				saveState(MyConstant.IS_LOGIN_SUCCESS, true);
				saveState(MyConstant.USER_NAME, userName);

				Log.v("tag1", "=========password========" + ofInfoMap.get("password"));
				Log.v("tag1", "=========username========" + ofInfoMap.get("username"));
				break;

			}

		}

		@Override
		public void onFailure(String result, int requestTpye) {
			switch (requestTpye) {
			case MyConstant.LOGIN_REQUEST_CODE:
				ToastUtil.showToast("登录失败", 0);
				break;
			case MyConstant.GET_OFINFO_REQUEST_CODE:
				ToastUtil.showToast("获取Openfire信息失败", 0);
				break;
			}
		}
	};

	protected void onPause() {
		super.onPause();
		CommonUtil.hideInput(LoginAct.this, usernameEd);
	}

}
