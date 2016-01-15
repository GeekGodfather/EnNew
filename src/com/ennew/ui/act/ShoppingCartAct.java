package com.ennew.ui.act;

import com.ennew.R;
import com.ennew.utils.ToastUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 购物车
 * 
 * @author jianglihui
 *
 */
public class ShoppingCartAct extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoppingcart);
		setTitle();
	}

	private void setTitle() {
		ImageView title_left = (ImageView) findViewById(R.id.title_left);
		title_left.setVisibility(View.GONE);
	}

	// 连续两次点击返回键退出程序
	long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 3000) {
				ToastUtil.showToast("再按一次退出程序", 0);
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				// System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
