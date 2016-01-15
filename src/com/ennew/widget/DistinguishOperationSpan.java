package com.ennew.widget;

import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

/**
 * 处理识别出的网址或者电话号码
 */
public class DistinguishOperationSpan extends ClickableSpan {

	private String mUrl;

	public DistinguishOperationSpan(String url) {
		mUrl = url;
	}

	@Override
	public void onClick(View widget) {
		Toast.makeText(widget.getContext(), mUrl, Toast.LENGTH_LONG).show();
	}

}
