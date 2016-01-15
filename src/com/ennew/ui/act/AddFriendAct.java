package com.ennew.ui.act;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ennew.R;
import com.ennew.callback.ChatChangeListener;
import com.ennew.model.MessageInfo;
import com.ennew.service.XMPPService;
import com.ennew.ui.adapter.FriendsListAdapter;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.ToastUtil;
import com.ennew.widget.SearchEditText;

/**
 * 添加好友
 * 
 * @author jianglihui
 *
 */
public class AddFriendAct extends BaseActivity implements ChatChangeListener {

	private InputMethodManager inputManager; // 软键盘
	private SearchEditText maintop_search; // 搜索输入框
	private ListView friends_lv;
	FriendsListAdapter friendsListAdapter;
	private List<RosterEntry> rosterEntries = new ArrayList<RosterEntry>();
	String userName = "lihui1";
	String pwd = "111111";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfriend);
		initData();
		setTitle();
		initView();
	}

	// 设置Title
	public void setTitle() {
		RelativeLayout searchLayout = (RelativeLayout) findViewById(R.id.search_layout);
		searchLayout.setVisibility(View.VISIBLE);
		maintop_search = (SearchEditText) findViewById(R.id.maintop_search);

		Button message = (Button) findViewById(R.id.message_btn);
		message.setBackgroundResource(R.drawable.search_press);
		message.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!TextUtils.isEmpty(maintop_search.getText().toString())) {
					// searchUsers(maintop_search.getText().toString().trim());
				} else {
					ToastUtil.showToast("请输入要搜索的内容", 0);
				}

			}
		});
	}

	// 初始化view

	private void initView() {
		inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		friends_lv = (ListView) findViewById(R.id.friends_lv);

		// getFriends();
		friendsListAdapter = new FriendsListAdapter(AddFriendAct.this, rosterEntries,null);
		friends_lv.setAdapter(friendsListAdapter);

		// ConnecMethod.login("lihui1", "111111");
		// // Intent intent = new Intent(this, ConnecService.class);
		// // startService(intent);
		// getFriends();
	}

	/**
	 * 屏幕点击事件 点击屏幕 隐藏软键盘
	 */

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				inputManager.hideSoftInputFromWindow(maintop_search.getWindowToken(), 0);
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}

		return onTouchEvent(ev);
	}

	/**
	 * 输入框的位置
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof SearchEditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			maintop_search.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + maintop_search.getHeight();
			int right = left + maintop_search.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}


	/**
	 * 获取所有好友的信息
	 */
	private void getFriends() {
		new Thread() {
			public void run() {
				rosterEntries.clear();

				Collection<RosterEntry> roscol = mXmppService.mXmppManager.mXmppConnection.getRoster().getEntries();
				Iterator<RosterEntry> iter = roscol.iterator();

				while (iter.hasNext()) {
					rosterEntries.add(iter.next());
				}
				Log.v("tag1", "========rosterEntries=======" + rosterEntries.size());
				insHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private Handler insHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				friendsListAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	private void initData() {
		bindXMPPService();

	}

	private void bindXMPPService() {
		Intent mServiceIntent = new Intent(this, XMPPService.class);
		bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	private XMPPService mXmppService;
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mXmppService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXmppService = ((XMPPService.XmppBind) service).getService();
			if (mXmppService != null) {
				mXmppService.setChatChangeListener(AddFriendAct.this);
				// 如果没有连接上，则重新连接xmpp服务器
				if (!mXmppService.isAuthenticated()) {
					mXmppService.login(userName, pwd);

				}
			}
		}
	};


	@Override
	public void sendMessageFailed(MessageInfo msg) {

	}

	@Override
	public void connectionStatusChanged(int mConnectedState, int errorCode) {
		if (mConnectedState == XMPPService.CONNECTED) {
			getFriends();
		}

	}

	@Override
	public void receivedNewMessage(MessageInfo msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendNewMessage(MessageInfo msg) {
		// TODO Auto-generated method stub
		
	}

}
