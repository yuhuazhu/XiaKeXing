package com.xiakexing.locate;

public class Location {
	static float[][] fourPoint = new float[4][3];

	static float[] distanceBetween4Point = new float[4];

	// public static void main(String[] args) {
	// try {
	// float[] point = new float[3];
	// Location loc = new Location();
	//
	// point[0] = 1.0F;
	// point[1] = 3.0F;
	// point[2] = 5.0F;
	// loc.setPoint(point, 1);
	//
	// point[0] = 2.0F;
	// point[1] = 4.0F;
	// point[2] = 6.0F;
	// loc.setPoint(point, 2);
	//
	// point[0] = 3.0F;
	// point[1] = 0.0F;
	// point[2] = 1.0F;
	// loc.setPoint(point, 3);
	//
	// point[0] = 5.0F;
	// point[1] = 3.0F;
	// point[2] = 2.0F;
	// loc.setPoint(point, 4);
	//
	// loc.setDistance((float) Math.sqrt(10.0f), 1);
	// loc.setDistance(3.0F, 2);
	// loc.setDistance((float) Math.sqrt(20.0f), 3);
	// loc.setDistance((float) Math.sqrt(11.0f), 4);
	//
	// float[] x = loc.location();
	// if (x == null) {
	// System.out.println("fail");
	// } else {
	// System.out.println(x[0] + "," + x[1] + "," + x[2]);
	// }
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

	public static void setPoint(float[] point, int num) throws Exception {
		int j = 0;

		for (j = 0; j < 3; j++) {
			fourPoint[(num - 1)][j] = point[j];
		}
	}

	/**
	 * 设置定位点到某一个点的距离
	 * 
	 * @param distance
	 * @param num
	 * @throws Exception
	 */
	public static void setDistance(float distance, int num) throws Exception {
		distanceBetween4Point[(num - 1)] = distance;
	}

	/**
	 * 定位点的坐标
	 * 
	 * @return
	 * @throws Exception
	 */
	public static float[] location() throws Exception {

		float[] locationPoint = new float[3];

		float[][] A = new float[3][3];

		float[] B = new float[3];
		int i = 0;
		int j = 0;

		for (i = 0; i < 3; i++) {
			B[i] = ((LocationMath.d_p_square(fourPoint[(i + 1)])
					- LocationMath.d_p_square(fourPoint[i]) - (distanceBetween4Point[(i + 1)]
					* distanceBetween4Point[(i + 1)] - distanceBetween4Point[i]
					* distanceBetween4Point[i])) / 2.0F);
		}

		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++) {
				A[i][j] = (fourPoint[(i + 1)][j] - fourPoint[i][j]);
			}

		}

		locationPoint = LocationMath.solve(A, B);

		return locationPoint;
	}

}