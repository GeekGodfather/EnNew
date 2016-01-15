package com.ennew.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.text.TextUtils;
import android.util.Log;
import cn.ennew.secret.SecretUtil;

import com.alibaba.fastjson.JSON;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ennew.Application;
import com.ennew.utils.CookieRequest;
import com.ennew.utils.PersistentCookieStore;
import com.ennew.utils.SharedPrefUtil;
import com.ennew.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.PreferencesCookieStore;

public class NetWorkUtils {
	public static RequestQueue mQueue = null;
	public static PersistentCookieStore cookieStore = null;

	/**
	 * json格式请求
	 * 
	 * @param httpUrl
	 *            请求地址
	 * @param jsonObject
	 * @param onPostListener
	 *            请求鉴听
	 * @param requestTpye
	 *            请求返回码
	 */
	public static void jsonDoPost(String httpUrl, JSONObject jsonObject, final OnPostListener onPostListener, final int requestTpye) {
		if (!isNetConnected(Application.getInstance())) {
			ToastUtil.showToast("网络连接异常", 0);
			return;
		}
		mQueue = Volley.newRequestQueue(Application.getInstance().getApplicationContext());
		if (LoginRequest.cookieStore != null) {
			getRequestQueue();
		}
		JsonObjectRequest stringRequest = new JsonObjectRequest(Method.POST, httpUrl, jsonObject, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ToastUtil.show(response.toString());
				onPostListener.onSuccess(response.toString(), requestTpye);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onPostListener.onFailure(null, requestTpye);
			}
		}) {
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json");
				headers.put("Content-Type", "application/json; charset=UTF-8");

				return headers;
			}
		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest);
	}

	/**
	 * json格式请求
	 * 
	 * @param httpUrl
	 *            请求地址
	 * @param jsonObject
	 * @param onPostListener
	 *            请求鉴听
	 * @param requestTpye
	 *            请求返回码
	 */
	public static void jsonDoPostSecret(String httpUrl, JSONObject jsonObject, final OnPostListener onPostListener, final int requestTpye) {
		if (!isNetConnected(Application.getInstance())) {
			ToastUtil.showToast("网络连接异常", 0);
			return;
		}
		mQueue = Volley.newRequestQueue(Application.getInstance().getApplicationContext());
		JSONObject secretJsonObject = null;
		try {
			String json = jsonObject.toString();
			String secretJson = SecretUtil.decodeData(json);
			secretJsonObject = new JSONObject(secretJson);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonObjectRequest stringRequest = new JsonObjectRequest(Method.POST, httpUrl, secretJsonObject, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.v("tag1", "======success==========" + response.toString());
				// onPostListener.onSuccess(response.toString(), requestTpye);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.v("tag1", "======success==========" + error.toString());
				// onPostListener.onFailure(null, requestTpye);

			}
		}) {
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json");
				headers.put("Content-Type", "application/json; charset=UTF-8");

				return headers;
			}
		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest);
	}

	/**
	 * 获取session 请求
	 * 
	 * @param httpUrl
	 *            请求地址
	 * @param jsonObject
	 * @param onPostListener
	 *            请求鉴听
	 * @param requestTpye
	 *            请求返回码
	 */
	public static void getSessionPost(String httpUrl, JSONObject jsonObject, final OnPostListener onPostListener, final int requestTpye) {
		if (!isNetConnected(Application.getInstance())) {
			ToastUtil.showToast("网络连接异常", 0);
			return;
		}
		mQueue = null;
		getLoginRequestQueue();
		JsonObjectPostRequest stringRequest = new JsonObjectPostRequest(httpUrl, jsonObject, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				Log.v("tag1", "=======response=============" + response.toString());
				com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
				if (null != jsonObject) {
					String retCode = jsonObject.getString("retCode");
					if ("0".equals(retCode)) {
						onPostListener.onSuccess(jsonObject.getString("retContent"), requestTpye);
					} else {
						onPostListener.onFailure(retCode, requestTpye);
					}
				}
				try {
					SharedPrefUtil.saveState(Application.getInstance(), null, "localCookie", response.getString("Cookie"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.v("tag1", "=======error=============" + error.toString());
				onPostListener.onFailure(null, requestTpye);
			}

		});
		/*String localCookieStr = getState("localCookie");
		if (!TextUtils.isEmpty(localCookieStr)) {
			stringRequest.setSendCookie(localCookieStr);// 向服务器发起post请求时加上cookie字段
		}*/
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest);
	}

	/**
	 * 同一session请求
	 * 
	 * @param httpUrl
	 * @param jsonObject
	 * @param onPostListener
	 * @param requestTpye
	 */
	public static void sameSessionPost(String httpUrl, JSONObject jsonObject, final OnPostListener onPostListener, final int requestTpye) {
		if (!isNetConnected(Application.getInstance())) {
			ToastUtil.showToast("网络连接异常", 0);
			return;
		}
		getLoginRequestQueue();
		CookieRequest stringRequest = new CookieRequest(httpUrl, jsonObject, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.v("tag1", "===========注册response=============" + response.toString());
				com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
				if (null != jsonObject) {
					String retCode = jsonObject.getString("retCode");
					if ("0".equals(retCode)) {
						onPostListener.onSuccess(jsonObject.getString("retContent"), requestTpye);
					} else {
						onPostListener.onFailure(retCode, requestTpye);
					}
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.v("tag1", "===========注册error=============" + error.toString());
				onPostListener.onFailure(null, requestTpye);

			}
		}) {

			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json");
				headers.put("Content-Type", "application/json; charset=UTF-8");
				return headers;
			}

		};
		String localCookieStr = getState("localCookie");
		if (!TextUtils.isEmpty(localCookieStr)) {
			stringRequest.setCookie(localCookieStr);// 向服务器发起post请求时加上cookie字段
		}
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest);
	}

	/**
	 * 带有登陆成功 返回cookie的 json 请求
	 * 
	 * @param httpUrl
	 * @param jsonObject
	 * @param onPostListener
	 * @param requestTpye
	 */
	public static void jsonPost(String httpUrl, JSONObject jsonObject, final OnPostListener onPostListener, final int requestTpye) {
		if (!isNetConnected(Application.getInstance())) {
			ToastUtil.showToast("网络连接异常", 0);
			return;
		}
		getRequestQueue();
		CookieRequest stringRequest = new CookieRequest(httpUrl, jsonObject, new Response.Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				Log.v("tag1", "========response=========" + response.toString());
				com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
				if (null != jsonObject) {
					String retCode = jsonObject.getString("retCode");
					if ("0".equals(retCode)) {
						onPostListener.onSuccess(jsonObject.getString("retContent"), requestTpye);
					} else {
						onPostListener.onFailure(null, requestTpye);
					}
				}

			}
		}, new Response.ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				Log.v("tag1", "========error=========" + error.toString());
				onPostListener.onFailure(null, requestTpye);

			}
		}) {

			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json");
				headers.put("Content-Type", "application/json; charset=UTF-8");
				return headers;
			}

		};

		stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest);
	}

	/**
	 * 普通POST请求
	 * 
	 * @param httpUrl
	 * @param argNames
	 * @param paramsValues
	 * @param onPostListener
	 * @param requestTpye
	 */

	public static void getResultDoPost(final String httpUrl, final String[] argNames, final Object[] paramsValues, final OnPostListener onPostListener, final int requestTpye) {

		if (!isNetConnected(Application.getInstance())) {
			ToastUtil.showToast("网络连接异常", 0);
			return;
		}

		HttpUtils httpUtils = new HttpUtils();
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
				com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(arg0.result);

			}
		});
	}

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

	private static String getState(String key) {
		return (String) SharedPrefUtil.getState(Application.getInstance(), String.class, null, key);
	}

	private static RequestQueue getRequestQueue() {
		if (mQueue == null) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			// 非持久化存储(内存存储) BasicCookieStore | 持久化存储 PreferencesCookieStore
			// CookieStore cookieStore = new
			// PreferencesCookieStore(Application.getInstance());
			cookieStore = new PersistentCookieStore(Application.getInstance().getApplicationContext());
			httpclient.setCookieStore(cookieStore);
			HttpStack httpStack = new HttpClientStack(httpclient);
			mQueue = Volley.newRequestQueue(Application.getInstance(), httpStack);
		}

		return mQueue;
	}

	private static RequestQueue getLoginRequestQueue() {
		if (mQueue == null) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			CookieStore cookieStore = new PreferencesCookieStore(Application.getInstance());
			httpclient.setCookieStore(cookieStore);
			HttpStack httpStack = new HttpClientStack(httpclient);
			mQueue = Volley.newRequestQueue(Application.getInstance(), httpStack);
		}

		return mQueue;
	}
}
