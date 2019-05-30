package com.rfstar.kevin.app;

import android.app.Application;
import android.content.pm.PackageManager;

public class App extends Application {
	public static final int BLUETOOTH_DATA_CHANNEL = 0; // 蓝牙数据通道 ffe5
	public static final int SERIAL_DATA_CHANNEL = 1; // 串口数据通道 ffe0
	public static final int ADC_INPUT = 2; // adc输入（2路）ffd0
	public static final int RSSI_REPORT = 3; // RSSI报告 ffa0
	public static final int PWM_OUTPUT = 4; // pwm输出（4路）ffb0
	public static final int BATTERY_REPORT = 5; // 电池电量报告 180f

	public static final int TURNIMING_OUTPUT = 6; // 定时翻转输出 fff0
	public static final int LEVEL_COUNTING_PULSE = 7; // 电平脉宽计数 fff0
	public static final int PORT_TIMING_EVENTS_CONFIG = 8;// 端口定时事件配置 fe00
	public static final int PROGRAMM_ABLEIO = 9; // 可编程io(8路) fff0
	public static final int DEVICE_INFORMATION = 10; // 设备信息 180a
	public static final int MODULE_PARAMETER = 11; // 模块参数设置 ff90
	public static final int ANTI_HIJACKINGKEY = 12;// 防劫持密钥 ffc0

	public static final String RFSTAR_DEVICE = "rfstar_device";

	public AppManager manager = null;

	public static final String TAG = "_TAG";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		manager = new AppManager(getApplicationContext());

	}
}
