package com.ls.bluetoothcontrol;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.bluetoothcontrol.server.BTService;
import com.ls.bluetoothcontrol.util.CommonName;

public class MainActivity extends Activity implements OnClickListener {
	private BluetoothAdapter adapter;
	public static final int REQUEST_DISCOVERABLE = 1;
	private TextView showInforTextView;
	private ImageView btIcImageView;
	private Receiver receiver;
	private TimerTask mTimerTask;
	private Handler mHandler;
	private int imgState;
	private Timer mTimer;
	private Button opneFengshanView;
	private Button closeFengshanView;
	private Button feng1;
	private Button feng2;
	private Button feng3;
	private Button manual;
	private Button intel;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
		if (mTimer != null || mTimerTask != null) {
			mTimer.cancel();
			mTimerTask.cancel();
			mTimer = null;
			mTimerTask = null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initBT();
		initview();
		initTask();
		IntentFilter filter = new IntentFilter(CommonName.CONNECT_BT_STATE);
		receiver = new Receiver();
		registerReceiver(receiver, filter);
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					btIcImageView.setImageResource(R.drawable.btclose);
					break;
				case 1:
					btIcImageView.setImageResource(R.drawable.btnactive);
					break;
				default:
					break;
				}
			}

		};

	}

	private void initTask() {
		// TODO Auto-generated method stub
		mTimer = new Timer();
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (imgState == 0) {
					imgState = 1;
					mHandler.sendEmptyMessage(0);
				} else if (imgState == 1) {
					imgState = 0;
					mHandler.sendEmptyMessage(1);
				}
			}
		};
	}

	private void initBT() {
		// TODO Auto-generated method stub
		adapter = BluetoothAdapter.getDefaultAdapter();

	}

	private void initview() {
		// TODO Auto-generated method stub
		((Button) findViewById(R.id.mainAc_btn_closebt))
				.setOnClickListener(this);
		((Button) findViewById(R.id.mainAc_btn_openbt))
				.setOnClickListener(this);
		((Button) findViewById(R.id.mainAc_btn_searchbt))
				.setOnClickListener(this);
		opneFengshanView = (Button) findViewById(R.id.mainAc_btn_openfenshan);
		opneFengshanView.setOnClickListener(this);
		closeFengshanView = (Button) findViewById(R.id.mainAc_btn_closefenshan);
		closeFengshanView.setOnClickListener(this);
		feng1 = ((Button) findViewById(R.id.mainAc_btn_fen1));
		feng1.setOnClickListener(this);
		feng2 = ((Button) findViewById(R.id.mainAc_btn_fen2));
		feng2.setOnClickListener(this);
		feng3 = ((Button) findViewById(R.id.mainAc_btn_fen3));
		feng3.setOnClickListener(this);
		manual = ((Button) findViewById(R.id.mainAc_btn_mode_intel));
		manual.setOnClickListener(this);
		intel = ((Button) findViewById(R.id.mainAc_btn_mode_manual));
		intel.setOnClickListener(this);
		showInforTextView = (TextView) findViewById(R.id.mainAc_tv_inforshow);
		btIcImageView = (ImageView) findViewById(R.id.mainAc_img_btic);
		if (adapter.isEnabled()) {
			showInforTextView.setText("已开启蓝牙！");
		}
		setButtonIsClickable(false);
	}

	private void setButtonIsClickable(boolean b) {
		// TODO Auto-generated method stub
		closeFengshanView.setClickable(b);
		opneFengshanView.setClickable(b);
		feng1.setClickable(b);
		feng2.setClickable(b);
		feng3.setClickable(b);
		manual.setClickable(b);
		intel.setClickable(b);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.mainAc_btn_openbt:// 打开蓝牙
			openBt();
			break;
		case R.id.mainAc_btn_closebt:// 关闭蓝牙
			closeBt();
			break;
		case R.id.mainAc_btn_searchbt:// 搜索蓝牙
			searchBt();
			break;
		case R.id.mainAc_btn_fen1:// 1
			intent.setAction(CommonName.FENS_1);
			showInforTextView.setText("Ⅰ级风");
			break;
		case R.id.mainAc_btn_fen2:// 2
			intent.setAction(CommonName.FENS_2);
			showInforTextView.setText("Ⅱ级风");
			break;
		case R.id.mainAc_btn_fen3:// 3
			intent.setAction(CommonName.FENS_3);
			showInforTextView.setText("Ⅲ级风");
			break;
		case R.id.mainAc_btn_openfenshan:// 打开风扇
			intent.setAction(CommonName.OPEN_FENSHAN);
			showInforTextView.setText("已打开风扇！");
			break;
		case R.id.mainAc_btn_closefenshan:// 关闭风扇
			intent.setAction(CommonName.CLOSE_FENSHAN);
			showInforTextView.setText("已关闭风扇！");
			break;

		}
		sendBroadcast(intent);
	}

	private void searchBt() {// 搜索蓝牙设备
		// TODO Auto-generated method stub
		showInforTextView.setText("正在搜索！");
		if (!adapter.isEnabled()) {
			showInforTextView.setText("请开启蓝牙！");
			Toast.makeText(getApplicationContext(), "请开启蓝牙", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, BTService.class);
		startService(intent);
		Intent intent2 = new Intent();
		intent2.setClass(MainActivity.this, BTListActivity.class);
		startActivity(intent2);
	}

	private void closeBt() {// 關閉藍牙設備
		// TODO Auto-generated method stub
		adapter.disable();
		showInforTextView.setText("关闭蓝牙！");
		if (mTimer != null || mTimerTask != null) {
			mTimer.cancel();
			mTimerTask.cancel();
			mTimer = null;
			mTimerTask = null;
		}
	}

	private void openBt() {// 打开蓝牙设备
		// TODO Auto-generated method stub
		Intent enabler = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(enabler, REQUEST_DISCOVERABLE);
		showInforTextView.setText("开启蓝牙！");
	}

	class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(CommonName.CONNECT_BT_STATE)) {
				if (intent.getBooleanExtra(CommonName.CONNECT_BT_STATE, false)) {
					showInforTextView.setText("蓝牙连接成功！");
					mTimer.schedule(mTimerTask, 0, 500);
					setButtonIsClickable(true);
				} else {
					showInforTextView.setText("蓝牙连接失敗！");
					setButtonIsClickable(false);
				}
			}
		}
	}

}
