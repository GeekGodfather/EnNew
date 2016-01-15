package com.ennew.ui.act;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ennew.R;
import com.ennew.annation.ViewInject;
import com.ennew.ui.base.BaseActivity;
import com.ennew.usualinterface.MyDialogFragmentInterface;
import com.ennew.usualinterface.PassParamter;
import com.ennew.utils.LogUtil;
import com.ennew.utils.ToastUtil;


import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lilong
 *
 */
public class PerSingleInforModify extends BaseActivity implements MyDialogFragmentInterface{

	Pattern pattern = Pattern.compile("(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)");


	@ViewInject(R.id.persingle_et_chang)
	 private EditText persingle_et_chang;

	@Override
	protected int onCreatLayoutById() {
		return R.layout.activity_persinglemodify ;
	}

	@Override
	protected void onInitView() {
		setHeadLeft(this, View.VISIBLE);
		setHeadRight(this, View.VISIBLE);
		setHeadMid(null, View.VISIBLE, "修改信息");
	}

	@Override
	protected void onInitData() {
		Bundle bundle=getIntent().getBundleExtra(ACTIVITY_BUNDLE);
		String dataString=bundle.getString("single_data");
		persingle_et_chang.setText(dataString);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.comm_left:
			finish();
			break;
		case R.id.comm_right:
			String  data=persingle_et_chang.getText().toString();
			if(!pattern.matcher(data).matches()){
				ToastUtil.show("日期不符！！！日期格式为yyyy-mm-dd");
				return;
			}
		    showDialog(PerSingleInforModify.this);
			break;	
		}
		super.onClick(v);
	}	
	
	@Override
    public void onBackPressed() {
        setData();
        super.onBackPressed();
    }

	private void setData() {
		String  data=persingle_et_chang.getText().toString();
		if(!pattern.matcher(data).matches()){
			ToastUtil.show("格式不正确！！！日期格式为0000-00-00");
			return;
		}
		// TODO Auto-generated method stub
		    Intent sIntent=new Intent();
		    Bundle bundle =new Bundle();
		    bundle.putString("data", data);
		    sIntent.putExtras(bundle);
		    setResult(0, sIntent);
		    finish();
	}

	@Override
	public void onDialogDone() {
		setData();
	}
	@Override
	public void onDialogUndone() {
		// TODO Auto-generated method stub
		
	}


	
	
	
}
