package com.ls.bluetoothcontrol.util;

import java.util.List;

import com.ls.bluetoothcontrol.BTListActivity;
import com.ls.bluetoothcontrol.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {
	private List<String> infor;
	private LayoutInflater inflater;
	private Activity activity;

	public ListAdapter(List<String> infor, Activity activity) {
		super();
		this.infor = infor;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return infor.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return infor.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			if (inflater == null) {
				inflater = LayoutInflater.from(activity);
			}
			convertView = inflater.inflate(R.layout.item_listview, null);
			viewHolder = new ViewHolder();
			viewHolder.btIc = (ImageView) convertView
					.findViewById(R.id.item_img_btic);
			viewHolder.btName = (TextView) convertView
					.findViewById(R.id.item_tx_btname);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.btName.setText(infor.get(position));

		return convertView;
	}

	class ViewHolder {
		TextView btName;
		ImageView btIc;
	}
}
