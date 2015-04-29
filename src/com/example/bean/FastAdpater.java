package com.example.bean;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.R;

public class FastAdpater extends BaseAdapter {
//	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;

	public final class ListItemView {
		public ImageView img_left;
		public ImageView img_right;
		public TextView title;
	}

	public FastAdpater(Context context, List<Map<String, Object>> listItems) {
//		this.context = context;
		listContainer = LayoutInflater.from(context);
		this.listItems = listItems;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(R.layout.left_item, null);
			// 获得控件对象
			listItemView.img_left = (ImageView) convertView
					.findViewById(R.id.left_img1);
			listItemView.title = (TextView) convertView
					.findViewById(R.id.left_tv1);
			listItemView.img_right = (ImageView) convertView
					.findViewById(R.id.left_img2);
			// 设置空间集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		
		// 设置文字图片
		listItemView.img_left.setBackgroundResource((Integer) listItems.get(
				position).get("image"));
		listItemView.img_right.setBackgroundResource(R.drawable.ic_left_next);
		listItemView.title.setText((String) listItems.get(position)
				.get("title"));
		
		return convertView;
	}
}
