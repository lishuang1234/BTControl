package com.ls.bluetoothcontrol.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.R.integer;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;

import com.ls.bluetoothcontrol.util.CommonName;

public class BTService extends Service {
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public BluetoothDevice device;
	public BluetoothSocket socket;
	public BluetoothAdapter btAdapter;
	public List<BluetoothDevice> deviceList;
	private BroadcastReceiver receiver;
	// private boolean isDiscover;
	public ArrayList<String> infor;
	private Intent sentToBTList;
	private Intent sendToMainAc;
	private int position;
	public OutputStream outStream;
	public byte[] myByte;
	public InputStream inPutStream;
	private int sendInforString;
	private StringBuffer buffer;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		infor = new ArrayList<String>();
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		deviceList = new ArrayList<BluetoothDevice>();

		receiver = new Receiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(CommonName.CONNECT_BT);
		filter.addAction(CommonName.CLOSE_FENSHAN);
		filter.addAction(CommonName.CUT_CONNECT_BT);
		filter.addAction(CommonName.OPEN_FENSHAN);
		filter.addAction(CommonName.FENS_1);
		filter.addAction(CommonName.FENS_2);
		filter.addAction(CommonName.FENS_3);
		registerReceiver(receiver, filter);

		sentToBTList = new Intent();
		sendToMainAc = new Intent();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// isDiscover = false;
		btAdapter.startDiscovery();
		// TODO Auto-generated method stub
		// new Work().execute(isDiscover);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	/**
	 * �첽���������������豸��Ϣ
	 * 
	 * @author Administrator
	 * 
	 */
	/*
	 * protected class Work extends AsyncTask<Boolean, String, String> {
	 * 
	 * @Override protected void onPostExecute(String result) { // TODO
	 * Auto-generated method stub super.onPostExecute(result); }
	 * 
	 * @Override protected void onPreExecute() { // TODO Auto-generated method
	 * stub super.onPreExecute();
	 * 
	 * }
	 * 
	 * @Override protected void onProgressUpdate(String... values) { // TODO
	 * Auto-generated method stub super.onProgressUpdate(values); }
	 * 
	 * @Override protected String doInBackground(Boolean... params) { // TODO
	 * Auto-generated method stub while (true) { if (isDiscover) {
	 * System.out.println("stop ---Server");
	 * sentToBTList.setAction(CommonName.DEVICE_STRING);
	 * sentToBTList.putStringArrayListExtra("device", turnString(deviceList));
	 * sendBroadcast(sentToBTList);// ���ͻ�õ������б� break; } } return null; } }
	 */

	public ArrayList<String> turnString(List<BluetoothDevice> deviceList2) {
		// TODO Auto-generated method stub
		String[] infor1 = new String[deviceList2.size()];
		for (int i = 0; i < deviceList2.size(); i++) {
			infor1[i] = deviceList2.get(i).getName();
			if (infor.indexOf(infor1[i]) == -1) {
				infor.add(infor1[i]);
			}
		}
		return infor;
	}

	class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				deviceList.add(device);
				System.out.println("Discovery__ing" + device.getName() + "/n"
						+ device.getAddress());
				// ����Device����
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {// �������
				System.out.println("������ɣ�");
				// isDiscover = true;
				sentToBTList.setAction(CommonName.DEVICE_STRING);
				sentToBTList.putStringArrayListExtra("device",
						turnString(deviceList));
				sendBroadcast(sentToBTList);// ���ͻ�õ������б�

			} else if (action.equals(CommonName.CONNECT_BT)) {// ������������
				position = intent.getIntExtra("position", -1);
				if (position > -1) {
					connectBlue(position);
				}
			} else if (action.equals(CommonName.CUT_CONNECT_BT)) {// ȡ����������
				System.out.println("ȡ����������");
				try {
					cutConnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (action.equals(CommonName.OPEN_FENSHAN)) {// �򿪷���
				sendInforString = (byte) 0x55;
				new Oper().start();
				System.out.println("�������ȣ�");
			} else if (action.equals(CommonName.FENS_1)) {
				sendInforString = (byte) 0x33;
				new Oper().start();
				System.out.println("����1��");
			} else if (action.equals(CommonName.FENS_2)) {
				sendInforString = (byte) 0x22;
				new Oper().start();
				System.out.println("����2��");
			} else if (action.equals(CommonName.FENS_3)) {
				sendInforString = (byte) 0x11;
				new Oper().start();
				System.out.println("����3��");
			} else if (action.equals(CommonName.CLOSE_FENSHAN)) {// �رշ���
				sendInforString = (byte) 0x44;
				new Oper().start();
				System.out.println("�رշ��ȣ�");
			}
		}
	}

	public void cutConnect() throws IOException {
		// TODO Auto-generated method stub
		if (socket != null && outStream != null && inPutStream != null) {
			socket.close();
			outStream.flush();
			outStream.close();
			inPutStream.close();
		}
	}

	private void connectBlue(int position) {
		// TODO Auto-generated method stub
		device = btAdapter.getRemoteDevice(deviceList.get(position)
				.getAddress());
		System.out.println("BeforeContect~~~"
				+ deviceList.get(position).getAddress());

		new Connect().start();
	}

	/**
	 * �������������߳�
	 * 
	 * @author Administrator
	 * 
	 */
	class Connect extends Thread {
		// private BluetoothSocket tmp;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("BeforeContect~~~");
			try {
				socket = device.createRfcommSocketToServiceRecord(MY_UUID);
				btAdapter.cancelDiscovery();
				socket.connect();
				outStream = socket.getOutputStream();
				inPutStream = socket.getInputStream();
				System.out.println("Contect~~~");
				sendToMainAc.setAction(CommonName.CONNECT_BT_STATE);
				sendToMainAc.putExtra(CommonName.CONNECT_BT_STATE, true);// ���͹㲥�����ӳɹ�
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("ContectError~~~");
				sendToMainAc.putExtra(CommonName.CONNECT_BT_STATE, false);// ����ʧ��
				socket = null;
				outStream = null;
				outStream = null;
				inPutStream = null;

			}

			sendBroadcast(sendToMainAc);
			super.run();

		}
	}

	/**
	 * �����������ȡ����
	 * 
	 * @author Administrator
	 * 
	 */
	class Oper extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				outStream.write(sendInforString);// ������Ϣ
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.run();
		}
	}

}
