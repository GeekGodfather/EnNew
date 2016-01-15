package com.ennew.ui.act;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ennew.R;
import com.ennew.config.MyConstant;
import com.ennew.event.SmsCodeVerif;
import com.ennew.net.NetWorkUtils;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.ToastUtil;

import de.greenrobot.event.Subscribe;

/**
 * @author lilong
 */

public class SmsCodeVerifAct extends BaseActivity {
    Button smscode_bt_verify;
    EditText smscode_et_getcode;
    String smscode;


    @Override
    protected int onCreatLayoutById() {
        return R.layout.activity_smscode_ver;
    }

    @Override
    protected void onInitView() {
        // TODO Auto-generated method stub
        smscode_bt_verify = (Button) findViewById(R.id.smscode_bt_verify);
        smscode_et_getcode = (EditText) findViewById(R.id.smscode_et_getcode);
        setHeadLeft(this, View.VISIBLE);
        setHeadRight(this, View.INVISIBLE);
        setHeadMid(null, View.VISIBLE, "验证");
    }

    @Override
    public void onSuccess(String result, int requestTpye) {
        // TODO Auto-generated method stub
        try {
            JSONObject jsonObject = new JSONObject(result);
            String code = jsonObject.optString("retCode");
            String messgae = jsonObject.optString("retMessage");
            if (code.equals(MyConstant.RETCODE)) {
                ToastUtil.show("验证通过");
                toActivity(PasswordRetrieveAct.class);
            }
            ToastUtil.show(messgae);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onSuccess(result, requestTpye);
    }

    @Override
    public void onFailure(String result, int requestTpye) {
        // TODO Auto-generated method stub
        ToastUtil.show("验证失败：" + result);
        super.onFailure(result, requestTpye);
    }


    public void request() {
        // TODO Auto-generated method stub
        JSONObject jsonObject = new JSONObject();
        smscode = smscode_et_getcode.getText().toString().trim();
        try {
            jsonObject.put("smsCode", smscode);
            jsonObject.put("platform", "3");
            jsonObject.put("type", "2");
            jsonObject.put("mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // NetWorkUtils.getResultDoPost();
        NetWorkUtils.jsonDoPost(MyConstant.CHECK_VERIFYCODE, jsonObject, this, 1);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.smscode_bt_verify:
                request();
                break;
            case R.id.comm_left:
                finish();
                break;

        }
    }

    String mobile;
    @Subscribe
    public void onEventMainThread(SmsCodeVerif event) {
        switch (event.getType()) {
            case SmsCodeVerif.mobile:
                mobile= (String) event.getData();
                onInitData();
                break;
        }

    }
}
