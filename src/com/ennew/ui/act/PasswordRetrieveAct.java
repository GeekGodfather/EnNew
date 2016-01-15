/**
 * 2015年12月16日PasswordRetrieveAct.javalilong2015
 */
package com.ennew.ui.act;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
 

import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ennew.R;
import com.ennew.app.EnnewAcitivityList;
import com.ennew.config.MyConstant;
import com.ennew.net.NetWorkUtils;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.ToastUtil;

/**
 * @author lilong
 *
 */
public class PasswordRetrieveAct extends BaseActivity  {
	private Button pd_rec_bt_pdmod;
	private EditText pd_rec_et_newpd,pd_rec_et_phone;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_password_rec);
		getView();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onSuccess(String result, int requestTpye) {
			try {
				JSONObject jsonObject=new JSONObject(result);
				String code=jsonObject.optString("retCode");
				String retMessage=jsonObject.optString("retMessage");
				if(code.equals(MyConstant.RETCODE)){	
					ToastUtil.show(retMessage);
					EnnewAcitivityList.getInstance().exitAccount(3);
				} 				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			 
	}
	@Override
	public void onFailure(String result, int requestTpye) {
		// TODO Auto-generated method stub
		ToastUtil.show(result);
		super.onFailure(result, requestTpye);
	}
 	

	public void getView() {
		// TODO Auto-generated method stub
		pd_rec_bt_pdmod=(Button) findViewById(R.id.pd_rec_bt_pdmod);		 
		pd_rec_et_newpd=(EditText) findViewById(R.id.pd_rec_et_newpd);
		pd_rec_et_phone=(EditText) findViewById(R.id.pd_rec_et_phone);
		 
		
		setHeadLeft(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	        finish();
				
			}
		}, View.VISIBLE);
		
		setHeadRight(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				
			}
		}, View.VISIBLE);
		
		setHeadMid(null, View.VISIBLE, "密码找回");
  
	}
	

	public void request() {
		// TODO Auto-generated method stub
		
		String newpd=pd_rec_et_newpd.getText().toString().trim();
		 
		String mobile=pd_rec_et_phone.getText().toString().trim();
        		
		JSONObject jsonObject = new JSONObject();
		try {			 
			jsonObject.put("platform", "3");
			jsonObject.put("password", newpd);			 
			jsonObject.put("mobile", mobile);
		    //添加验证码
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 找回密码
		NetWorkUtils.jsonDoPost(MyConstant.PASSWORD_RETRIEVE_INTERFACE, jsonObject, this, 1);	 
	}	
 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub		
		switch (v.getId()) {
		case R.id.pd_rec_bt_pdmod:
			//确定
			request();
			break;
		}
		
	}
}
