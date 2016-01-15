package com.ennew.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ennew.R;
import com.ennew.model.HomePage;

/**
 * 首页  适配器
 * 
 * @author jianglihui
 *
 */

public class HomePageAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HomePage> mList;
	private Context context;

	public HomePageAdapter(Context context, List<HomePage> list) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		if (list != null) {
			this.mList = list;
		} else {
			mList = new ArrayList<HomePage>();
		}
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getCount() == 0) {
			return null;
		}
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.homepage_item_layout, null);

			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HomePage homePage = mList.get(position);
		holder.name.setText(homePage.getName());
		holder.img.setImageBitmap(homePage.getImg());

		return convertView;
	}

	private class ViewHolder {
		private TextView name;
		private ImageView img;

	}

}
