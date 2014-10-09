package com.ls.bluetoothcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ls.bluetoothcontrol.util.CommonName;
import com.ls.bluetoothcontrol.util.ListAdapter;

public class BTListActivity extends Activity {
	public ListAdapter adapter;
	public List<BluetoothDevice> deviceList;
	public BluetoothDevice device;
	public BluetoothSocket socket;
	public BluetoothAdapter btAdapter;
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public ListView list1;
	public List<String> infor;
	public ProgressDialog pd;
	private boolean isDiscoverOver = false;
	private Receiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.btlist_activity);

		btAdapter = BluetoothAdapter.getDefaultAdapter();
		deviceList = new ArrayList<BluetoothDevice>();
		pd = new ProgressDialog(BTListActivity.this);
		list1 = (ListView) findViewById(R.id.btlistAc_lsv_bts);
		infor = new ArrayList<String>();
		IntentFilter filter = new IntentFilter(CommonName.DEVICE_STRING);
		receiver = new Receiver();
		registerReceiver(receiver, filter);
		new Work().execute(isDiscoverOver);

		list1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setAction(CommonName.CONNECT_BT);// ������������Service
				intent.putExtra("position", position);
				sendBroadcast(intent);
				intent.setClass(BTListActivity.this, MainActivity.class);// ��ת��MainAc
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		btAdapter.cancelDiscovery();
	}

	/**
	 * �������ؽ���������List
	 * 
	 * @author Administrator
	 * 
	 */
	protected class Work extends AsyncTask<Boolean, String, String> {
		@Override
		protected void onPostExecute(String result) {// ����listView
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// adapter = new ArrayAdapter<String>(BTListActivity.this,
			// R.id.item_tx_btname, infor);
			adapter = new ListAdapter(infor, BTListActivity.this);
			list1.setAdapter(adapter);// /////////////////?????
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd.setMessage("�������������Ժ󡭡�");
			pd.setIndeterminate(false);// �����ֵ��Сֵ���ƶ�
			pd.setCancelable(true);// ����ȡ��
			pd.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(Boolean... params) {
			// TODO Auto-generated method stub
			while (true) {
				if (isDiscoverOver) {
					pd.dismiss();
					break;
				}
			}
			return null;
		}
	}

	/**
	 * ������������������Ϣ
	 * 
	 * @author Administrator
	 * 
	 */
	class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();

			if (CommonName.DEVICE_STRING.equals(action))// ��������
			{
				infor = intent.getStringArrayListExtra("device");
				isDiscoverOver = true;
			}
		}
	}
}
