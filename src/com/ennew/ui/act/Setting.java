package com.ennew.ui.act;

import com.ennew.R;
import com.ennew.config.MyConstant;
import com.ennew.net.LoginRequest;
import com.ennew.net.NetWorkUtils;
import com.ennew.net.OnPostListener;
import com.ennew.utils.SharedPrefUtil;
import com.ennew.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 设置
 * 
 * @author jianglihui
 *
 */
public class Setting extends Activity implements OnClickListener{
	
	private TextView login_out; // 登出
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		setTitle();
		initView();
	}
	private void setTitle() {
		ImageView title_left = (ImageView) findViewById(R.id.title_left);
		TextView title_center = (TextView) findViewById(R.id.title_center);
		title_center.setText("返回");

		title_left.setOnClickListener(this);
	}
	private void initView(){
		login_out = (TextView) findViewById(R.id.login_out);
		login_out.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			finish();
			break;
		case R.id.login_out:
			NetWorkUtils.jsonPost(MyConstant.LOGOUT, null, onPostListener, MyConstant.LOGOUT_REQUEST_CODE);
			break;

		
		}
		
	}
	
	OnPostListener onPostListener = new OnPostListener() {

		@Override
		public void onSuccess(String result, int requestTpye) {
			switch (requestTpye) {
			case MyConstant.LOGOUT_REQUEST_CODE:
				ToastUtil.showToast("登出成功", 0);
				SharedPrefUtil.remove(Setting.this, "login");
				NetWorkUtils.mQueue = null;
				NetWorkUtils.cookieStore.clear();
				LoginRequest.httpUtils = null;
				Intent it = getIntent();
				setResult(1007, it);
				finish();
				
//				MainTabActivity main = (MainTabActivity) Setting.this.getParent();
//				main.changePhonenum(0);
//				Intent intent = new Intent(MyAct.this, LoginAct.class);
//				startActivity(intent);
//				System.exit(0);
				break;

			}

		}

		@Override
		public void onFailure(String result, int requestTpye) {
			switch (requestTpye) {
			case MyConstant.LOGOUT_REQUEST_CODE:
				ToastUtil.showToast("登出失败", 0);
				break;
			}
		}
	};
}
