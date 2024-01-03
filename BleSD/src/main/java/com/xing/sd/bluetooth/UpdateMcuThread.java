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
import com.xing.sd.util.ParseUtil;
import com.xing.sd.util.Tool;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

import androidx.annotation.NonNull;

public class UpdateMcuThread extends Thread {

    private boolean stop = false;
    private BluetoothGatt bluetoothGatt;

    private BluetoothGattCharacteristic curBC;
    private Handler handler;
    private volatile boolean rwBusy = false;
    private volatile boolean READ_NULL=false;

    private byte[] readBuffer;
    private Uri uri;
    private Context context;
    private CurrentImageInfo currentImageInfo;

    private int type = 0;

    public UpdateMcuThread(BluetoothGatt bluetoothGatt, Handler handler) {
        this.bluetoothGatt = bluetoothGatt;
        this.handler = handler;
    }

    public void startUpdate(Context context, Uri uri, CurrentImageInfo currentImageInfo){
        type = 1;
        this.uri = uri;
        this.context = context;
        this.currentImageInfo = currentImageInfo;
        start();
    }

    public void startGetInfo(){
        type = 0;
        start();
    }

    /****************************************OTA*******************************************/
    /**
     * 获取当前硬件image信息
     * @return 当前image信息
     */
    private void getCurrentImageInfo(){
        Tool.logd("try-->getCurrentImageInfo");
        byte[] response = spWRCharacteristic(CommandUtil.getImageInfoCommand(),0);
        CurrentImageInfo currentImageInfo = ParseUtil.parseImageFromResponse(response);
        Tool.logd("currentImageInfo: " + currentImageInfo);
        Message.obtain(handler, States.UPDATE_IMAGE, currentImageInfo).sendToTarget();
    }

    public boolean isStop() {
        return stop;
    }

    public boolean isRwBusy() {
        return rwBusy;
    }

    public void setRwBusy(boolean rwBusy) {
        this.rwBusy = rwBusy;
    }

    public boolean isREAD_NULL() {
        return READ_NULL;
    }

    public void setREAD_NULL(boolean READ_NULL) {
        this.READ_NULL = READ_NULL;
    }

    public void setReadData(byte[] data){
        this.readBuffer = data;
    }

    public void closeUpdateThread() {
        stop = true;
        READ_NULL = false;
        rwBusy = false;
    }

    @Override
    public void run() {
        if(init()){
            if(type == 0){
                getCurrentImageInfo();
            } else {
                File file = Tool.uriToFile(context, uri);
                start(file, currentImageInfo);
            }
        } else {
            ResultData resultData = new ResultData(ResultData.UPDATE_MCU, null);
            resultData.data = "升级通道查找失败";
            callback(false, resultData);
        }
        stop = true;
    }

    private void callback(boolean ok, ResultData resultData){
        Message.obtain(handler, ok?States.UPDATE_OK:States.UPDATE_ERROR, resultData).sendToTarget();
    }

