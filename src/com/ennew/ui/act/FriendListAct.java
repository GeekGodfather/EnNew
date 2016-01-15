package com.ennew.ui.act;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ennew.R;
import com.ennew.callback.ChatChangeListener;
import com.ennew.config.MyConstant;
import com.ennew.model.MessageInfo;
import com.ennew.service.XMPPService;
import com.ennew.ui.adapter.FriendsListAdapter;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.SharedPrefUtil;
import com.ennew.utils.ToastUtil;
import com.ennew.widget.SearchEditText;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 好友列表
 * 
 * @author jianglihui
 *
 */
public class FriendListAct extends BaseActivity implements ChatChangeListener {

	private InputMethodManager inputManager; // 软键盘
	private SearchEditText maintop_search; // 搜索输入框
	private ListView friends_lv;
	FriendsListAdapter friendsListAdapter;
	private List<RosterEntry> rosterEntries = new ArrayList<RosterEntry>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfriend);
		initData();
		setTitle();
		initView();
	}

	@Override
	protected void onDestroy() {
		Log.e("TAG", "-->>onDestroy");
		unbindXPPService();
		super.onDestroy();
	}
	/**
	 * 解除服务
	 */
	private void unbindXPPService() {
		try {
			unbindService(mServiceConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 设置Title
	public void setTitle() {
		RelativeLayout searchLayout = (RelativeLayout) findViewById(R.id.search_layout);
		searchLayout.setVisibility(View.VISIBLE);
		maintop_search = (SearchEditText) findViewById(R.id.maintop_search);
		ImageView title_left = (ImageView) findViewById(R.id.title_left);

		title_left.setVisibility(View.VISIBLE);

		Button message = (Button) findViewById(R.id.message_btn);
		message.setBackgroundResource(R.drawable.search_press);
		title_left.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();

			}
		});
		message.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!TextUtils.isEmpty(maintop_search.getText().toString())) {
					searchUser(maintop_search.getText().toString().trim());
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
		friendsListAdapter = new FriendsListAdapter(FriendListAct.this, rosterEntries, FriendListAct.this);
		friends_lv.setAdapter(friendsListAdapter);

		friends_lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent it = new Intent(FriendListAct.this, ChatActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("ToUserId", rosterEntries.get(position).getUser());
				it.putExtras(bundle);
				startActivity(it);
//				unbindXPPService();
//				finish();
				
				getVCard(rosterEntries.get(position).getUser());
			}
		});
		
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

	static XMPPService mXmppService;
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mXmppService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXmppService = ((XMPPService.XmppBind) service).getService();
			if (mXmppService != null) {
				mXmppService.setChatChangeListener(FriendListAct.this);
				// 如果没有连接上，则重新连接xmpp服务器
				if (!mXmppService.isAuthenticated()) {
					mXmppService.login(getState(MyConstant.ACCOUNT), getState(MyConstant.PASSWORD));
//					mXmppService.login("lihui1", "111111");

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
			new Handler().postDelayed(new Runnable() {
				public void run() {
					getFriends();
				}
			}, 1000);

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

	/**
	 * 搜索用户
	 */
	private void searchUser(final String friendName) {
		new Thread() {
			public void run() {
				ArrayList<String> users = new ArrayList<String>();
				UserSearchManager usm = new UserSearchManager(mXmppService.mXmppManager.mXmppConnection);
				Form searchForm = null;
				try {
					searchForm = usm.getSearchForm("search." + mXmppService.mXmppManager.mXmppConnection.getServiceName());
					Form answerForm = searchForm.createAnswerForm();
					answerForm.setAnswer("Username", true);
					answerForm.setAnswer("search", friendName);
					ReportedData data = usm.getSearchResults(answerForm, "search." + mXmppService.mXmppManager.mXmppConnection.getServiceName());
					// column:jid,Username,Name,Email
					Iterator<Row> it = data.getRows();
					Row row = null;
					while (it.hasNext()) {
						row = it.next();
						// Log.d("UserName",
						// row.getValues("Username").next().toString());
						// Log.d("Name",
						// row.getValues("Name").next().toString());
						// Log.d("Email",
						// row.getValues("Email").next().toString());
						// 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
						users.add(row.getValues("Username").next().toString());
					}
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				// 跳转页面
				Intent intent = new Intent(FriendListAct.this, SearchFriendAct.class);
				intent.putStringArrayListExtra("users", users);
				startActivity(intent);
			};
		}.start();
	}
	public void delFriend(final RosterEntry rosterEntry) {
		new Thread() {
			public void run() {
				try {
					// 添加好友
					Roster roster = FriendListAct.mXmppService.mXmppManager.mXmppConnection.getRoster();
					roster.removeEntry(rosterEntry);
					for (int i = 0; i < rosterEntries.size(); i++) {
						if(rosterEntries.get(i).equals(rosterEntry)){
							rosterEntries.remove(i);
							break;
						}
					}
					friendsListAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	public void  getVCard(String user) {
		ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
				new org.jivesoftware.smackx.provider.VCardProvider());
		VCard card = new VCard();
		try {
			card.load(mXmppService.mXmppManager.mXmppConnection, user);
			Log.v("tag1", "========FirstName()=====" + card.getFirstName() + card.getNickName());
			Log.v("tag1", "========getNickName()=====" + card.getNickName());
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getState(String key) {
		return (String) SharedPrefUtil.getState(this, String.class, null, key);
	}
}
