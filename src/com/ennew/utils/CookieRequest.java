package com.ennew.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

public class CookieRequest extends JsonObjectRequest{

	private Map<String, String> mHeaders=new HashMap<String, String>(5);
	 
	public CookieRequest(String url, JSONObject jsonRequest, Listener<JSONObject> listener,
	ErrorListener errorListener) {
	super(url, jsonRequest, listener, errorListener);
	}
	 
	public CookieRequest(int method, String url, JSONObject jsonRequest, Listener<JSONObject> listener,
	ErrorListener errorListener) {
	super(method, url, jsonRequest, listener, errorListener);
	}
	 
	public void setCookie(String cookie){
	mHeaders.put("Cookie", cookie);
	}
	public void setCookie(String castgc,String jsessionid,String domain, String path){
		mHeaders.put("CASTGC", castgc);
		mHeaders.put("JSESSIONID", jsessionid);
		mHeaders.put("domain", domain);
		mHeaders.put("path", path);
		}
	 
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Log.v("tag", "1111111111111111");
	return mHeaders;
	}
}
