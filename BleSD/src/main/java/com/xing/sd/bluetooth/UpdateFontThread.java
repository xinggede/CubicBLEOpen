package com.xing.sd.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.xing.sd.bean.CurrentImageInfo;
import com.xing.sd.bean.ImageType;
import com.xing.sd.util.CommandUtil;
import com.xing.sd.util.FileParseUtil;
import com.xing.sd.util.FormatUtil;
import com.xing.sd.util.ParseUtil;
import com.xing.sd.util.Tool;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import androidx.annotation.NonNull;

public class UpdateFontThread extends Thread {

    private boolean stop = false;
    private BluetoothGatt bluetoothGatt;

    private BluetoothGattCharacteristic curBC;
    private Handler handler;
    private Uri uri;
    private Context context;

    private volatile boolean rwBusy = false;

    private volatile boolean READ_NULL=false;

    private byte[] readBuffer;

    public UpdateFontThread(BluetoothGatt bluetoothGatt, Handler handler) {
        this.bluetoothGatt = bluetoothGatt;
        this.handler = handler;
    }

    public void startUpdate(Context context, Uri uri){
        this.uri = uri;
        this.context = context;
        start();
    }

    public void setRwBusy(boolean rwBusy) {
        this.rwBusy = rwBusy;
    }


    public void setREAD_NULL(boolean READ_NULL) {
        this.READ_NULL = READ_NULL;
    }

    public void setReadData(byte[] data){
        this.readBuffer = data;
    }
    public boolean isStop() {
        return stop;
    }


    public void closeUpdateThread() {
        stop = true;
        interrupt();
    }

    @Override
    public void run() {
        if(init()){
            Tool.logd("字库文件: " + uri.toString());
            InputStream inputStream = Tool.uriToInputStream(context, uri);
            start(inputStream);
        } else {
            ResultData resultData = new ResultData(ResultData.UPDATE_FONT, null);
            resultData.data = "字库升级通道查找失败";
            callback(false, resultData);
        }
        stop = true;
    }

    private void callback(boolean ok, ResultData resultData){
        Message.obtain(handler, ok?States.UPDATE_OK:States.UPDATE_ERROR, resultData).sendToTarget();
    }

