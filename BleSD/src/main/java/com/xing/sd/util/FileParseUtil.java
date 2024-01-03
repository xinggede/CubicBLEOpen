package com.xing.sd.util;

import com.xing.sd.bean.CurrentImageInfo;
import com.xing.sd.bean.ImageType;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class FileParseUtil {

    public static boolean checkImageIllegal(CurrentImageInfo imageInfo, ByteBuffer byteBuffer){
        if(byteBuffer.capacity()<8){
            return false;
        }
        byte[] temp=new byte[]{byteBuffer.get(4),byteBuffer.get(5),byteBuffer.get(6),byteBuffer.get(7)};
        int imageFileOffset = FormatUtil.bytesToIntLittleEndian(temp, 0);
        Tool.logd("imageFile offset: "+imageFileOffset);
        Tool.logd("imageInfo offset: "+imageInfo.getOffset());

        //检查Bin文件的offset
        if(imageInfo.getType()== ImageType.A && imageFileOffset>imageInfo.getOffset()){
            return true;
        }else if(imageInfo.getType()==ImageType.B && imageFileOffset<imageInfo.getOffset()){
            return true;
        }else {
            return false;
        }
    }

    public static ByteBuffer parseBinFile(File file){
        long length = file.length();
        if(length>Integer.MAX_VALUE){
            return null;
        }
        //读取文件
        FileInputStream stream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            stream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(stream);
            int readLen=0;
            int arraySize=1024;
            byte[] arrayBuffer=new byte[arraySize];
            ByteBuffer byteBuffer=ByteBuffer.allocate(bufferedInputStream.available());
            while ((readLen=bufferedInputStream.read(arrayBuffer))!=-1){
                byteBuffer.put(arrayBuffer,0,readLen);
            }
            byteBuffer.flip();
            return byteBuffer;
        } catch (Exception e) {

        } finally {
            try {
                if(bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if(stream != null){
                    stream.close();
                }
            } catch (IOException e) {

            }
        }
        return null;
    }

    public static ByteBuffer parseBinFile(InputStream inputStream){
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            int readLen=0;
            int arraySize=1024;
            byte[] arrayBuffer=new byte[arraySize];
            ByteBuffer byteBuffer=ByteBuffer.allocate(bufferedInputStream.available());
            while ((readLen=bufferedInputStream.read(arrayBuffer))!=-1){
                byteBuffer.put(arrayBuffer,0,readLen);
            }
            byteBuffer.flip();
            return byteBuffer;
        } catch (Exception e) {

        } finally {
            try {
                if(bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {

            }
        }
        return null;
    }

    public static ByteBuffer parseHexFile( File file)  {
        if(file==null || !file.exists() || !file.isFile() ||(!file.getName().endsWith("hex") && !file.getName().endsWith("HEX"))){
            return null;
        }
        long length = file.length();
        if(length>Integer.MAX_VALUE){
            return null;
        }
        BufferedReader reader=null;
        ArrayList<FileElement> list=new ArrayList<>();
        int totalLen=0;
        int minAddr=-1;
        int maxAddr=-1;

        //用来记录最大地址所在包的数据长度，用来确定分配数组的大小
        int maxAddr_Data_Len=0;
        //读取文件,分配数组内存大小
        try {
            FileStruct fileStruct = new FileStruct();
            reader=new BufferedReader(new FileReader(file));
            String hexStr=null;

            int l_addr=0;
            while ((hexStr=reader.readLine())!=null){
                //LogUtil.d("-----readHexStr: "+hexStr);
                if(!hexStr.startsWith(":",0)){
                    Tool.loge("hex file don't start with ':' ");
                    return null;
                }
                if(!hexStringValid(hexStr.substring(1))){
                    Tool.loge("hex file contains invalid char ");
                    return null;
                }
                if(hexStr.length()<11){
                    Tool.loge("hex file content is invalid,every line should't less than 11 ");
                    return null;
                }
                fileStruct.setLength(Integer.parseInt(hexStr.substring(1,3),16));
                fileStruct.setOffset(Integer.parseInt(hexStr.substring(3,7),16));
                fileStruct.setType(Integer.parseInt(hexStr.substring(7,9),16));
                fileStruct.setData(hexString2ByteArray(hexStr.substring(9,9+fileStruct.getLength()*2)));
                fileStruct.setCheck(Integer.parseInt(hexStr.substring(9+fileStruct.getLength()*2,9+fileStruct.getLength()*2+2),16));

                int type=fileStruct.getType();
                if(type==0x00){//本行的数据类型为“数据记录”
                    if(fileStruct.getFormat()==0x00){//本行所从属的数据类型为“数据记录”
                        l_addr=fileStruct.getOffset();
                    }else if(fileStruct.getFormat()==0x02){//本行所从属的数据类型为“扩展段地址记录”(HEX86)--20位地址
                        l_addr = (fileStruct.getAddress() << 4) + fileStruct.getOffset();
                    }else if(fileStruct.getFormat()==0x04){///本行所从属的数据类型为“扩展线性地址记录”(HEX386)--32位地址
                        l_addr = (fileStruct.getAddress() << 16) + fileStruct.getOffset();
                    }else {
                        Tool.loge("invalid type!");
                        return null;
                    }
                    //记录地址中的最大最小值
                    //LogUtil.d("l_addr:" + l_addr);
                    if(minAddr<0){
                        //第一次
                        minAddr=l_addr;
                    }else {
                        minAddr=Math.min(l_addr,minAddr);
                    }
                    if(maxAddr<0){
                        //第一次
                        maxAddr=l_addr;
                        maxAddr_Data_Len=fileStruct.getData().length;
                    }else {
                        //引入maxAddr_Data_Len
                        maxAddr=Math.max(l_addr,maxAddr);
                        if(l_addr==maxAddr){
                            //更新了最大地址
                            maxAddr_Data_Len=fileStruct.getData().length;
                        }
                    }

                    FileElement hexRec = new FileElement();
                    hexRec.setAddr(l_addr);
                    hexRec.setLen(fileStruct.getLength());
                    hexRec.setData(fileStruct.getData());
                    //LogUtil.d("readData:" +bytesToHexString(fileStruct.getData()) );
                    list.add(hexRec);
                    totalLen+=hexRec.getData().length;
                    //LogUtil.d("total size: "+totalLen);

                }else if(type==0x01){//本行的数据类型为“文件结束记录”
                    //跳出循环
                    fileStruct.format = 0x01;
                    break;
                }else if(type==0x02){//本行的数据类型为“扩展段地址记录”
                    //扩展段地址记录的数据个数一定是0x02
                    if (fileStruct.getLength() != 0x02){
                        Tool.loge("incorrect length!");
                        return null;
                    }
                    //扩展段地址记录的地址一定是0x0000
                    if (fileStruct.getOffset() != 0x0000) {
                        Tool.loge("incorrect address!");
                        return null;
                    }
                    //更改从属的数据类型
                    fileStruct.setFormat(0x02);
                    //获取段地址
                    byte[] data = fileStruct.getData();
                    fileStruct.setAddress((data[0] << 8 | data[1]));

                }else if(type==0x03){//开始段地址记录
                    Tool.logd("Start Segment Address record: ignored");
                    //throw new CH579OTAException("this type is unhandled");
                }else if(type==0x04){//本行的数据类型为“扩展线性地址记录”
                    //扩展线性地址记录中的数据个数一定是0x02
                    if (fileStruct.getLength() != 0x02){
                        Tool.loge("incorrect length!");
                        return null;
                    }
                    //扩展线性地址记录的地址一定是0x0000
                    if (fileStruct.getOffset() != 0x0000){
                        Tool.loge("incorrect address!");
                        return null;
                    }
                    //更改hex从属的数据类型
                    fileStruct.setFormat(0x04);
                    //获取高16位地址
                    byte[] data = fileStruct.getData();
                    fileStruct.setAddress((data[0] << 8 | data[1]));
                }else if(type==0x05){//开始线性地址记录
                    Tool.logd("Start Linear Address record: ignored");
                }else {
                    Tool.loge("this type is undefined");
                    return null;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Tool.logd("final total size: "+totalLen);
        Tool.logd("min address: "+minAddr);
        Tool.logd("max address: "+maxAddr);

        //由于地址不连续，totalLen 可能缺少
        //数组可能由maxAddr-minAddr+maxAddr_Data_Len分配
        int realSize=maxAddr-minAddr+maxAddr_Data_Len;
        if(realSize!=totalLen){
            Tool.logd("hex文件地址不连续："+"realSize->"+realSize+" totalLen"+totalLen);
        }
        totalLen=Math.max(realSize,totalLen);

        byte[] buffer=new byte[totalLen];
        for (FileElement element : list) {
            int offset=element.getAddr()-minAddr;
            System.arraycopy(element.getData(),0,buffer,offset,element.getData().length);
        }
        ByteBuffer allocate = ByteBuffer.allocate(totalLen);
        allocate.put(buffer);
        allocate.flip();
        return allocate;
    }

    private static boolean hexStringValid(String s){
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(('0'<=chars[i] && chars[i]<='9') || ('a'<=chars[i] && chars[i]<='f') || ('A'<=chars[i] && chars[i]<='F')){

            }else {
                return false;
            }
        }
        return true;
    }

    public static byte[] hexString2ByteArray(String hexString) {
        try {
            if (hexString == null || hexString.equals("")) {
                return null;
            }
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public static String bytesToHexString(byte[] bArr) {
        if (bArr == null || bArr.length==0)
            return "";
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;
        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase() + " ");
        }
        return sb.toString();
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    static class FileStruct{
        private String start = ":";    //每一条Hex记录的起始字符“:”
        private int length = 0x00;    //数据的字节数量
        private int address = 0x0000;    //数据存放的地址
        private int type = 0xFF;    //HEX记录的类型
        private byte[] data;//一行最多有16个字节的数据
        private int check = 0xAA;    //校验和
        private int offset = 0x0000;    //偏移量
        private int format = 0x00;    //数据行所从属的记录类型

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getAddress() {
            return address;
        }

        public void setAddress(int address) {
            this.address = address;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public int getCheck() {
            return check;
        }

        public void setCheck(int check) {
            this.check = check;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getFormat() {
            return format;
        }

        public void setFormat(int format) {
            this.format = format;
        }
    }

    static class FileElement{
        private int addr;
        private int len;

        private byte[] data;

        public int getAddr() {
            return addr;
        }

        public void setAddr(int addr) {
            this.addr = addr;
        }

        public int getLen() {
            return len;
        }

        public void setLen(int len) {
            this.len = len;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }
}
