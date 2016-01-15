package com.ennew.widget;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ennew.R;
import com.ennew.ui.adapter.EmojiPageAdapter;
import com.ennew.ui.adapter.EmojiViewPagerAdapter;
import com.ennew.utils.EmojiUtil;

/**
 * 自定义表情布局
 */
public class EmojiLinearLayout extends LinearLayout implements
		OnItemClickListener, View.OnTouchListener {

	private static final int EMOJI_COLUMN = 7;// 设置表情的列数
	private static final int EMOJI_ROW = 3; // 设置表情的行数

	// 表情滑动的ViewPager
	private ViewPager mViewPager;

	// 表情页数
	private int mViewPagerNum;

	private onEmojiItemClickListener mOnEmojiItemClickListener;

	public void setOnEmojiItemClickListener(
			onEmojiItemClickListener onEmojiItemClickListener) {
		mOnEmojiItemClickListener = onEmojiItemClickListener;
	}

	public interface onEmojiItemClickListener {
		void onEmojiItemClick(String emoji);
	}

	public EmojiLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EmojiLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EmojiLinearLayout(Context context) {
		this(context, null);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mViewPager = (ViewPager) findViewById(R.id.child_pager);
		// 获得表情页数
		mViewPagerNum = getEmojiSize();
		// 获得每页的表情布局
		ArrayList<GridView> mGridViews = new ArrayList<GridView>();
		for (int i = 0; i < mViewPagerNum; i++) {
			mGridViews.add(getGridViewLayout(LayoutInflater.from(getContext()),
					i));
		}
		// 表情的适配器
		EmojiViewPagerAdapter adapter = new EmojiViewPagerAdapter(mGridViews);
		mViewPager.setAdapter(adapter);
		// 设置指示器
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
	}

	// 初始化每页表情
	@SuppressLint("InflateParams")
	private GridView getGridViewLayout(LayoutInflater inflater, int i) {
		GridView mGridView = (GridView) inflater.inflate(R.layout.emoji_grid,
				null);
		EmojiPageAdapter emojiAdapter = new EmojiPageAdapter(getContext(), i,
				EMOJI_COLUMN * EMOJI_ROW);
		mGridView.setAdapter(emojiAdapter);
		mGridView.setOnTouchListener(this);
		mGridView.setOnItemClickListener(this);
		return mGridView;
	}

	// 获得表情的页数
	private int getEmojiSize() {
		// 获得表情的总数
		int size = EmojiUtil.getInstance().getFaceMap().size();
		if (size % EMOJI_COLUMN * EMOJI_ROW == 0) {// 刚好被整除
			return size / (EMOJI_COLUMN * EMOJI_ROW);
		}
		return size / (EMOJI_COLUMN * EMOJI_ROW) + 1;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String selectedStr = (String) view.getTag(view.getId());
		if (TextUtils.isEmpty(selectedStr)) {
			return;
		}
		// 调用自定义的回调函数
		if (mOnEmojiItemClickListener != null) {
			mOnEmojiItemClickListener.onEmojiItemClick(selectedStr);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 防止乱ViewPager乱滚动
		if (v.performClick()) {
			if (event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
				return true;
			}
		}
		return false;
	}
}