    private void start(InputStream inputStream) {
        stop = false;
        ResultData resultData = new ResultData(ResultData.UPDATE_FONT, curBC.getUuid().toString());
        if(inputStream == null){
            resultData.data = "字库文件读取错误";
            callback(false, resultData);
            return;
        }
        while(!stop){
            byte[] response = readResponse();
            if(response != null && response.length >= 2){
                Tool.loge("font response: " + FormatUtil.bytesToHexString(response));
                resultData.data = "数据准备中:" + FormatUtil.bytesToHexString(response);
                callback(false, resultData);
                if(response[1] == 0x04){
                    break;
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
        }
        if(stop){
            return;
        }

        //读取文件
        Tool.logd("读取文件");
        ByteBuffer byteBuffer = FileParseUtil.parseBinFile(inputStream);
        if(byteBuffer==null){
            Tool.loge("字库文件解析错误");
            resultData.data = "字库文件解析错误";
            callback(false, resultData);
            return;
        }
        int total=  byteBuffer.capacity();
        Tool.logd("字库文件大小: "+ total);
        byte[] realBuffer = new byte[byteBuffer.remaining()];
        byteBuffer.get(realBuffer);
        if(stop){
            return;
        }
        int sendLen = write(realBuffer, realBuffer.length, resultData);
        if(stop){
            return;
        }
        if(sendLen == realBuffer.length){
            resultData.data = "字库升级成功";
            callback(true, resultData);
        } else {
            resultData.data = "字库升级失败: " + sendLen +"/" + realBuffer.length;
            callback(false, resultData);
        }
    }

    private boolean init(){
        if(bluetoothGatt == null){
            return false;
        }
        List<BluetoothGattService> gattServices = getServices(bluetoothGatt);
        for (BluetoothGattService gattService : gattServices) {
            String gattServiceUUID = Long.toHexString(gattService.getUuid().getMostSignificantBits()).substring(0, 4);
            if (gattServiceUUID.equals(CommandUtil.FONT_SID)) {
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : gattService.getCharacteristics()) {
                    String characterUUID = Long.toHexString(bluetoothGattCharacteristic.getUuid().getMostSignificantBits()).substring(0, 4);
                    if (characterUUID.equals(CommandUtil.FONT_CID)) {
                        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        curBC = bluetoothGattCharacteristic;
                        break;
                    }
                }
            }
        }
        return curBC != null;
    }

    private List<BluetoothGattService> getServices(BluetoothGatt bluetoothGatt) {
        return bluetoothGatt.getServices();
    }

    /**
     * 通过蓝牙写数据
     * （这是一个耗时操作，避免在主线程执行）
     * @param data 数据数组
     * @param length  写数组的长度，从data[0]开始
     * @return 该值为负 发送出错；不为负 发送成功的数据长度
     */
    public synchronized int write(@NonNull byte[] data, int length, ResultData resultData){
        Tool.logd("开始升级字库: " + data.length + "  " + length);
        resultData.total = 100;
        int total=0;
        if(data.length==0 || length==0){
            return 0;
        }
        resultData.progress = 0;
        List<byte[]> list = CommandUtil.fen(data, 20);
        int p = 0;
        boolean b;
        for (byte[] bytes : list) {
            if(stop){
                break;
            }
            b = syncWriteCharacteristic(curBC, bytes);
            Tool.logd("write bytes: " + bytes.length + " is " + b);
            if(!b){
                break;
            }
            total += bytes.length;
            p = total*100/length;
            if(p != resultData.progress){
                resultData.progress = p;
                resultData.data = "升级进度: " + p +"%";
                callback(true, resultData);
                if(stop){
                    return total;
                }
            }
        }
        return total;
    }

    public boolean syncWriteCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data) {
        if (bluetoothGatt==null || characteristic==null)
            return false;
        characteristic.setValue(data);
        setRwBusy(true);
        boolean ok = bluetoothGatt.writeCharacteristic(characteristic);
        if (ok){
            ok = waitIdle(1000);
        }
        return ok;
    }

    private boolean waitIdle(long timeout) {
        long timeout_tmp = timeout *50+1;
        while (timeout_tmp-->0 && !stop) {
            if (!rwBusy) {
                return true;
            }
            try {
                sleep(0,1000*20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Tool.logd("waitIdle Timeout!");
        rwBusy=false;
        return false;
    }

    private byte[] readResponse() {
        if(curBC == null){
            return null;
        }
        return read(curBC, false);
    }

    private byte[] read(@NonNull BluetoothGattCharacteristic characteristic, boolean single){
        if((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ)==0){
            return null;
        }
        if(single? syncReadSingle(characteristic): syncReadRepeat(characteristic)){
            if(stop){
                return null;
            }
            byte[] data = this.readBuffer;
            byte[] p1 = new byte[data.length];
            System.arraycopy(data, 0, p1, 0, data.length);
            this.readBuffer = null;
            return p1;
        } else{
            return null;
        }
    }

    public boolean syncReadSingle(BluetoothGattCharacteristic mReadCharacterist) {
        setRwBusy(true);
        bluetoothGatt.readCharacteristic(mReadCharacterist);
        return waitIdle(1000);
    }

    public boolean syncReadRepeat(BluetoothGattCharacteristic mReadCharacterist) {
        for(int i=0;i<5;i++){
            Tool.logd("syncReadRepeat-->"+i);
            if(syncReadSingle(mReadCharacterist)){
                if(READ_NULL){
                    READ_NULL=false;
                } else{
                    READ_NULL=false;
                    return true;
                }
            }
        }
        return false;
    }
}
