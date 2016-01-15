package com.ennew.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonParse {
	private static JsonParse mJsonParse;

	public static JsonParse getJsonParse() {
		if (mJsonParse == null) {
			mJsonParse = new JsonParse();
		}
		return mJsonParse;
	}
	/**
	 * 解析登录
	 * @param jsonStr
	 * @return
	 */
	public Map<String, Object> parseOfInfo(String jsonStr){
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		try {
			JSONObject jsonParser = new JSONObject(jsonStr);
			jsonMap.put("password", jsonParser.getString("password"));
			jsonMap.put("username", jsonParser.getString("username"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonMap;
	}
	
	
	
	
	
}
