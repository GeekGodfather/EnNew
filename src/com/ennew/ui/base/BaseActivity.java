package com.ennew.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ennew.R;
import com.ennew.annation.InitInjectedView;
import com.ennew.app.EnnewAcitivityList;
import com.ennew.config.MyConstant;
import com.ennew.event.BaseEvent;
import com.ennew.net.OnPostListener;
import com.ennew.utils.DialogUtil;
import com.ennew.utils.MyCrashHandler;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


public class BaseActivity extends Activity implements OnPostListener
         ,OnClickListener{

    private static String TAG = "BaseActivity";
    public static final int BASE = 100;
    public static final int MENU_EXIT = BASE + 3;
    public static final int MENU_SHARE = BASE + 2;
    public static final int MENU_FEEDBACK = BASE + 1;
    private static SharedPreferences sharedPreferences;
    public static boolean isShow = false;
    public static String ACTIVITY_BUNDLE = "activity_bundle";
    protected EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        EnnewAcitivityList.getInstance().addActivity(this);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        // 捕获处理
        if (MyConstant.ENNEW_DEBUG) {
            MyCrashHandler crashHandler = MyCrashHandler.getInstance();
            crashHandler.init(this, getApplicationContext());
        }
        int id=onCreatLayoutById();
        if(id>0){
            setContentView(id);
            onInitView();
            onInitData();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setAnntion(this);

    }

    protected void onInitView() {

    }

    protected void onInitData() {

    }
    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isShow = false;
    }

    @Override
    protected void onStop() {
        isShow = false;
        super.onStop();
    }

    public static boolean getShow() {
        return isShow;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    protected void toActivity(Class<?> cls) {
        toActivity(getApplicationContext(), cls, null);
    }

    protected void toActivity(Class<?> cls, Bundle bundle) {
        toActivity(getApplicationContext(), cls, bundle);
    }

    protected void toActivity(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(ACTIVITY_BUNDLE, bundle);
        startActivity(intent);
    }

    /**
     * 获取回调的start
     */
    protected void toActivityForResult(Context context, Class<?> cls,
                                       int requestCode) {
        toActivityForResult(context, cls, requestCode, null);
    }

    protected void toActivityForResult(Context context, Class<?> cls,
                                       int requestCode, @Nullable Bundle options) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(ACTIVITY_BUNDLE, options);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
        isShow = false;
        EnnewAcitivityList.getInstance().removeActivity(this);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onMenuOpened(int featureId, android.view.Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onSuccess(String result, int requestTpye) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFailure(String result, int requestTpye) {
        // TODO Auto-generated method stub

    }

    protected View setHeadLeft(OnClickListener listener, int visble) {
        return setHead(listener, visble, "", 0);

    }

    protected View setHeadRight(OnClickListener listener, int visble) {
        return setHead(listener, visble, "", 1);
    }

    protected View setHeadMid(OnClickListener listener, int visble,
                              String content) {
        return setHead(listener, visble, content, 2);

    }

    /**
     * 设置头部操作 @ @ listener 监听 @ visble 控件隐藏
     */
    protected View setHead(OnClickListener listener, int visble,
                           String content, int leftandright) {
        if (leftandright == 2) {
            TextView view = (TextView) findViewById(R.id.comm_title);
            view.setText(content);
            view.setTextColor(getResources().getColor(R.color.black));
            return view;
        } else if (leftandright == 0) {
            View view = findViewById(R.id.comm_left);
            view.setOnClickListener(listener);
            view.setVisibility(visble);
            return view;
        } else {
            View view = findViewById(R.id.comm_right);
            view.setOnClickListener(listener);
            view.setVisibility(visble);
            return view;
        }
    }
    /**
     * 添加注解
     *
     * @param activity
     */
    protected void setAnntion(Activity activity) {
            new InitInjectedView(activity);

    }
    /**
     * 显示dialog
     */
    protected void showDialog(Activity activity) {
        // TODO Auto-generated method stub
        DialogUtil.showProgressDialog(activity, "");
    }
    @Subscribe
    public void onEvent(BaseEvent baseEvent) {

    }

    protected int onCreatLayoutById(){
        return -1;
    }


    @Override
    public void onClick(View v) {

    }
}
