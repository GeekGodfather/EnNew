package com.ennew.ui.act;

import java.util.ArrayList;

import org.jivesoftware.smack.Roster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ennew.R;
import com.ennew.config.MyConstant;
import com.ennew.ui.adapter.SearchFriendAdapter;
import com.ennew.ui.base.BaseActivity;

/**
 * 搜索好友
 * 
 * @author jianglihui
 *
 */
public class SearchFriendAct extends BaseActivity implements OnClickListener {
	private ListView friends_lv;
	ArrayList<String> users = null;
	SearchFriendAdapter searchFriendAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friend_layout);
		initData();
		setTitle();
		initView();
	}

	private void setTitle() {
		ImageView title_left = (ImageView) findViewById(R.id.title_left);
		TextView title_center = (TextView) findViewById(R.id.title_center);
		title_center.setText("返回");

		title_left.setOnClickListener(this);
	}

	// 初始化view

	private void initView() {
		friends_lv = (ListView) findViewById(R.id.friends_lv);

		searchFriendAdapter = new SearchFriendAdapter(SearchFriendAct.this, users,SearchFriendAct.this);
		friends_lv.setAdapter(searchFriendAdapter);
		friends_lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent it = new Intent(SearchFriendAct.this, ChatActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("ToUserId", users.get(position).toString() + "/Smack");
				it.putExtras(bundle);
				startActivity(it);
			}
		});
	}

	private void initData() {
		// 判空
		if (null == (users = getIntent().getStringArrayListExtra("users"))) {
			this.finish();
		}

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.title_left:
			finish();
			break;

		}
	}
	public void addFriend(final String user) {
		new Thread() {
			public void run() {
				try {
					// 添加好友
					Roster roster = FriendListAct.mXmppService.mXmppManager.mXmppConnection.getRoster();
					roster.createEntry(user + "@" + MyConstant.SMACK_CUSTOM_SERVER, null, new String[] { "friends" });
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

}
