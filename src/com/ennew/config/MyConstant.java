package com.ennew.config;

/**
 * 常量类
 * 
 * @author jianglihui
 * 
 */
public class MyConstant {
	public static final int FILE_SAVE_TYPE_AUDIO = 0X00010;
	public static final int FILE_SAVE_TYPE_IMAGE = 0X00011;
	public static final String DEFAULT_AUDIO_SUFFIX = ".spx";
	public static final boolean ENNEW_DEBUG = false;
	public static String LOGIN_ID = "1001"; // 用户ID
	public static final int LOGIN_RESULT = 1006; // 登录返回码

	public static final int REGISTER_REQUEST_CODE = 10000; // 注册请求返回码
	public static final int VERIFYCODE_REQUEST_CODE = 10001; // 获取验证码请求返回码
	public static final int CHECK_VERIFYCODE_REQUEST_CODE = 10003; // 验证
																	// 验证码请求返回码
	public static final int LOGIN_REQUEST_CODE = 10004; // 登陆请求返回码
	public static final int GET_OFINFO_REQUEST_CODE = 10005; // 登录请求返回码
	public static final int LOGOUT_REQUEST_CODE = 10006; // 登出请求返回码

	/** retCode返回码值 */
	public static final String RETCODE = "200";
	public static final String RETCODE_REREGISTER = "10001";   //手机号码已经存在
	public static final String RETCODE_VERIFYCODE_ERROR = "10002";   //短信验证码错误
	public static final String RETCODE_LOGIN_ERROR = "10003";   //用户名或者密码错误
	public static final String RETCODE_REQUEST_ERROR = "10004";   //注册失败(非法请求)
	public static final String RETCODE_NOT_USER = "10005";   //用户不存在
	public static final String RETCODE_VERIFYCODE_OUTTIME = "10006";   //验证码失效
	public static final String RETCODE_VERIFYCODE_INTIME = "10007";   //获取验证码间隔时间短
	public static final String RETCODE_GETMSG_FAIL = "10008";   //短信发送失败
	public static final String RETCODE_OUT_VERIFYNO = "10009";   //超出短信验证码验证次数
	public static final String RETCODE_RETRIEVE_FAIL = "10010";   //找回失败
	public static final String RETCODE_UPLOAD_IMG_PARAM_ERROR = "10011";   //上传图片失败:参数问题
	public static final String RETCODE_UPLOAD_IMG_SERVICE_ERROR = "10012";   //上传图片失败:服务器问题
	public static final String RETCODE_UPLOAD_IMG_DEL_ERROR = "10013";   //上传图片删除失败
	public static final String RETCODE_CONSERVICE_OUTTIME = "10014";   //连接服务器超时
	public static final String RETCODE_USERID_ISNULL = "10015";   //用户用Id为空
	public static final String RETCODE_NO_LOGIN = "10016";   //登录超时或未登录
	public static final String RETCODE_USER_LOGIN = "10017";   //用户已登录,不允许重复登录
	/** 内部---http请求服务器地址 */
	public static final String SERVER_URL = "http://10.37.149.26:8080";

	// public static final String SERVER_URL = "http://10.4.44.77:10121";//崇楠IP
	// public static final String SERVER_URL = "http://10.37.149.26:8080";//罗龙IP
	// public static final String SERVER_URL = "http://www.xx.cn/services";

	public static final String GET_VERIFYCODE = SERVER_URL + "/nologin/smsCode"; // 获取验证码
	public static final String CHECK_VERIFYCODE = SERVER_URL + "/nologin/validSmsCode"; // 验证
																						// 验证码

	public static final String REGISTER = SERVER_URL + "/user/reg"; // 注册
	public static final String LOGIN_INIT = "http://10.37.149.26:8080/user/init?platform=3"; // 登录
	public static final String LOGIN = "http://10.37.149.27/login?"; // 登录
	public static final String LOGOUT = "http://10.37.149.27/logout?service=http://10.37.149.26:8080/user/logout"; // 登出
	public static final String GET_OPENFIRE_INFO = SERVER_URL + "/user/ofdetail"; // 获取openfire信息

	public static final String PERDETAILS_QUERY_INTERFACE = SERVER_URL + "/user/queryUserInfo"; // 个人信息详情
	public static final String PERDETAILS_CHANGE_INTERFACE = SERVER_URL + "/user/changeUserInfo"; // 修改个人信息
	public static final String PASSWORD_MODIFY_INTERFACE = SERVER_URL + "/user/updateUserInfoPwd"; // 重置密码
	public static final String PASSWORD_RETRIEVE_INTERFACE = SERVER_URL + "/user/findUserInfoPwdBack"; // 找回密码

	public static final String PERDETAILS_INTERFACE = SERVER_URL + "/user/queryUserInfo"; // 个人信息详情

	/**
	 * 聊天相关
	 */
	public static final int SMACK_PORT = 5222;
	public static final String SMACK_CUSTOM_SERVER = "10.37.149.13";
	public static final String SMACK_NAME_BASE = "@" + SMACK_CUSTOM_SERVER;
	public static final String SMACK_NAME_SUFFIX_SMACK = SMACK_NAME_BASE + "/Smack";
	public static final String SMACK_NAME_SUFFIX_PHONE = SMACK_NAME_BASE + "/iPhone";
	public static final boolean SMACK_DEBUG = false;
	public static final boolean SMACK_REQUIRE = false;

	/**
	 * 登录成功与否状态
	 */
	public static final String IS_LOGIN_SUCCESS = "isLoginSuccess";

	/**
	 * 用户名
	 */
	public static final String USER_NAME = "USER_NAME";

	/**
	 * openfire的用户名
	 */
	public static final String ACCOUNT = "ACCOUNT";
	/**
	 * openfire的用户密码
	 */
	public static final String PASSWORD = "PASSWORD";

	/**
	 * 文本类型
	 */
	public static final String SEMMAGE_CONTENT_TYPE_TEXT = "1";
	/**
	 * 图片类型
	 */
	public static final String SEMMAGE_CONTENT_TYPE_IMAGE = "2";
	/**
	 * 录音类型
	 */
	public static final String SEMMAGE_CONTENT_TYPE_FILE = "3";
	/**
	 * 单聊类型
	 */
	public static final String CHAT_TYPE_SINGLE = "0";
	/**
	 * 群聊类型
	 */
	public static final String CHAT_TYPE_GROUP = "1";

	public static final String[] argNameGetCaptcha = { "isCS", "service" };
	public static final String[] argNameLogin2 = { "isCS", "service", "lt", "execution", "username", "password", "_eventId" };

}