    private void start(File file, CurrentImageInfo currentImageInfo) {
        stop = false;
        ResultData resultData = new ResultData(ResultData.UPDATE_MCU, curBC.getUuid().toString());
        if(file == null){
            resultData.data = "升级文件读取错误";
            callback(false, resultData);
            return;
        }

        currentImageInfo.setType(ImageType.B);

        //image信息
        Tool.logd("开始升级固件： "+ file.getAbsolutePath());
        int startAddr=0;
        if(currentImageInfo.getType()== ImageType.A){
            startAddr=currentImageInfo.getOffset();
        }else if(currentImageInfo.getType()== ImageType.B){
            startAddr=0;
        } else {

        }
        //读取文件
        Tool.logd("读取文件");
        ByteBuffer byteBuffer=null;
        if(file.getName().endsWith(".bin") || file.getName().endsWith(".BIN")){
            byteBuffer= FileParseUtil.parseBinFile(file);
        }else if(file.getName().endsWith(".hex") || file.getName().endsWith(".HEX")){
            byteBuffer= FileParseUtil.parseHexFile(file);
        } else {
            Tool.loge("CH57X only support hex and bin image file");
            resultData.data = "升级文件错误";
            callback(false, resultData);
            return;
        }
        if(byteBuffer==null){
            Tool.loge("parse file fail");
            resultData.data = "文件解析错误";
            callback(false, resultData);
            return;
        }
        Tool.logd("byteBuffer  capacity: "+byteBuffer.capacity());
        int total=  byteBuffer.capacity();
        Tool.logd("total size: "+total);
        //读取文件的offset
        Tool.logd("解析文件");

        //检查Image合法性
        if(!FileParseUtil.checkImageIllegal(currentImageInfo,byteBuffer)){
            Tool.loge("image file is illegal!");
            resultData.data = "文件不合法";
            callback(false, resultData);
            return;
        }
        //目前不需要检查Image合法性
        /*int nBlocks = (((total)+ OTA_BLOCK_SIZE - 1) / OTA_BLOCK_SIZE);
        LogUtil.d("image nBlocks: "+(nBlocks & 0xffff));*/

        int nBlocks = (((total-512)+(currentImageInfo.getBlockSize()-1))/currentImageInfo.getBlockSize());
        Tool.logd("erase nBlocks: "+(nBlocks & 0xffff));

        resultData.data = "Erase Start";
        callback(true, resultData);

        //开始擦除
        Tool.logd("start erase... ");
        Tool.logd("startAddr: "+startAddr);
        Tool.logd("nBlocks: "+nBlocks);
        byte[] bytes = spWRCharacteristic(CommandUtil.getEraseCommand(startAddr, nBlocks), 1000);
        if(!ParseUtil.parseEraseResponse(bytes)){
            Tool.loge("erase fail!");
            resultData.data = "erase fail!";
            callback(false, resultData);
            return;
        }else {
            Tool.logd("erase success!");

            resultData.data = "Erase Success!";
            callback(true, resultData);
        }

        resultData.data = "Program Start!";
        callback(true, resultData);

        byte[] realBuffer = byteBuffer.array();
        //开始编程
        Tool.logd("start program... ");
        //DebugUtil.getInstance().write("start program... ");
        int offset=0;
        int p = 0;
        while (offset<realBuffer.length){
            if(stop){
                return;
            }
            //有效数据的长度
            int programmeLength = CommandUtil.getProgrammeLength(realBuffer, offset);
            byte[] programmeCommand = CommandUtil.getProgrammeCommand(offset + startAddr, realBuffer, offset);
            if(write(programmeCommand,programmeCommand.length)!=programmeCommand.length){
                resultData.data = "Program Fail!";
                callback(false, resultData);
                return;
            }
            offset+=programmeLength;
            p = offset*100/realBuffer.length;
            if(p != resultData.progress){
                Tool.logd("progress: "+ offset+"/"+ realBuffer.length);
                resultData.progress = p;
                resultData.data ="program progress: "+ p +"/100";
                resultData.total = 100;
                callback(true, resultData);
            }
        }
        Tool.logd("program complete! ");
        resultData.progress = 0;
        resultData.data ="Program Finish!";
        callback(true, resultData);

        //开始校验
        resultData.data ="Verify Start!";
        callback(true, resultData);

        Tool.logd("start verify... ");
        int vIndex=0;
        while (vIndex<realBuffer.length){
            if(stop){
                return;
            }
            int verifyLength = CommandUtil.getVerifyLength(realBuffer, vIndex);
            byte[] verifyCommand = CommandUtil.getVerifyCommand(vIndex + startAddr, realBuffer, vIndex);
            if(write(verifyCommand,verifyCommand.length)!=verifyCommand.length){
                resultData.data ="Verify fail!";
                callback(false, resultData);
                return;
            }
            vIndex+=verifyLength;

            p = vIndex*100/realBuffer.length;
            if(p != resultData.progress){
                Tool.logd("verify progress: "+ vIndex+"/"+ realBuffer.length);
                resultData.progress = p;
                resultData.data ="verify progress: "+ p +"/100";
                resultData.total = 100;
                callback(true, resultData);
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] bytes1 = readResponse();
        if(!ParseUtil.parseVerifyResponse(bytes1)){
            resultData.data ="Verify fail!";
            callback(false, resultData);
            return;
        }
        Tool.logd("verify complete! ");

        resultData.data ="Verify Complete!";
        callback(true, resultData);

        //结束
        Tool.logd("start ending... ");
        byte[] endCommand = CommandUtil.getEndCommand();

        if(endCommand.length!=write(endCommand, endCommand.length)){
            resultData.data ="升级失败!";
            callback(false, resultData);
            return;
        } else {
            resultData.data ="升级成功!";
            callback(true, resultData);
        }
    }

    private boolean init(){
        if(bluetoothGatt == null){
            return false;
        }
        List<BluetoothGattService> gattServices = getServices(bluetoothGatt);
        for (BluetoothGattService gattService : gattServices) {
            String gattServiceUUID = Long.toHexString(gattService.getUuid().getMostSignificantBits()).substring(0, 4);
            if (gattServiceUUID.equals(CommandUtil.OTA_SID)) {
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : gattService.getCharacteristics()) {
                    String characterUUID = Long.toHexString(bluetoothGattCharacteristic.getUuid().getMostSignificantBits()).substring(0, 4);
                    if (characterUUID.equals(CommandUtil.OTA_CID)) {
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

    private byte[] readResponse(){
        if(curBC == null){
            return null;
        }
        return read(curBC, false);
    }

    /**
     * 通过蓝牙写数据
     * （这是一个耗时操作，避免在主线程执行）
     * @param data 数据数组
     * @param length  写数组的长度，从data[0]开始
     * @return 该值为负 发送出错；不为负 发送成功的数据长度
     */
    public synchronized int write(@NonNull byte[] data, int length){
        int total=0;

        if(data.length==0 || length==0){
            return 0;
        }

        List<byte[]> list = CommandUtil.fen(data, BlueToothManage.maxSize);
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

        }
        return total;
    }

    /**
     * 向特征值写数据后再从特征值读取数据
     * @return 读取的数据
     */
    private byte[] spWRCharacteristic(@NonNull byte[] writeData, long waitTimeBeforeRead){
        if(write(writeData,writeData.length)==writeData.length){
            if(waitTimeBeforeRead>0){
                try {
                    sleep(waitTimeBeforeRead);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return read(curBC,false);
        }
        return null;
    }

    /**
     * 从特征值读取数据
     * @param characteristic 特征值
     * @param single 是否多次尝试读取到
     * @return 读取的数据
     */
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

    private boolean waitIdle(long timeout) {
        long timeout_tmp = timeout *50+1;
        while (timeout_tmp-->0) {
            if (!rwBusy) {
                Tool.logd("waitIdle break");
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

    private synchronized boolean writeValue(BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt == null) {
            return false;
        }
        int count = 0;
        while (!bluetoothGatt.writeCharacteristic(characteristic) && (count < 5)) {
            count++;
            try {
                Thread.sleep(count * 200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Tool.logd("重新发送: " + count);
        }
        return count != 5;
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
}
