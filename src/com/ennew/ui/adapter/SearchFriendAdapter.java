package com.ennew.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ennew.R;
import com.ennew.model.Conversation;
import com.ennew.model.HomePage;
import com.ennew.ui.act.SearchFriendAct;

/**
 * 好友列表
 * 
 * @author jianglihui
 *
 */

public class SearchFriendAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<String> mList;
	private Context context;
	SearchFriendAct searchFriendAct;

	public SearchFriendAdapter(Context context, List<String> list, SearchFriendAct act) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		searchFriendAct = act;
		if (list != null) {
			this.mList = list;
		} else {
			mList = new ArrayList<String>();
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
			convertView = mInflater.inflate(R.layout.search_friendslist_item_layout, null);

			holder = new ViewHolder();
			holder.friend_name = (TextView) convertView.findViewById(R.id.friend_name);
			holder.add_friend = (TextView) convertView.findViewById(R.id.add_friend);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.friend_name.setText(mList.get(position).toString() );
		final String userName = mList.get(position).toString();
		holder.add_friend.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				searchFriendAct.addFriend(userName);
				
			}
		});

		return convertView;
	}

	private class ViewHolder {
		private TextView friend_name;
		private TextView add_friend;

	}

}
