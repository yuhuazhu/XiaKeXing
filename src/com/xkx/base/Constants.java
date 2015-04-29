package com.xkx.base;

public class Constants {
	//public static String HTTP_HOST="http://117.25.182.242:28083/mmineapp/v1/";
	public static String HTTP_HOST="http://www.xmlyt.cn/";
	public static String HTTP_LOGIN="index/login.htm";                   //登录
	public static String HTTP_SIGN="mobile/index/sign.htm";                   //签名
	public static String HTTP_SIGNLIST="mobile/index/sign_list.htm";       //签名地址列表
	public static String HTTP_CITYDATA="opportunity/opportunity_manage/subordinate.htm";       //下载城市数据
	
	public static String HTTP_OPPUNITY="opportunity/opportunity_manage/subordinate_result.htm";       //下属客户数据
	public static String HTTP_CHANGEPWD="changePassword.do";      //淇敼瀵嗙爜             
	public static String HTTP_GETPOSITION="getPosition.do";       //浜哄憳瀹氫綅
	public static String HTTP_GETENVIRONMENT="getEnvironment.do"; //鐜鐩戞帶
	public static String HTTP_GETALARM="getAlarm.do";             //鎶ヨ缁熻
	public static String HTTP_VERSION="version.do";               //鐗堟湰鏇存柊
	public static String RESCULTCODE="0";  //璇锋眰鎴愬姛杩斿洖鐮?
	public static final int REFRESH= 1;     //Message
	public static String appVer="1";     //绋嬪簭鐗堟湰
	public static String flatVer="1";    //骞冲彴鐗堟湰
	public static int DELAY=50000;//30S杞
	public static int DELAY2=5000;//30S杞
	public static String device = "android";
	private Constants() {
	}

	// 配置
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	// 额外类
	public static class Extra {
		public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}
	
}
