package com.ennew.ui.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * 表情滑动页的适配器
 */
public class EmojiViewPagerAdapter extends PagerAdapter {

	private ArrayList<GridView> mGridViews;

	public EmojiViewPagerAdapter(ArrayList<GridView> mGridViews) {
		this.mGridViews = mGridViews;
	}

	@Override
	public int getCount() {
		return mGridViews == null ? 0 : mGridViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mGridViews.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = mGridViews.get(position);
		container.addView(view);
		return view;
	}

}
