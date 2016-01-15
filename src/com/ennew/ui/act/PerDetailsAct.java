package com.ennew.ui.act;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ennew.R;
import com.ennew.config.MyConstant;
import com.ennew.model.PerDetailsModel;
import com.ennew.net.NetWorkUtils;
import com.ennew.net.OnPostListener;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.LogUtil;
import com.ennew.utils.SharedPrefUtil;
import com.ennew.utils.ToastUtil;
import com.google.gson.Gson;

public class PerDetailsAct extends BaseActivity {

	private ImageView per_deatil_image_head;
	private TextView per_detail_tv_certno, per_detail_tv_userName,
			per_detail_tv_brithday, per_detail_tv_certtype,
			per_detail_tv_email, per_detail_tv_mobile, per_detail_tv_nickname;
	private RelativeLayout per_detail_rl_email, per_detail_rl_brithday,
			per_detail_rl_certno, per_detail_rl_certtype, per_detail_rl_mobile,
			per_detail_rl_username, per_detail_rl_nickname;

	final int CODE_BRITHDAY = 0;
	final int CODE_EMAIL = 1;
	final int CODE_CERTNO = 2;
	final int CODE_USERNAME = 3;
	final int CODE_NICKNAME = 4;
	final int CODE_MOBILE = 5;
	final int CODE_CERTTYPE = 6;
	final int QUERY_INFOR = 7;
	final int CHANGE_INFOR = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_per_detail);
		getView();		
		request();
		super.onCreate(savedInstanceState);
	}


	public void request() {
		// TODO Auto-generated method stub
		// super.request();
		JSONObject jsonObject = new JSONObject();
		try {			
		    String id=	SharedPrefUtil.getStateString(PerDetailsAct.this, "userId");
		    //ToastUtil.show(id);
			jsonObject.put("user_id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetWorkUtils.jsonPost(MyConstant.PERDETAILS_QUERY_INTERFACE,
				jsonObject, this, QUERY_INFOR);

	}

	/**
	 * 修改个人信息
	 */
	public void request1() {
		// TODO Auto-generated method stub
		// super.request();
		JSONObject jsonObject = new JSONObject();
		try {
			
			jsonObject.put("nickName", per_detail_tv_nickname.getText().toString());
			jsonObject.put("email", per_detail_tv_email.getText().toString());		
			String  version=SharedPrefUtil.getString(SharedPrefUtil.PERCHAGEVERSION);
			jsonObject.put("version", version);
			jsonObject.put("certType", per_detail_tv_certtype.getText().toString());
			jsonObject.put("certNo", per_detail_tv_certno.getText().toString());
			jsonObject.put("birthday", per_detail_tv_brithday.getText().toString());
			jsonObject.put("mobile", per_detail_tv_mobile.getText().toString());
			jsonObject.put("userName", per_detail_tv_userName.getText().toString());
			String id=	SharedPrefUtil.getStateString(PerDetailsAct.this, "userId");
			    //ToastUtil.show(id);
			jsonObject.put("user_id", id);
		 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetWorkUtils.jsonPost(MyConstant.PERDETAILS_CHANGE_INTERFACE,
				jsonObject, this, CHANGE_INFOR);
	}

	@Override
	public void onSuccess(String result, int requestTpye) {
		// TODO Auto-generated method stub
		switch (requestTpye) {
		case QUERY_INFOR:				 
			setView(getJsonToobj(result) );
			break;
		case CHANGE_INFOR:
			try {
				JSONObject jsonObject=new JSONObject(result);
			    	jsonObject.optBoolean("successful");
				ToastUtil.show("修改成功");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			break;
		}
	}

	@Override
	public void onFailure(String result, int requestTpye) {
		  
		  ToastUtil.show("服务端数据返回失败！！！");
	 
	}


	public void getView() {
		// TODO Auto-generated method stub
		per_deatil_image_head = (ImageView) findViewById(R.id.per_detail_image_head);
		per_detail_tv_nickname = (TextView) findViewById(R.id.per_detail_tv_nickname);
		per_detail_rl_email = (RelativeLayout) findViewById(R.id.per_detail_rl_email);
		per_detail_rl_brithday = (RelativeLayout) findViewById(R.id.per_detail_rl_brithday);
		per_detail_rl_certno = (RelativeLayout) findViewById(R.id.per_detail_rl_certno);
		per_detail_rl_certtype = (RelativeLayout) findViewById(R.id.per_detail_rl_certtype);
		per_detail_rl_mobile = (RelativeLayout) findViewById(R.id.per_detail_rl_mobile);
		per_detail_rl_username = (RelativeLayout) findViewById(R.id.per_detail_rl_username);
		per_detail_rl_nickname = (RelativeLayout) findViewById(R.id.per_detail_rl_nickname);

		per_detail_rl_brithday.setOnClickListener(this);
		per_detail_rl_certno.setOnClickListener(this);
		per_detail_rl_certtype.setOnClickListener(this);
		per_detail_rl_email.setOnClickListener(this);
		per_detail_rl_mobile.setOnClickListener(this);
		per_detail_rl_nickname.setOnClickListener(this);
		per_detail_rl_username.setOnClickListener(this);

		per_detail_tv_certno = (TextView) findViewById(R.id.per_deatil_tv_certno);
		per_detail_tv_userName = (TextView) findViewById(R.id.per_deatil_tv_userName);
		per_detail_tv_brithday = (TextView) findViewById(R.id.per_detail_tv_brithday);
		per_detail_tv_certtype = (TextView) findViewById(R.id.per_detail_tv_certtype);
		per_detail_tv_email = (TextView) findViewById(R.id.per_detail_tv_email);
		per_detail_tv_mobile = (TextView) findViewById(R.id.per_detail_tv_mobile);

		setHeadLeft(this, View.VISIBLE);
		setHeadRight(this, View.VISIBLE);
		setHeadMid(null, View.VISIBLE, "个人详情");
	}


	public void setView(Object obj) {
		// TODO Auto-generated method stub
		if(obj==null)
			return;		
		PerDetailsModel p = (PerDetailsModel) obj;
		per_detail_tv_certno.setText(p.certNo);
		per_detail_tv_certtype.setText(p.certType);
		per_detail_tv_brithday.setText(p.birthday);
		per_detail_tv_email.setText(p.email);
		per_detail_tv_mobile.setText(p.mobile);
		per_detail_tv_nickname.setText(p.nickname);
		per_detail_tv_userName.setText(p.userName);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        Bundle bundle =new Bundle();
		switch (v.getId()) {
		case R.id.per_detail_rl_brithday:
			bundle.putString("single_data", per_detail_tv_brithday.getText().toString());
			toActivityForResult(PerDetailsAct.this, PerSingleInforModify.class,
					CODE_BRITHDAY,bundle);
			break;
		case R.id.per_detail_rl_certno:
			bundle.putString("single_data", per_detail_tv_certno.getText().toString());
			toActivityForResult(PerDetailsAct.this, PerSingleInforModify.class,
					CODE_CERTNO,bundle);
			break;
		case R.id.per_detail_rl_certtype:
			bundle.putString("single_data", per_detail_tv_certtype.getText().toString());
			toActivityForResult(PerDetailsAct.this, PerSingleInforModify.class,
					CODE_CERTTYPE,bundle);
			break;
		case R.id.per_detail_rl_email:
			bundle.putString("single_data", per_detail_tv_email.getText().toString());
			toActivityForResult(PerDetailsAct.this, PerSingleInforModify.class,
					CODE_EMAIL,bundle);
			break;
		case R.id.per_detail_rl_mobile:
			bundle.putString("single_data", per_detail_tv_mobile.getText().toString());
			toActivityForResult(PerDetailsAct.this, PerSingleInforModify.class,
					CODE_MOBILE,bundle);
			break;
		case R.id.per_detail_rl_username:
			bundle.putString("single_data", per_detail_tv_userName.getText().toString());
			toActivityForResult(PerDetailsAct.this, PerSingleInforModify.class,
					CODE_USERNAME,bundle);
			break;
		case R.id.per_detail_rl_nickname:
			bundle.putString("single_data", per_detail_tv_nickname.getText().toString());
			toActivityForResult(PerDetailsAct.this, PerSingleInforModify.class,
					CODE_NICKNAME,bundle);
			break;
		case R.id.comm_right:
			request1();
			break;
		case R.id.comm_left:
			finish();
			break;

		}
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(data==null)
			return;
		Bundle bundle = data.getExtras();
		String dataString = bundle.getString("data").toString().trim();
		switch (requestCode) {
		case CODE_BRITHDAY:
			per_detail_tv_brithday.setText(dataString);
			break;
		case CODE_EMAIL:
			per_detail_tv_email.setText(dataString);
			break;
		case CODE_CERTNO:
			per_detail_tv_certno.setText(dataString);
			break;
		case CODE_USERNAME:
			per_detail_tv_userName.setText(dataString);
			break;
		case CODE_NICKNAME:
			per_detail_tv_nickname.setText(dataString);
			break;
		case CODE_MOBILE:
			per_detail_tv_mobile.setText(dataString);
			break;
		case CODE_CERTTYPE:
			per_detail_tv_certtype.setText(dataString);
			break;
		 

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public interface PassParamter {
		public void toPass(String str);
	}

	/**
	 * 
	 */
	private PerDetailsModel getJsonToobj(String json) {
		// TODO Auto-generated method stub  gson
		if(TextUtils.isEmpty(json))
			return null;
		PerDetailsModel model = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String retCode = jsonObject.optString("retCode");
			String retMessage = jsonObject.optString("retMessage");
			Boolean successful = jsonObject.optBoolean("successful");
			JSONObject content = jsonObject.optJSONObject("retContent");
			
			if( content==null)
				return null;
			model = new PerDetailsModel();
			model.retCode = retCode;
			model.retMessage = retMessage;
			model.userId = content.optLong("user_id");
			model.nickname = content.optString("nickName");
			model.email = content.optString("email");
			model.version = content.optString("version");
			SharedPrefUtil.putString(SharedPrefUtil.PERCHAGEVERSION, model.version);
			model.certType = content.optString("certType");
			model.certNo = content.optString("certNo");
			model.birthday = content.optString("birthday");
			model.mobile = content.optString("mobile");
			model.userName = content.optString("userName");
			model.successful = successful;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}
}
