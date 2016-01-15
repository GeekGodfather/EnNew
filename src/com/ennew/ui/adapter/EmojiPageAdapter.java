package com.ennew.ui.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ennew.R;
import com.ennew.utils.EmojiUtil;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 每页表情的适配器
 */
public class EmojiPageAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;
	private Context mContext;
	// 每页表情的数量
	private int mSize;
	// 当前表情页的页数
	private int mCurrentPage;
	// 所有的表情缓存
	private Map<String, String> mFaceMap;
	// 表情HashMap的key
	private List<String> mEmojiKeys = new ArrayList<String>();
	// 表情HashMap的value
	private List<String> mEmojiValues = new ArrayList<String>();

	public EmojiPageAdapter(Context context, int currentPage, int size) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mSize = size;
		mCurrentPage = currentPage;
		mFaceMap = EmojiUtil.getInstance().getFaceMap();
		initData();
	}

	// 初始化
	private void initData() {
		for (Map.Entry<String, String> entry : mFaceMap.entrySet()) {
			mEmojiValues.add(entry.getValue());
			mEmojiKeys.add(entry.getKey());
		}
	}

	@Override
	public int getCount() {
		return mSize;
	}

	@Override
	public Object getItem(int position) {
		// 计算表情key的实际位置
		int mEmojiKeyIndex = getCount() * mCurrentPage + position;
		if (mEmojiKeyIndex < mFaceMap.size())
			return mEmojiKeys.get(mEmojiKeyIndex);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (convertView == null) {
			rowView = mLayoutInflater.inflate(R.layout.emoji_cell, parent,
					false);
			ViewHolder viewHolder = new ViewHolder((ImageView) rowView);
			rowView.setTag(viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) rowView.getTag();
		// 计算表情的实际位置
		int mEmojiValueIndex = getCount() * mCurrentPage + position;
		if (mEmojiValueIndex < mFaceMap.size()) {
			// 根据资源名称获得表情的资源ID
			int resId = mContext.getResources().getIdentifier(
					EmojiUtil.STATIC_FACE_PREFIX
							+ mEmojiValues.get(mEmojiValueIndex), "drawable",
					mContext.getPackageName());
			viewHolder.imageView.setImageResource(resId);
			// 将当前表情的key作为标签设置到ImageView上
			viewHolder.imageView.setTag(viewHolder.imageView.getId(),
					mEmojiKeys.get(mEmojiValueIndex));
		} else {
			viewHolder.imageView
					.setBackgroundResource(android.R.color.transparent);
		}
		return rowView;
	}

	static class ViewHolder {
		public ImageView imageView;

		public ViewHolder(ImageView imageView) {
			this.imageView = imageView;
		};
	}
}
