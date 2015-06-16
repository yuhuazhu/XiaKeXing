package com.xiakexing.locate;

/**
 * <pre>
 * 根据卡尔曼滤波算法写的类，用于保存每个时刻的数据信息。
 * see at http://blog.chinaunix.net/uid-26694208-id-3184442.html
 * </pre>
 * 
 * @author Administrator
 * 
 */
public class Kalman {
	/** 测量值 */
	int valMeas;
	/** 估计值 */
	int valEsti;
	/** 最优值 */
	double valOpti;
	/** 估计值不确定度 */
	double uncerEsti;
	/** 测量值不确定度 */
	double uncerMeas;
	/** 估计值偏差 */
	double devEsti;
	/** 测量值偏差 */
	double devMeas;
	/** 最优值偏差 */
	double devOpti;
}
