package com.ennew.ui.act;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

/**
 * @author lilong
 * 2015-12-16
 * 
 * 密码重置
 * */


import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ennew.R;
import com.ennew.config.MyConstant;
import com.ennew.net.NetWorkUtils;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.ToastUtil;

public class PasswordModify extends BaseActivity implements OnClickListener {
	
	private EditText pd_mod_et_oldpd,pd_mod_et_newpd,pd_mod_et_onemore_newpd;
	private Button pd_mod_bt_pdmod;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_password_mod);
		getView();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onSuccess(String result, int requestTpye) {
		// TODO Auto-generated method stub
		
		//解析成功的字段 
		try {
			JSONObject jsonObject=new JSONObject(result);
			String code=jsonObject.optString("retCode");
			String retMessage=jsonObject.optString("retMessage");			
			if(code.equals("0")){
				ToastUtil.showToast(retMessage, Toast.LENGTH_SHORT);
				finish();
			}else{
				ToastUtil.showToast("失败", Toast.LENGTH_SHORT);
			}			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		super.onSuccess(result, requestTpye);
				
	}
	
	
	@Override
	public void onFailure(String result, int requestTpye) {
		// TODO Auto-generated method stub
		super.onFailure(result, requestTpye);
	}
	

	public void getView() {
		// TODO Auto-generated method stub		
	    pd_mod_et_oldpd=(EditText) findViewById(R.id.pd_mod_et_oldpd);
		pd_mod_et_newpd=(EditText) findViewById(R.id.pd_mod_et_newpd);
		pd_mod_et_onemore_newpd=(EditText) findViewById(R.id.pd_mod_et_onemore_newpd);
		pd_mod_bt_pdmod=(Button) findViewById(R.id.pd_mod_bt_pdmod);			
		setHeadLeft(new OnClickListener() {			
			@Override
			public void onClick(View v) {
						finish();
			}
		}, View.VISIBLE);
		
		setHeadRight(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
	        finish();
	
			}
		}, View.VISIBLE);
		
		setHeadMid(null, View.VISIBLE, "密码更改");
		

	}
	


	
	/* (non-Javadoc)
	 * @see com.ennew.ui.base.BaseActivity#request()
	 */

	public void request() {
		// TODO Auto-generated method stub
		String newpd=pd_mod_et_newpd.getText().toString().trim();
		String oldpd=pd_mod_et_oldpd.getText().toString().trim();
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("userId", "");	
			jsonObject.put("platform", "3");
			jsonObject.put("password", newpd);
			jsonObject.put("passwordOld", oldpd);
		    //添加验证码
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// NetWorkUtils.getResultDoPost();
		NetWorkUtils.jsonDoPost(MyConstant.PASSWORD_MODIFY_INTERFACE, jsonObject, this, 1);	

	}


	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.pd_mod_bt_pdmod:
			String newpd= pd_mod_et_newpd.getText().toString().trim();
			String newonemorepd=pd_mod_et_onemore_newpd.getText().toString().trim();
			if(!TextUtils.isEmpty(newpd)&&newonemorepd.equals(newpd)){
				request();
			}
			break;
		}
		
	}
	
	
	
	
	
	
	

}
