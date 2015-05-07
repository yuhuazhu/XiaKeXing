package com.xkx.goldenox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class BottomFragment extends Fragment {
	private View rootView;// 缓存Fragment view
	private GifView GifView1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_bottom, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
//		initUI(rootView);

		return rootView;
	}

	
	private void initUI(View rootView) {

		GifView1 = (GifView) rootView.findViewById(R.id.GifView1);
		GifView1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int topleftx = x - 10;

				// left, top, right, bottom
				int y = (int) event.getY();
				int toplefty = y - 10;
				//textView1.setText("x=" + x + ",y=" + y);
				// 右上角
				int topRightx = x + 10;
				int topRighty = y - 10;

				// 左下角
				int bottomleftx = x - 10;
				int bottomlefty = y + 10;

				// 右上角
				int bottomRightx = x + 10;
				int bottomRighty = y + 10;

				// LayoutParams lp = new lay
				// textView1.setLayoutParams())
				if (event.getAction() == MotionEvent.ACTION_UP) {

					String title = "";

					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {

						title = "实景游览";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO 替换frament
						fragTrans.replace(R.id.menu_frame, new ShijinyulanFragment(), "");
						fragTrans.commit();
					}
					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {

						title = "福到印象";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO 替换frament
						fragTrans.replace(R.id.menu_frame, new BottomFragment(), "");
						fragTrans.commit();
					}
					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {

						title = "沿途风景";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO 替换frament
						fragTrans.replace(R.id.menu_frame, new YantuFragment(), "");
						fragTrans.commit();
					}

					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {

						title = "便民服务";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO 替换frament
						fragTrans.replace(R.id.menu_frame, new BianminFragment(), "");
						fragTrans.commit();
					}
					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {

						title = "游客互动";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO 替换frament
						fragTrans.replace(R.id.menu_frame, new YoukehudongFragment(), "");
						fragTrans.commit();
					}
					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {
						title = "地图查询";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO 替换frament
						fragTrans.replace(R.id.menu_frame, new MapserchFragment(), "");
						fragTrans.commit();
					}
					// if (event.getX() >= Integer.parseInt(xMap.get(2))
					// && event.getY() < Integer.parseInt(xMap.get(2))) {
					//
					// mapID = 1;
					// title = "引导台";
					// process(mapID, title);
					// }

				}
				return true;
			}
		});

	}
}
