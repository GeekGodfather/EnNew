package com.ennew.ui.act;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.opengl.ETC1;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ennew.R;
import com.ennew.annation.ViewInject;
import com.ennew.config.MyConstant;
import com.ennew.event.SmsCodeVerif;
import com.ennew.net.NetWorkUtils;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.CommonUtil;
import com.ennew.utils.ToastUtil;

/**
 * @author lilong
 * */

public class SmsCodeGetAct extends BaseActivity {
	@ViewInject(value = R.id.smscode_bt_get_getcode,click = "onClick")
	Button smscode_bt_get_getcode;
	@ViewInject(value = R.id.smscode_et_get_moblie)
	EditText smscode_et_get_moblie;
	String mobile;

	@Override
	protected int onCreatLayoutById() {
		return R.layout.activity_smscode_get;
	}
	@Override
	protected void onInitView() {
		// TODO Auto-generated method stub
		setHeadLeft(this, View.VISIBLE);
		setHeadMid(null, View.VISIBLE, "获取短信验证码");
		setHeadRight(this, View.INVISIBLE);
	}

	@Override
	public void onSuccess(String result, int requestTpye) {
		// TODO Auto-generated method stub
		 try {
			JSONObject jsonObject=new JSONObject(result);
			String code =jsonObject.optString("retCode");
			String messgae=jsonObject.optString("retMessage");
				toActivity(SmsCodeVerifAct.class );
				SmsCodeVerif smsEvent=new SmsCodeVerif(SmsCodeVerif.mobile);
				smsEvent.setData(mobile);
				eventBus.post(smsEvent);
			ToastUtil.showToast(messgae, Toast.LENGTH_LONG);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		super.onSuccess(result, requestTpye);
	}
	
	/* (non-Javadoc)
	 * @see com.ennew.ui.base.BaseActivity#onFailure(java.lang.String, int)
	 */
	@Override
	public void onFailure(String result,int requestTpye) {
		// TODO Auto-generated method stub
		ToastUtil.show("获取验证码失败");
		super.onFailure(result, requestTpye);
	}
	

	public void request() {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", "2");
			jsonObject.put("mobile", mobile);
			jsonObject.put("platform", "3");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 获取验证码
		NetWorkUtils.jsonDoPost(MyConstant.GET_VERIFYCODE, jsonObject, this, 1);
	}



	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.smscode_bt_get_getcode:
				mobile= smscode_et_get_moblie.getText().toString().trim();
				if(CommonUtil.checkCellPhone(mobile)&&!TextUtils.isEmpty(mobile)){
					request();
				}else{
					ToastUtil.show("号码不正确");
				}
				break;
			case R.id.comm_left:
				finish();
				break;

		}
		super.onClick(v);
	}
}
