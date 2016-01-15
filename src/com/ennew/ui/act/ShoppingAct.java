package com.ennew.ui.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.ennew.R;
import com.ennew.utils.ToastUtil;

/**
 * 社区
 * 
 * @author jianglihui
 *
 */
public class ShoppingAct extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping);
		setTitle();
	}
	
	private void setTitle(){
		Button title_right_button = (Button)findViewById(R.id.title_right_button);
		ImageView title_left = (ImageView) findViewById(R.id.title_left);
		title_left.setVisibility(View.GONE);
		title_right_button.setText("好友列表");
		title_right_button.setVisibility(View.VISIBLE);
		title_right_button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_right_button:
			Intent intent = new Intent(ShoppingAct.this, FriendListAct.class);
			startActivity(intent);
			break;

		}
		
	}
	// 连续两次点击返回键退出程序
		long exitTime = 0;
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

				if ((System.currentTimeMillis() - exitTime) > 3000) {
					ToastUtil.showToast("再按一次退出程序",0);
					exitTime = System.currentTimeMillis();
				} else {
					finish();
//					System.exit(0);
				}

				return true;
			}
			return super.onKeyDown(keyCode, event);
		}

}
