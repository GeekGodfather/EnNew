package com.ennew.ui.fragment;

import java.util.zip.Inflater;

import com.ennew.R;
import com.ennew.usualinterface.MyDialogFragmentInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author lilong
 * 
 */

public class MyAlertFragment extends DialogFragment implements OnClickListener {
	public static MyAlertFragment newInstance(String title, String message) {
		MyAlertFragment adf = new MyAlertFragment();
		Bundle bundle = new Bundle();
		bundle.putString("alert-title", title);
		bundle.putString("alert-message", message);
		adf.setArguments(bundle);		 
		return adf;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		try {
			MyDialogFragmentInterface myDialogFragmentInterface=(MyDialogFragmentInterface) activity;
		} catch (Exception e) {
			// TODO: 未实现接口  异常处理
			
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.dialog_common, container, false);
		TextView title = (TextView) view.findViewById(R.id.tv_dialog_title);
		Button bt_ok = (Button) view.findViewById(R.id.bt_dialog_ok);
		Button bt_cancle = (Button) view.findViewById(R.id.bt_dialog_cancle);
		bt_ok.setOnClickListener(this);
		bt_cancle.setOnClickListener(this);
		return view;

	}
	
	
	
	
	 

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		MyDialogFragmentInterface myDialogFragmentInterface=(MyDialogFragmentInterface) getActivity();
		switch (arg0.getId()) {
		case R.id.bt_dialog_cancle:
			myDialogFragmentInterface.onDialogUndone();
			dismiss();
			break;
		case R.id.bt_dialog_ok:
			myDialogFragmentInterface.onDialogDone();
			dismiss();
			break;

		}

	}
	
	@Override //在onCreate中设置对话框的风格、属性等
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState); 
        //如果setCancelable()中参数为true，若点击dialog覆盖不到的activity的空白或者按返回键，则进行cancel，状态检测依次onCancel()和onDismiss()。如参数为false，则按空白处或返回键无反应。缺省为true 
        setCancelable(true); 
        //可以设置dialog的显示风格，如style为STYLE_NO_TITLE，将被显示title。遗憾的是，我没有在DialogFragment中找到设置title内容的方法。theme为0，表示由系统选择合适的theme。
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0; 
        setStyle(style,theme);  
    }
}
