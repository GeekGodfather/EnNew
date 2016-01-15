package com.ennew.ui.act;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.ennew.net.NetWorkUtils;
import com.ennew.net.OnPostListener;
import com.ennew.utils.CommonUtil;
import com.ennew.utils.StringUtil;
import com.ennew.utils.ToastUtil;
import com.ennew.utils.Utils;

/**
 * 注册
 * 
 * @author jianglihui
 *
 */

public class RegisterAct extends Activity implements OnClickListener {

	Map<Object, Object> loginMap;
	private Button register_btn;
	private EditText phone_num;
	private EditText verify_code;
	private EditText password;
	private TextView get_verify_code; // 获取验证码
	private CheckBox register_checkbox;
	private String phoneNum, verifyCode, pwd;
	private AppManager appManager;
	private TimeCount time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		setTitle();
		initView();
		// login();
	}

	private void initView() {
		register_btn = (Button) findViewById(R.id.register_btn);
		phone_num = (EditText) findViewById(R.id.phone_num);
		verify_code = (EditText) findViewById(R.id.verify_code);
		password = (EditText) findViewById(R.id.password);
		get_verify_code = (TextView) findViewById(R.id.get_verify_code);
		register_checkbox = (CheckBox) findViewById(R.id.register_checkbox);

		register_btn.setOnClickListener(this);
		get_verify_code.setOnClickListener(this);
	}

	private void setTitle() {
		ImageView title_left = (ImageView) findViewById(R.id.title_left);
		TextView title_center = (TextView) findViewById(R.id.title_center);
		title_center.setText("返回");

		title_left.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_btn:
			if(Utils.isFastDoubleClick()){
				break;
			}
			pwd = password.getText().toString().trim();
			verifyCode = verify_code.getText().toString().trim();
			if (!getPhoneEdittext()) {
				break;
			}
			if (TextUtils.isEmpty(pwd)) {
				ToastUtil.showToast("密码不能为空", 0);
				break;
			}
			if (TextUtils.isEmpty(verifyCode)) {
				ToastUtil.showToast("验证码不能为空", 0);
				break;
			}
			if (!register_checkbox.isChecked()) {
				ToastUtil.showToast("请阅读使用说明", 0);
				break;
			}
			if (appManager == null) {
				appManager = AppManager.getAppManager();
				appManager.finishActivity(LoginAct.class);
			}
			checkVerifyCode();

			break;
		case R.id.title_left:
			finish();
			break;
		case R.id.get_verify_code:
			if (!getPhoneEdittext()) {
				break;
			}
			getVerifyCode();
			break;

		}

	}

	/**
	 * 屏幕点击事件 点击屏幕 隐藏软键盘
	 */

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (CommonUtil.isShouldHideInput(v, ev, phone_num) && 
					CommonUtil.isShouldHideInput(v, ev, verify_code)  && 
					CommonUtil.isShouldHideInput(v, ev, password))  {
				CommonUtil.hideInput(RegisterAct.this, phone_num);
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
	 * 获取验证码
	 */
	String type = "1"; // 1：注册 2：找回密码

	private void getVerifyCode() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("platform", "3");
			jsonObject.put("mobile", phoneNum);
			jsonObject.put("type", type);
			jsonObject.put("smsCode", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// NetWorkUtils.getResultDoPost();
		NetWorkUtils.getSessionPost(MyConstant.GET_VERIFYCODE, jsonObject, onPostListener, MyConstant.VERIFYCODE_REQUEST_CODE);
	}

	/**
	 * 验证 验证码
	 */
	private void checkVerifyCode() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("platform", "3");
			jsonObject.put("mobile", phoneNum);
			jsonObject.put("smsCode", verifyCode);
			jsonObject.put("type", type);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		NetWorkUtils.sameSessionPost(MyConstant.CHECK_VERIFYCODE, jsonObject, onPostListener, MyConstant.CHECK_VERIFYCODE_REQUEST_CODE);

	}

	/**
	 * 注册
	 */
	private void register() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("platform", "3");
			jsonObject.put("mobile", phoneNum);
			jsonObject.put("smsCode", verifyCode);
			jsonObject.put("passwd", pwd);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NetWorkUtils.sameSessionPost(MyConstant.REGISTER, jsonObject, onPostListener, MyConstant.REGISTER_REQUEST_CODE);

	}

	private boolean getPhoneEdittext() {
		phoneNum = phone_num.getText().toString().trim();
		if (TextUtils.isEmpty(phoneNum)) {
			ToastUtil.showToast("手机号不能为空", 0);
			return false;
		}
		if (!StringUtil.isMobileNO(phoneNum)) {
			ToastUtil.showToast("手机号格式不正确", 0);
		}
		return StringUtil.isMobileNO(phoneNum);
	}

	OnPostListener onPostListener = new OnPostListener() {

		@Override
		public void onSuccess(String result, int requestTpye) {
			switch (requestTpye) {
			case MyConstant.VERIFYCODE_REQUEST_CODE:
				time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
				time.start();// 开始计时
				ToastUtil.showToast("验证码已发送，请注意查收", 0);
				break;
			case MyConstant.CHECK_VERIFYCODE_REQUEST_CODE:
				ToastUtil.showToast("验证码验证成功", 0);
				register();
				break;
			case MyConstant.REGISTER_REQUEST_CODE:
				ToastUtil.showToast("注册成功", 0);
				NetWorkUtils.mQueue = null;
				finish();
				break;
			}

		}

		@Override
		public void onFailure(String result, int requestTpye) {
			switch (requestTpye) {
			case MyConstant.VERIFYCODE_REQUEST_CODE:
				if (MyConstant.RETCODE_VERIFYCODE_INTIME.equals(result)) {
					ToastUtil.showToast("获取验证码间隔时间短", 0);
				} else if (MyConstant.RETCODE_GETMSG_FAIL.equals(result)) {
					ToastUtil.showToast("获取验证码失败", 0);
				} else if (MyConstant.RETCODE_REREGISTER.equals(result)) {
					ToastUtil.showToast("此账号已存在", 0);
				} else {
					ToastUtil.showToast("获取验证码失败", 0);
				}
				break;
			case MyConstant.CHECK_VERIFYCODE_REQUEST_CODE:
				if (MyConstant.RETCODE_VERIFYCODE_ERROR.equals(result)) {
					ToastUtil.showToast("验证码错误", 0);
				} else if (MyConstant.RETCODE_VERIFYCODE_OUTTIME.equals(result)) {
					ToastUtil.showToast("验证码失效", 0);
				} else if (MyConstant.RETCODE_OUT_VERIFYNO.equals(result)) {
					ToastUtil.showToast("超出短信验证码验证次数", 0);
				} else {
					ToastUtil.showToast("验证验证码失败", 0);
				}

				break;
			case MyConstant.REGISTER_REQUEST_CODE:
				if (MyConstant.RETCODE_REQUEST_ERROR.equals(result)) {
					ToastUtil.showToast("注册失败(非法请求)", 0);
				} else {
					ToastUtil.showToast("注册失败", 0);
				}

				break;
			}
		}
	};

	private void initUserSession() {

	}

	protected void onPause() {
		super.onPause();
		CommonUtil.hideInput(RegisterAct.this, phone_num);
	}
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			get_verify_code.setText("重新获取");
			get_verify_code.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			get_verify_code.setClickable(false);
			get_verify_code.setText(millisUntilFinished / 1000 + "秒后重新获取");
		}
	}

}
