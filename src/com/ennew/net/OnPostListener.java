package com.ennew.net;


/**
 * 用于传回异WebService响就结果的接口
 * 
 * @author jianglihui
 * 
 */
public interface  OnPostListener {

	/**
	 * HTTP响应结果产生后的回调方法
	 * 
	 * @param result
	 * @param requestTpye
	 * @param activity
	 *            调用者的Activity 只有Super调用时用到，其它情况下为null
	 */
	public void onSuccess(String result, int requestTpye);
	public void onFailure(String result, int requestTpye);
	


}
