package com.ennew.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.ennew.app.NetworkBroadcastReceiver;
import com.ennew.app.NetworkBroadcastReceiver.EventHandler;
import com.ennew.callback.ChatChangeListener;
import com.ennew.config.MyConstant;
import com.ennew.model.MessageInfo;
import com.ennew.net.NetWorkUtils;
import com.ennew.ui.act.ChatActivity;
import com.ennew.utils.CommonUtil;
import com.ennew.utils.SharedPrefUtil;
import com.ennew.utils.StringUtil;
import com.ennew.utils.XMPPManager;

/**
 * 聊天服务类
 */
public class XMPPService extends Service implements EventHandler {
    private static final String TAG = "XMPPService";

    /**
     * 手动退出，不需要重连
     */
    public static final int LOGOUT = 0x9100;
    /**
     * 网络不可用
     */
    public static final int NETWORK_ERROR = 0x9101;
    /**
     * 登录失败
     */
    public static final int LOGIN_ERROR = 0x0902;
    /**
     * 登录异常
     */
    public static final int LOGIN_FAILED = 0x0903;
    /**
     * 连接超时
     */
    public static final int CONNECTION_TIMEOUT = 0x0904;

    /**
     * 连接异常关闭
     */
    public static final int CONNECTION_FAILED = 0x0905;

    public XMPPManager mXmppManager;

    // 连接服务的线程
    private Thread mConnectingThread;

    /****************** 断线重连start ***********************/

    /**
     * 已经连接
     */
    public static final int CONNECTED = 0;
    /**
     * 连接已经断开
     */
    public static final int DISCONNECTED = -1;
    /**
     * 正在连接
     */
    public static final int CONNECTING = 1;
    private static final int RECONNECT_AFTER = 5;
    /**
     * 最大重连时间间隔
     */
    private static final int RECONNECT_MAXIMUM = 10 * 60;

    private static final String RECONNECT_ALARM = "com.ennew.xmpp.RECONNECT_ALARM";

    /**
     * 是否已经连接
     */
    private int mConnectedState = DISCONNECTED;
    /**
     * 重连超时时间
     */
    private int mReconnectTimeout = RECONNECT_AFTER;
    private PendingIntent mPAlarmIntent;

    /**
     * 是否是第一次登陆
     */
    private boolean isFirstLogin = true;

    /**
     * 聊天对象地址
     */
    private String touserid = "";

    /**
     * 自动重连广播
     */
    private BroadcastReceiver mAlarmReceiver = new ReconnectAlarmReceiver();

