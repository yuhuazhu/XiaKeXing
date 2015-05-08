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
import android.widget.Toast;

public class BottomFragment extends Fragment {
	private View rootView;// 缓存Fragment view
	private GifView GifView1;
	private int[] m_minX = { 8, 213, 600, 784, 699, 801 };
	private int[] m_minY = { 498, 213, 55, 218, 513, 710 };
	private int[] m_maxX = { 224, 356, 734, 973, 850, 996 };
	private int[] m_maxY = { 642, 397, 226, 339, 623, 828 };

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
		initUI(rootView);

		return rootView;
	}

	private void initUI(View rootView) {
		
		GifView1 = (GifView) rootView.findViewById(R.id.GifView1);
		GifView1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
//				Toast.makeText(getActivity(), "x=" + x + "y=" + y,
//						Toast.LENGTH_SHORT).show();
					for (int i = 0; i < m_minX.length; i++) {
						int x1 = m_minX[i];
						int x2 = m_maxX[i];
						int y1 = m_minY[i];
						int y2 = m_maxY[i];
						if (x < m_maxX[i] && x > m_minX[i] && y < m_maxY[i]
								&& y > m_minY[i]) {
							FragmentManager fragMgr = getActivity()
									.getSupportFragmentManager();
							FragmentTransaction fragTrans = fragMgr
									.beginTransaction();
							// TODO 替换frament
							Fragment fragment = null;
							switch (i) {
							case 0:
								fragment = new ShijinyulanFragment();
								break;
							case 1:
								fragment = new FudaoFragment();
								break;
							case 2:
								fragment = new YantuFragment();
								break;
							case 3:
								fragment = new BianminFragment();
								break;
							case 4:
								fragment = new YoukehudongFragment();
								break;
							case 5:
								fragment = new MapserchFragment();
								break;

							default:
								break;
							}
//							fragTrans.hide(BottomFragment.this);
							fragTrans.add(R.id.menu_frame, fragment, "");
							fragTrans.addToBackStack(null);
							fragTrans.commit();
						}
					}
				return false;
			}
		});

	}
}
