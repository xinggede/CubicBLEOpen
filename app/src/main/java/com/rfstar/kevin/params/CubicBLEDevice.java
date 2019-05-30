package com.rfstar.kevin.params;

import com.rfstar.kevin.app.App;
import com.rfstar.kevin.main.BaseActivity;
import com.rfstar.kevin.main.MainActivity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class CubicBLEDevice extends BLEDevice {

	public CubicBLEDevice(Context context, BluetoothDevice bluetoothDevice) {
		// TODO Auto-generated constructor stub
		super(context, bluetoothDevice);
	}

	@Override
	protected void discoverCharacteristicsFromService() {
		// TODO Auto-generated method stub
		Log.d(App.TAG, "load all the services ");

		for (BluetoothGattService bluetoothGattService : bleService
				.getSupportedGattServices(device)) {
			String serviceUUID = Long.toHexString(
					bluetoothGattService.getUuid().getMostSignificantBits())
					.substring(0, 4);
			for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService
					.getCharacteristics()) {
				String characterUUID = Long.toHexString(
						bluetoothGattCharacteristic.getUuid()
								.getMostSignificantBits()).substring(0, 4);

			}
		}
	}

	/**
	 * 
	 * @param serviceUUID
	 * @param characteristicUUID
	 * @param value
	 */
	public void writeValue(String serviceUUID, String characteristicUUID,
			byte[] value) {
		// TODO Auto-generated method stub
		for (BluetoothGattService bluetoothGattService : bleService
				.getSupportedGattServices(device)) {
			String gattServiceUUID = Long.toHexString(
					bluetoothGattService.getUuid().getMostSignificantBits())
					.substring(0, 4);
			for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService
					.getCharacteristics()) {
				String characterUUID = Long.toHexString(
						bluetoothGattCharacteristic.getUuid()
								.getMostSignificantBits()).substring(0, 4);
				if (gattServiceUUID.equals(serviceUUID)
						&& characteristicUUID.equals(characterUUID)) {
					bluetoothGattCharacteristic.setValue(value);
					this.writeValue(bluetoothGattCharacteristic);
				}
			}
		}
	}
	/**
	 * 
	 * @param serviceUUID
	 * @param characteristicUUID
	 * @param data
	 */
	public void writeValue(String serviceUUID, String characteristicUUID,
			String value) {
		// TODO Auto-generated method stub
		for (BluetoothGattService bluetoothGattService : bleService
				.getSupportedGattServices(device)) {
			String gattServiceUUID = Long.toHexString(
					bluetoothGattService.getUuid().getMostSignificantBits())
					.substring(0, 4);
			for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService
					.getCharacteristics()) {
				String characterUUID = Long.toHexString(
						bluetoothGattCharacteristic.getUuid()
								.getMostSignificantBits()).substring(0, 4);
				if (gattServiceUUID.equals(serviceUUID)
						&& characteristicUUID.equals(characterUUID)) {

					int length = value.length();
					int lengthChar = 0;
					int position = 0;
					while (length > 0) {
						if (length > 20) {
							lengthChar = 20;
						} else if (length > 0) {
							lengthChar = length;
						} else {
							return;
						}
						String sendValue = value.substring(position, lengthChar+position);
						bluetoothGattCharacteristic.setValue(sendValue);
						bluetoothGattCharacteristic
								.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
						this.writeValue(bluetoothGattCharacteristic);		
						
						length -= lengthChar;
						position += lengthChar;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param serviceUUID
	 * @param characteristicUUID
	 */
	public void readValue(String serviceUUID, String characteristicUUID) {
		for (BluetoothGattService bluetoothGattService : bleService
				.getSupportedGattServices(device)) {
			String gattServiceUUID = Long.toHexString(
					bluetoothGattService.getUuid().getMostSignificantBits())
					.substring(0, 4);
			for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService
					.getCharacteristics()) {
				String characterUUID = Long.toHexString(
						bluetoothGattCharacteristic.getUuid()
								.getMostSignificantBits()).substring(0, 4);
				if (gattServiceUUID.equals(serviceUUID)
						&& characteristicUUID.equals(characterUUID)) {
					Log.d(App.TAG, "charaterUUID read is success  : "
							+ characterUUID);
					this.readValue(bluetoothGattCharacteristic);
				}
			}
		}
	}

	/**
	 * 
	 * @param serviceUUID
	 * @param characteristicUUID
	 */
	public void setNotification(String serviceUUID, String characteristicUUID,
			boolean enable) {
		for (BluetoothGattService bluetoothGattService : bleService
				.getSupportedGattServices(device)) {
			String gattServiceUUID = Long.toHexString(
					bluetoothGattService.getUuid().getMostSignificantBits())
					.substring(0, 4);
			for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService
					.getCharacteristics()) {
				String characterUUID = Long.toHexString(
						bluetoothGattCharacteristic.getUuid()
								.getMostSignificantBits()).substring(0, 4);
				if (gattServiceUUID.equals(serviceUUID)
						&& characteristicUUID.equals(characterUUID)) {
					this.setCharacteristicNotification(
							bluetoothGattCharacteristic, enable);
				}
			}
		}
	}

}
