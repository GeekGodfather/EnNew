package com.ennew.net;

import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ennew.Application;
import com.ennew.config.MyConstant;
import com.ennew.ui.act.LoginAct;
import com.ennew.utils.PersistentCookieStore;
import com.ennew.utils.SharedPrefUtil;
import com.ennew.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class LoginRequest {
	public static CookieStore cookieStore = null;
	public static HttpUtils httpUtils;
	public static PersistentCookieStore myCookieStore = new PersistentCookieStore(Application.getInstance());

	// http.configCookieStore(cookieStore); //设置cookie

	public static void loginPost(String httpUrl, final String[] argNames, final Object[] paramsValues, final OnPostListener onPostListener, final int requestTpye, final String userName, final String pwd) {
		if (!isNetConnected(Application.getInstance())) {
			ToastUtil.showToast("网络连接异常", 0);
			return;
		}

		httpUtils = new HttpUtils();

		RequestParams httpParams = new RequestParams();
		if (null != argNames) {
			for (int i = 0; i < argNames.length; i++) {
				httpParams.addBodyParameter(argNames[i], paramsValues[i].toString());
			}
		}

		httpUtils.send(HttpMethod.POST, httpUrl, httpParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				onPostListener.onFailure(arg1, requestTpye);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				DefaultHttpClient httpClient = (DefaultHttpClient) httpUtils.getHttpClient();
				cookieStore = httpClient.getCookieStore();
				Log.v("tag1", "=============第一次登录=============" + cookieStore.getCookies().toString());
				
				JSONObject jsonObject = JSON.parseObject(arg0.result);
				if (null != jsonObject) {
					String status = jsonObject.getString("status");
					if ("0".equals(status)) {
						String data = jsonObject.getString("data");
						JSONObject dataObject = JSON.parseObject(data);
						String flowExecutionKey = dataObject.getString("execution");
						String loginTicket = dataObject.getString("lt");
						login2Post(httpUtils, MyConstant.LOGIN, MyConstant.argNameLogin2, new Object[] { "true", MyConstant.LOGIN_INIT, loginTicket, flowExecutionKey, userName, pwd, "submit" }, onPostListener, requestTpye);

					} else {
						onPostListener.onFailure(null, requestTpye);
					}

				}

			}
		});
	}

	public static void login2Post(final HttpUtils http, String httpUrl, final String[] argNames, final Object[] paramsValues, final OnPostListener onPostListener, final int requestTpye) {

		RequestParams httpParams = new RequestParams();
		if (null != argNames) {
			for (int i = 0; i < argNames.length; i++) {
				httpParams.addBodyParameter(argNames[i], paramsValues[i].toString());
			}
		}

		http.send(HttpMethod.POST, httpUrl, httpParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				onPostListener.onFailure(arg1, requestTpye);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				DefaultHttpClient httpClient = (DefaultHttpClient) http.getHttpClient();
				cookieStore = httpClient.getCookieStore();
				Log.v("tag1", "=============第二次登录=============" + cookieStore.getCookies().toString());
				List<Cookie> cookies = httpClient.getCookieStore().getCookies();
				
				 for (Cookie cookie:cookies){  
			            myCookieStore.addCookie(cookie);  
			        }
				JSONObject jsonObject = JSON.parseObject(arg0.result);
				if (null != jsonObject) {
					String retCode = jsonObject.getString("retCode");
					if ("0".equals(retCode)) {
						onPostListener.onSuccess(jsonObject.getString("statusInfo"), requestTpye);
					} else {
						onPostListener.onFailure(null, requestTpye);
					}
				}
			}
		});
	}

	/*
	 * public static void httpPost(CookieStore cs){ final DefaultHttpClient
	 * httpclient = new DefaultHttpClient();
	 * 
	 * httpclient.setCookieStore(cs); final HttpPost method = new
	 * HttpPost(MyConstant.GET_OPENFIRE_INFO); JSONObject jsonObject = new
	 * JSONObject(); try { jsonObject.put("platform", "3"); StringEntity entity
	 * = new StringEntity(jsonObject.toString(),"utf-8");
	 * entity.setContentEncoding("UTF-8");
	 * entity.setContentType("application/json"); method.setEntity(entity); }
	 * catch (UnsupportedEncodingException e) { e.printStackTrace(); }
	 * 
	 * 
	 * new Thread(){
	 * 
	 * @Override public void run() { try { Log.v("tag1",
	 * "=====httpclient.getCookieStore()======" +
	 * httpclient.getCookieStore().getCookies().toString()); HttpResponse result
	 * = httpclient.execute(method);
	 * 
	 * String resData = EntityUtils.toString(result.getEntity()); Log.v("tag1",
	 * "=========aaa===========" + resData); } catch(Exception e) {
	 * System.out.println("<-------Exception------->"); e.printStackTrace(); } }
	 * }.start(); }
	 */

	/**
	 * 判断网络是否链接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {

		int netType = getNetworkState(context);
		if (netType != NONE) {
			return true;
		}
		return false;
	}

	public final static int NONE = 0; // 无网络
	public final static int WIFI = 1; // Wi-Fi
	public final static int MOBILE = 2; // 3G,GPRS
	public final static int CONN_TIMEOUT = 20000;
	public final static int READ_TIMEOUT = 20000;

	/**
	 * 获取当前网络状态
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetworkState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connManager == null) {
			return NONE;
		}

		NetworkInfo mobileInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		State state;
		if (mobileInfo != null) {
			// 手机网络判断
			state = mobileInfo.getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return MOBILE;
			}
		}

		if (wifiInfo != null) {
			// Wifi网络判断
			state = wifiInfo.getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return WIFI;
			}
		}
		return NONE;
	}
	public static void saveState(String key, Object value) {
		SharedPrefUtil.saveState(Application.getInstance(), null, key, value);
	}
}
