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
import android.widget.TextView;

import com.ennew.R;
import com.ennew.ui.act.FriendListAct;

/**
 * 好友列表
 * 
 * @author jianglihui
 *
 */

public class FriendsListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<RosterEntry> mList;
	private Context context;
	FriendListAct friendListAct;

	public FriendsListAdapter(Context context, List<RosterEntry> list, FriendListAct act) {
		this.context = context;
		friendListAct = act;
		mInflater = LayoutInflater.from(context);
		if (list != null) {
			this.mList = list;
		} else {
			mList = new ArrayList<RosterEntry>();
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
			convertView = mInflater.inflate(R.layout.friendslist_item_layout, null);

			holder = new ViewHolder();
			holder.friend_name = (TextView) convertView.findViewById(R.id.friend_name);
			holder.friend_del = (TextView) convertView.findViewById(R.id.friend_del);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.friend_name.setText(mList.get(position).getUser()  + ";"+ mList.get(position).getName());
		
		final RosterEntry rosterEntry = mList.get(position);
		holder.friend_del.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				friendListAct.delFriend(rosterEntry);
			}
		});
		return convertView;
	}

	private class ViewHolder {
		private TextView friend_name;
		private TextView friend_del;

	}

}
