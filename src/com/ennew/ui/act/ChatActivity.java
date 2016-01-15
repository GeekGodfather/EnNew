package com.ennew.ui.act;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ennew.R;
import com.ennew.app.AppManager;
import com.ennew.callback.AudioBtnOnClickListener;
import com.ennew.callback.ChatChangeListener;
import com.ennew.config.MyConstant;
import com.ennew.db.DataBaseManager;
import com.ennew.model.MessageInfo;
import com.ennew.net.NetWorkUtils;
import com.ennew.service.XMPPService;
import com.ennew.ui.adapter.ChatListAdapter;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.CommonUtil;
import com.ennew.utils.FileUtil;
import com.ennew.utils.ImageUtil;
import com.ennew.utils.SharedPrefUtil;
import com.ennew.utils.StringUtil;
import com.ennew.utils.ToastUtil;
import com.ennew.widget.EmojiEditText;
import com.ennew.widget.EmojiLinearLayout;
import com.ennew.widget.EmojiLinearLayout.onEmojiItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天页面
 */
@SuppressLint("ClickableViewAccessibility")
public class ChatActivity extends BaseActivity implements OnClickListener,
        onEmojiItemClickListener, OnTouchListener, ChatChangeListener {

    public static final int REQUEST_CODE_LOCAL = 0x19;
    public static final int REQUEST_CODE_CAMERA = 0x20;

    public static final String INTENT_TOUSERID = "ToUserId";

    private AppManager appManager;

    // 聊天信息展示列表
    private ListView mListView;

    // 聊天列表的适配器器
    private ChatListAdapter mAdapter;

    // 输入框
    private EmojiEditText mChatEditText;

    // 表情布局
    private EmojiLinearLayout emojiLinearLayout;

    // 显示表情的按钮
    private ImageView mEmojiSwitchButton;

    // 切换录音按钮
    private ImageView et_mode_keyboard;

    // 录音按钮
    private ImageView btn_press_to_speak;

    // 录音按钮布局
    private View btn_press_to_speak_layout;

    // 向下箭头按钮
    private View btn_arrow_down;

    // 更多按钮
    private ImageView activity_chat_more;

    // 更多菜单布局
    private LinearLayout activity_btn_container;

    // 录音提示
    private TextView recordingHint;

    // 按住说话的提示语
    private TextView speakPropmt;

    private PowerManager.WakeLock wakeLock;

    // 键盘相关
    private InputMethodManager mInputMethodManager;

    private List<MessageInfo> msgList;

    private boolean isShowSpeakBtn;

    private String friendAds = "";

    // 聊天后台服务
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
                mXmppService.setToUserId(friendAds);
                mXmppService.setChatChangeListener(ChatActivity.this);
                // 如果没有连接上，则重新连接xmpp服务器
                if (!mXmppService.isAuthenticated()) {
                    String userName = SharedPrefUtil.getStateString(
                            ChatActivity.this, MyConstant.ACCOUNT);
                    String password = SharedPrefUtil.getStateString(
                            ChatActivity.this, MyConstant.PASSWORD);
                    mXmppService.login(userName, password);
                }
            }
        }
    };
    private Handler handler = new Handler(Looper.getMainLooper());

    private File cameraFile;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (getIntent() != null) {
            friendAds = getIntent().getStringExtra(INTENT_TOUSERID);
        }

        initView();
        initListener();
        if (appManager == null) {
            appManager = AppManager.getAppManager();
            appManager.addActivity(this);
        }
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

    }

    // 初始化控件
    private void initView() {
        mListView = (ListView) findViewById(R.id.activity_chat_listView);
        mEmojiSwitchButton = (ImageView) findViewById(R.id.activity_chat_emoji);

        mChatEditText = (EmojiEditText) findViewById(R.id.activity_chat_editText);
        emojiLinearLayout = (EmojiLinearLayout) findViewById(R.id.emoji_linearLayout);
        et_mode_keyboard = (ImageView) findViewById(R.id.activity_chat_set_mode_keyboard);
        btn_press_to_speak = (ImageView) findViewById(R.id.activity_chat_press_to_speak);
        activity_chat_more = (ImageView) findViewById(R.id.activity_chat_more);
        activity_btn_container = (LinearLayout) findViewById(R.id.activity_btn_container);
        recordingHint = (TextView) findViewById(R.id.activity_chat_recording_hint);
        speakPropmt = (TextView) findViewById(R.id.activity_chat_speak_propmt);
        btn_press_to_speak_layout = findViewById(R.id.activity_chat_press_to_speak_layout);
        btn_arrow_down = findViewById(R.id.activity_chat_arrow_down);

        msgList = new ArrayList<MessageInfo>();
        mAdapter = new ChatListAdapter(this);
        mAdapter.setDatas(msgList);
        mListView.setAdapter(mAdapter);
    }

    // 设置监听
    private void initListener() {
        findViewById(R.id.activity_chat_send).setOnClickListener(this);
        findViewById(R.id.title_left).setOnClickListener(this);
        mEmojiSwitchButton.setOnClickListener(this);
        emojiLinearLayout.setOnEmojiItemClickListener(this);
        mChatEditText.setOnTouchListener(this);
        mListView.setOnTouchListener(this);
        et_mode_keyboard.setOnClickListener(this);
        btn_press_to_speak.setOnTouchListener(speakOnTouchListener);
        activity_chat_more.setOnClickListener(this);
        btn_arrow_down.setOnClickListener(this);

        mChatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s)) {
                    activity_chat_more.setVisibility(View.GONE);
                    findViewById(R.id.activity_chat_send).setVisibility(
                            View.VISIBLE);
                } else {
                    activity_chat_more.setVisibility(View.VISIBLE);
                    findViewById(R.id.activity_chat_send).setVisibility(
                            View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindXMPPService();
        getHistoryChat();
    }

    /**
     * 获取历史消息
     */
    private void getHistoryChat() {
        if (TextUtils.isEmpty(friendAds)) {
            return;
        }
        new Thread() {
            public void run() {
                String account = SharedPrefUtil.getStateString(
                        ChatActivity.this, MyConstant.ACCOUNT);
                msgList = DataBaseManager.getInstance()
                        .queryMessageInfosByFromIdAndToId(
                                account + MyConstant.SMACK_NAME_BASE,
                                friendAds, 0, 0);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.setDatas(msgList);
                        mAdapter.notifyDataSetChanged(true);
                        mListView.setSelection(mAdapter.getCount() - 1);
                    }
                });
            }

            ;
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard();
        if (wakeLock.isHeld())
            wakeLock.release();
        // 界面进入后台时停止正在播放的录音
        if (AudioBtnOnClickListener.isPlaying
                && AudioBtnOnClickListener.audioBtnOnClickListener != null) {
            AudioBtnOnClickListener.audioBtnOnClickListener.stopPlayVoice();
        }
        // 停止录音
        stopRecording();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindXPPService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ArrayList<String> imagePaths = null;
            if (requestCode == REQUEST_CODE_LOCAL) {
                if (data != null) {
                    imagePaths = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                    sendPicture(imagePaths);
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                Log.e("TAG", "cameraFile : " + cameraFile);
                if (cameraFile != null && cameraFile.exists()) {
                    imagePaths = new ArrayList<String>();
                    imagePaths.add(cameraFile.getAbsolutePath());
                    sendPicture(imagePaths);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left:
                finish();
                break;
            case R.id.title_right_button:
                Intent intent = new Intent(ChatActivity.this,
                        ConversationActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_chat_send:// 发送信息
                String msg = mChatEditText.getText().toString().trim();
                if (StringUtil.isEmpty(msg)) {
                    ToastUtil.showToast("请输入内容！", 0);
                    return;
                }
                if (!NetWorkUtils.isNetConnected(this)) {
                    ToastUtil.showToast("请检查网络！", 0);
                    return;
                }
                if (mXmppService != null) {
                    mXmppService.sendMessage(
                            msg, MyConstant.SEMMAGE_CONTENT_TYPE_TEXT,
                            MyConstant.CHAT_TYPE_SINGLE);
                } else {
                    ToastUtil.showToast("发送失败请重新发送", 0);
                }
                break;
            case R.id.activity_chat_emoji:// 点击显示隐藏表情布局
                activity_btn_container.setVisibility(View.GONE);
                if (emojiLinearLayout.getVisibility() == View.GONE) {
                    hideKeyboard();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isShowSpeakBtn = false;
                            et_mode_keyboard
                                    .setImageResource(R.drawable.chatting_setmode_voice_btn_normal);
                            emojiLinearLayout.setVisibility(View.VISIBLE);
                            mChatEditText.setVisibility(View.VISIBLE);
                            mChatEditText.requestFocus();
                            mChatEditText.setFocusable(true);
                            mChatEditText.setFocusableInTouchMode(true);
                            btn_press_to_speak_layout.setVisibility(View.GONE);
                            btn_arrow_down.setVisibility(View.GONE);
                        }
                    }, 150);
                } else {
                    emojiLinearLayout.setVisibility(View.GONE);
                    mEmojiSwitchButton
                            .setImageResource(R.drawable.chatting_biaoqing_btn_normal);
                }
                break;
            case R.id.activity_chat_set_mode_keyboard:// 语音文字切换
                if (!isShowSpeakBtn) {
                    isShowSpeakBtn = true;
                    if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN) {
                        hideKeyboard();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btn_press_to_speak_layout
                                        .setVisibility(View.VISIBLE);
                                btn_arrow_down.setVisibility(View.VISIBLE);
                            }
                        }, 150);
                    } else {
                        btn_press_to_speak_layout.setVisibility(View.VISIBLE);
                        btn_arrow_down.setVisibility(View.VISIBLE);
                    }

                    et_mode_keyboard
                            .setImageResource(R.drawable.chatting_setmode_keyboard_btn_normal);
                    mChatEditText.setVisibility(View.INVISIBLE);
                    emojiLinearLayout.setVisibility(View.GONE);
                    activity_btn_container.setVisibility(View.GONE);
                    mEmojiSwitchButton
                            .setImageResource(R.drawable.chatting_biaoqing_btn_normal);
                } else {
                    isShowSpeakBtn = false;
                    et_mode_keyboard
                            .setImageResource(R.drawable.chatting_setmode_voice_btn_normal);
                    btn_press_to_speak_layout.setVisibility(View.GONE);
                    btn_arrow_down.setVisibility(View.GONE);
                    mChatEditText.setVisibility(View.VISIBLE);
                    mChatEditText.requestFocus();
                    mChatEditText.setFocusable(true);
                    mChatEditText.setFocusableInTouchMode(true);
                    showKeyboard();
                }
                break;
            case R.id.activity_chat_more:// 显示更多
                hideKeyboard();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity_btn_container.setVisibility(View.VISIBLE);
                    }
                }, 150);
                et_mode_keyboard
                        .setImageResource(R.drawable.chatting_setmode_voice_btn_normal);
                mChatEditText.setVisibility(View.VISIBLE);
                btn_arrow_down.setVisibility(View.GONE);
                btn_press_to_speak_layout.setVisibility(View.GONE);
                emojiLinearLayout.setVisibility(View.GONE);
                mEmojiSwitchButton
                        .setImageResource(R.drawable.chatting_biaoqing_btn_normal);
                isShowSpeakBtn = false;
                break;
            case R.id.activity_chat_arrow_down:
                if (btn_press_to_speak_layout.getVisibility() == View.GONE) {
                    btn_press_to_speak_layout.setVisibility(View.VISIBLE);
                } else {
                    btn_press_to_speak_layout.setVisibility(View.GONE);
                }
                break;
        }

    }

    /*********************************
     * 发送录音开始
     *************************************/

    private MediaRecorder mMediaRecorder;

    private long recordingTime;

    // 录音文件的位置
    private String audioFilePath = "";

    private OnTouchListener speakOnTouchListener = new OnTouchListener() {

        @SuppressLint("Wakelock")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {

                    if (!CommonUtil.checkSDCard()) {
                        Toast.makeText(ChatActivity.this, "发送录音需要SD卡支持",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    audioFilePath = FileUtil.createAudioCacheFilePath("IM_"
                            + System.currentTimeMillis() + ".amr");
                    if (TextUtils.isEmpty(audioFilePath)) {
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        recordingHint.setVisibility(View.VISIBLE);
                        recordingHint.setText("手指上滑，取消发送");
                        recordingHint.setTextColor(getResources().getColor(
                                R.color.speak_hint_color));
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld()) {
                            wakeLock.release();
                        }
                        Toast.makeText(ChatActivity.this, "录音失败请重试",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    // 开始录音
                    startRecording();
                    speakPropmt.setVisibility(View.INVISIBLE);
                    recordingTime = System.currentTimeMillis();
                    return true;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint.setText("松开手指，取消发送");
                        recordingHint.setTextColor(Color.parseColor("#f94567"));
                    } else {
                        recordingHint.setText("手指上滑，取消发送");
                        recordingHint.setTextColor(getResources().getColor(
                                R.color.speak_hint_color));
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    speakPropmt.setVisibility(View.VISIBLE);
                    recordingHint.setVisibility(View.INVISIBLE);
                    File file = new File(audioFilePath);
                    v.setPressed(false);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {// 取消录音
                        stopRecording();
                        recordingTime = 0;
                        // 取消录音时删除已经录制的文件
                        if (file.exists()) {
                            file.delete();
                        }
                    } else {// 停止录音
                        stopRecording();
                        recordingTime = System.currentTimeMillis() - recordingTime;
                        recordingTime = recordingTime / 1000;
                        if(recordingTime > 0) {
                            if (file.exists()) {
                                new SendAudioThread(audioFilePath).start();
                            } else {
                                ToastUtil.showToast("发送录音失败", 0);
                            }
                        }else{
                            ToastUtil.showToast("录音时间过短", 0);
                        }
                    }
                    return true;
                }
                default:
                    return false;
            }

        }
    };

    /**
     * 开始录音
     */
    private void startRecording() {
        try {
            Toast.makeText(this, "开始录制", Toast.LENGTH_LONG).show();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置声音来源为麦克风
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);// 设置MediaRecorder录制的音频格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置MediaRecorder录制音频的编码为amr
            mMediaRecorder.setOutputFile(audioFilePath);// 设置录制好的音频文件保存路径

            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {

        }
    }

    /**
     * 停止录音
     */
    private void stopRecording() {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
        }
    }

    private class SendAudioThread extends Thread {
        private String path;

        public SendAudioThread(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            byte[] fileBytes = FileUtil.getFileContent(path);
            String encodeToString = Base64.encodeToString(fileBytes,
                    Base64.DEFAULT);
            if (mXmppService != null && !TextUtils.isEmpty(friendAds)) {
                mXmppService
                        .sendMessage(encodeToString,
                                MyConstant.SEMMAGE_CONTENT_TYPE_FILE,
                                MyConstant.CHAT_TYPE_SINGLE,
                                (int) recordingTime);

            }
        }
    }

    /*********************************
     * 发送录音结束
     *************************************/

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!v.performClick()) {
            switch (v.getId()) {
                case R.id.activity_chat_listView:// 点击ListView的时候隐藏键盘和表情
                    hideKeyboard();
                    activity_btn_container.setVisibility(View.GONE);
                    btn_press_to_speak_layout.setVisibility(View.GONE);
                    if (emojiLinearLayout.getVisibility() == View.VISIBLE) {
                        emojiLinearLayout.setVisibility(View.GONE);
                        mEmojiSwitchButton
                                .setImageResource(R.drawable.chatting_biaoqing_btn_normal);
                    }
                    break;
                case R.id.activity_chat_editText:// 点击输入框时隐藏表情
                    activity_btn_container.setVisibility(View.GONE);
                    if (emojiLinearLayout.getVisibility() == View.VISIBLE) {
                        emojiLinearLayout.setVisibility(View.GONE);
                        mEmojiSwitchButton
                                .setImageResource(R.drawable.chatting_biaoqing_btn_normal);
                        activity_btn_container.setVisibility(View.GONE);
                    }
                    showKeyboard();
                    break;
            }

        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && emojiLinearLayout.getVisibility() == View.VISIBLE) {
            emojiLinearLayout.setVisibility(View.GONE);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void receivedNewMessage(final MessageInfo msg) {
        mChatEditText.setText("");
        msgList.add(msg);
        if(msg.getContentType() == 3){
            mAdapter.notifyDataSetChanged(false);
        }else{
            mAdapter.notifyDataSetChanged(true);
        }
        mListView.setSelection(mAdapter.getCount() - 1);
    }

    @Override
    public void sendNewMessage(final MessageInfo msg) {
        ToastUtil.show("发送消息成功");
        mChatEditText.setText("");
        msg.setAudioFilePath(audioFilePath);
        msgList.add(msg);
        if(msg.getContentType() == 3){
            mAdapter.notifyDataSetChanged(false);
            DataBaseManager.getInstance().updateAudioFilePathByMessageId(msg);
        }else{
            mAdapter.notifyDataSetChanged(true);
        }
        mListView.setSelection(mAdapter.getCount() - 1);
    }

    @Override
    public void onEmojiItemClick(String emoji) {
        mChatEditText.insertEmoji(emoji);
    }

    /**
     * 绑定服务
     */
    private void bindXMPPService() {
        Intent mServiceIntent = new Intent(this, XMPPService.class);
        mServiceIntent.putExtra(INTENT_TOUSERID, friendAds);
        bindService(mServiceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
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

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN) {
            if (getCurrentFocus() != null)
                mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    /**
     * 隐藏软键盘
     */
    private void showKeyboard() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN) {
            mInputMethodManager.toggleSoftInput(0,
                    InputMethodManager.SHOW_IMPLICIT);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // 更多菜单按钮
    public void onMoreMenuClick(View v) {
        switch (v.getId()) {
            case R.id.activity_chat_take_picture:// 拍照
                selectPicFromCamera();
                break;
            case R.id.activity_chat_picture:// 相册
                selectPicFromLocal();
                break;
            default:
                break;
        }
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if (!CommonUtil.checkSDCard()) {
            ToastUtil.showToast("请检查SDCard", 0);
            return;
        }
        String cameraFileName = "EnNew_" + System.currentTimeMillis() + ".jpg";
        String cameraFilePath = CommonUtil.getImageSavePath(cameraFileName);
        cameraFile = new File(cameraFilePath);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                            MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                    REQUEST_CODE_CAMERA);
        }
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent = new Intent(this, PhotoPickerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * 发送图片
     */
    private void sendPicture(final ArrayList<String> filePaths) {
        if (!NetWorkUtils.isNetConnected(this)) {
            ToastUtil.showToast("请检查网络！", 0);
            return;
        }
        new Thread() {
            public void run() {
                for (String filePath : filePaths) {
                    String encodeStr = ImageUtil.filePathToString(filePath);
                    mXmppService.sendMessage(
                            encodeStr, MyConstant.SEMMAGE_CONTENT_TYPE_IMAGE,
                            MyConstant.CHAT_TYPE_SINGLE);
                }
            }
        }.start();
    }

    @Override
    public void sendMessageFailed(MessageInfo msg) {
        ToastUtil.show("消息发送失败，请重新发送");
    }

    @Override
    public void connectionStatusChanged(int mConnectedState, int errorCode) {
        switch (mConnectedState) {
            case XMPPService.DISCONNECTED:
                ToastUtil.showToast("连接断开 ： " + errorCode, 0);
                switch (errorCode) {
                    case XMPPService.CONNECTION_FAILED:
                        ToastUtil.showToast("连接异常关闭 ： " + errorCode, 0);
                        break;
                    case XMPPService.CONNECTION_TIMEOUT:
                        ToastUtil.showToast("连接超时 ： " + errorCode, 0);
                        break;
                    case XMPPService.NETWORK_ERROR:
                        ToastUtil.showToast("网络不可用 ： " + errorCode, 0);
                        break;
                    case XMPPService.LOGIN_ERROR:
                        ToastUtil.showToast("登陆失败 ： " + errorCode, 0);
                        break;
                    case XMPPService.LOGIN_FAILED:
                        ToastUtil.showToast("登陆异常 ： " + errorCode, 0);
                        break;
                }
                break;
            case XMPPService.CONNECTING:
                ToastUtil.showToast("正在连接", 0);
                break;
            case XMPPService.CONNECTED:
                ToastUtil.showToast("连接成功", 0);
                break;
        }
    }
}