    /**
     * 自动重连广播
     */
    private class ReconnectAlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent i) {
            // 如果第一次登录没有成功就不进行重连
            if (isFirstLogin) {
                return;
            }
            if (mConnectedState != DISCONNECTED) {
                Log.d("XMPService",
                        "Reconnect attempt aborted: we are connected again!");
                return;
            }
            String account = SharedPrefUtil.getStateString(XMPPService.this,
                    MyConstant.ACCOUNT);
            String password = SharedPrefUtil.getStateString(XMPPService.this,
                    MyConstant.PASSWORD);
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                Log.d("XMPService", "username = null || password = null");
                return;
            }
            login(account, password);
        }
    }

    /****************** 断线重连end ***********************/

    /**
     * 消息监听发送成功或者接受到消息
     */
    private ChatChangeListener mChatChangeListener;

    public void setChatChangeListener(ChatChangeListener changeListener) {
        this.mChatChangeListener = changeListener;
    }

    private XmppBind mXmppBind = new XmppBind();
    private Handler mMainHandler = new Handler();

    public class XmppBind extends Binder {
        public XMPPService getService() {
            return XMPPService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "-->>Service onCreate");
        NetworkBroadcastReceiver.mListeners.add(this);
        // 注册自动重连的广播
        Intent mAlarmIntent = new Intent(RECONNECT_ALARM);
        mPAlarmIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        registerReceiver(mAlarmReceiver, new IntentFilter(RECONNECT_ALARM));
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            touserid = intent.getStringExtra(ChatActivity.INTENT_TOUSERID);
        }
        return mXmppBind;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        if (intent != null) {
            touserid = intent.getStringExtra(ChatActivity.INTENT_TOUSERID);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent != null) {
            touserid = intent.getStringExtra(ChatActivity.INTENT_TOUSERID);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TAG", "-->>Service onStartCommand");
        if (intent != null) {
            touserid = intent.getStringExtra(ChatActivity.INTENT_TOUSERID);
        }
        if (intent != null
                && intent.getAction() != null
                && TextUtils.equals(intent.getAction(),
                NetworkBroadcastReceiver.BOOT_COMPLETED_ACTION)) {
            String account = (String) SharedPrefUtil.getStateString(
                    XMPPService.this, MyConstant.ACCOUNT);
            String password = (String) SharedPrefUtil.getStateString(
                    XMPPService.this, MyConstant.PASSWORD);
            if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password))
                login(account, password);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        Log.e("TAG", "-->>Service onDestroy");

        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .cancel(mPAlarmIntent);// 取消重连闹钟
        unregisterReceiver(mAlarmReceiver);// 注销广播监听
        logout();
        super.onDestroy();
    }

    /**
     * 登录
     */
    public void login(final String name, final String pwd) {
        // 判断网络是否可用
        if (!CommonUtil.isNetAvailable(this)) {
            Log.e("TAG", "-->>网络不可用");
            postConnectionFailed(NETWORK_ERROR);
            return;
        }

        if (mConnectingThread != null) {
            Log.e("TAG", "-->>连接线程还存在");
            return;
        }
        mConnectingThread = new Thread() {
            @Override
            public void run() {
                try {
                    postConnecting();
                    mXmppManager = new XMPPManager(XMPPService.this);
                    mXmppManager.setToUserId(touserid);
                    boolean b = mXmppManager.login(name, pwd);
                    if (b) {// 登录成功
                        Log.e("TAG", "-->>登录 成功");
                        isFirstLogin = false;
                        postConnectionScuessed();
                        // 登陆成功保存当前登录的用户名以及密码
                        // SharedPrefUtil.saveState(XMPPService.this,
                        // MyConstant.ACCOUNT, name);
                        // SharedPrefUtil.saveState(XMPPService.this,
                        // MyConstant.PASSWORD, pwd);
                    } else {// 登录失败
                        Log.e("TAG", "-->>登录 失败");
                        postConnectionFailed(LOGIN_ERROR);
                    }
                } catch (Exception e) {
                    // 出现异常登录失败
                    e.printStackTrace();
                    Log.e("TAG", "-->>登录 异常");
                    postConnectionFailed(LOGIN_FAILED);
                } finally {
                    if (mConnectingThread != null)
                        synchronized (mConnectingThread) {
                            mConnectingThread = null;
                        }
                }
            }
        };
        mConnectingThread.start();
    }

    /**
     * 开始连接
     */
    protected void postConnecting() {
        mMainHandler.post(new Runnable() {
            public void run() {
                connecting();
            }
        });
    }

    protected void connecting() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mChatChangeListener != null) {
                    mChatChangeListener.connectionStatusChanged(CONNECTING, 0);
                }
            }
        });
    }

    /**
     * 登陆出现异常反馈给界面
     */
    public void postConnectionFailed(final int message) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                connectionFailed(message);
            }
        });
    }

    /**
     * 连接错误时进行的操作
     */
    private void connectionFailed(int reason) {
        mConnectedState = DISCONNECTED;// 更新当前连接状态
        if (reason == LOGOUT) {// 如果是手动退出
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                    .cancel(mPAlarmIntent);
            return;
        }
        // 回调
        if (mChatChangeListener != null) {
            mChatChangeListener
                    .connectionStatusChanged(mConnectedState, reason);
        }
        // 无网络连接时,直接返回
        if (!CommonUtil.isNetAvailable(this)) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                    .cancel(mPAlarmIntent);
            return;
        }

        String account = (String) SharedPrefUtil.getStateString(
                XMPPService.this, MyConstant.ACCOUNT);
        String password = (String) SharedPrefUtil.getStateString(
                XMPPService.this, MyConstant.PASSWORD);
        // 无保存的帐号密码时，也直接返回
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            Log.d(TAG, "account = null || password = null");
            return;
        }
        // 如果不是手动退出，则开启重连闹钟
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).set(
                AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + mReconnectTimeout * 1000, mPAlarmIntent);
        mReconnectTimeout = mReconnectTimeout * 2;
        if (mReconnectTimeout > RECONNECT_MAXIMUM) {
            mReconnectTimeout = RECONNECT_MAXIMUM;
        }
    }

    /**
     * 连接成功进行一些设置
     */
    protected void postConnectionScuessed() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                connectionScuessed();
            }
        });

    }

    private void connectionScuessed() {
        mConnectedState = CONNECTED;// 已经连接上
        mReconnectTimeout = RECONNECT_AFTER;// 重置重连的时间
        if (mChatChangeListener != null)
            mChatChangeListener.connectionStatusChanged(mConnectedState, 0);
    }

    /**
     * 设置聊天对象地址
     *
     * @param touserid
     */
    public void setToUserId(String touserid) {
        this.touserid = StringUtil.formatUserId(touserid);
        if (mXmppManager != null) {
            mXmppManager.setToUserId(touserid);
        }
    }

    public void sendMessage(String message,
                            String contentType, String messageType) {
        sendMessage(message, contentType, messageType, 0);
    }


    public void sendMessage(String message,
                            String contentType, String messageType, int duration) {
        if (mXmppManager != null) {
            mXmppManager.sendMessage(message, contentType, messageType, duration);
        }
    }

    /**
     * 是否连接上服务器
     */
    public boolean isAuthenticated() {
        if (mXmppManager != null) {
            return mXmppManager.isAuthenticated();
        }

        return false;
    }

    /**
     * 通知UI界面收到新消息
     */
    public void receiveMessage(final MessageInfo message) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mChatChangeListener != null) {
                    mChatChangeListener.receivedNewMessage(message);
                }
            }
        });

    }

    /**
     * 通知UI界面发送新消息
     */
    public void sendNewMessage(final MessageInfo message) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mChatChangeListener != null) {
                    mChatChangeListener.sendNewMessage(message);
                }
            }
        });

    }

    /**
     * 通知UI界面发送新消息
     */
    public void sendMessageFailed(final MessageInfo message) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mChatChangeListener != null) {
                    mChatChangeListener.sendMessageFailed(message);
                }
            }
        });

    }


    // 退出
    public boolean logout() {
        boolean isLogout = false;
        if (mConnectingThread != null) {
            synchronized (mConnectingThread) {
                try {
                    mConnectingThread.interrupt();
                    mConnectingThread.join(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mConnectingThread = null;
                }
            }
        }
        if (mXmppManager != null) {
            isLogout = mXmppManager.logout();
            mXmppManager = null;
        }
        postConnectionFailed(LOGOUT);// 手动退出
        return isLogout;
    }

    @Override
    public void onNetChange() {
        if (NetWorkUtils.getNetworkState(this) == NetWorkUtils.NONE) {// 如果是网络断开，不作处理
            connectionFailed(NETWORK_ERROR);
            return;
        }
        if (isAuthenticated())// 如果已经连接上，直接返回
            return;
        String account = (String) SharedPrefUtil.getStateString(
                XMPPService.this, MyConstant.ACCOUNT);
        String password = (String) SharedPrefUtil.getStateString(
                XMPPService.this, MyConstant.PASSWORD);
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password))// 如果没有帐号，也直接返回
            return;
        login(account, password);// 重连
    }

}
