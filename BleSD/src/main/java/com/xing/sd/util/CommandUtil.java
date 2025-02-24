package com.xing.sd.util;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CommandUtil {

    public static final Charset charset = Charset.forName("GB2312");

    public static final String SEND_SID = "ffe0";
    public static final String SEND_CID = "ffe3";

    public static final String READ_SID = "ffe0";
    public static final String READ_CID = "ffe4";

    public static final String FONT_SID = "ffe0";
    public static final String FONT_CID = "ffe1";

    public static final String OTA_SID="fee0";
    public static final String OTA_CID="fee1";
    public static final String OTA_RID="fee1";

    private static String START = "BLP:";
    private static String END = "\r";

    public final static byte CHECK = 0x0D;

    public static final String CONNECT = "CONNECT"; //已连接上； APP连接上发此命令。 APP->MCU:
    public static final String OK = "OK"; //响应符；APP接收到命令和数据后的发此响应。APP->MCU:
    public static final String ERR = "ERR"; //响应符；APP接收到命令和数据后的发此响应。APP->MCU:
    public static final String GALL = "GALL"; ///获取全部信息的命令； APP->MCU: 如：VGH:GALL
    public static final String SALL = "SALL"; ///保存设置的命令； APP->MCU:
    public static final String VER = "VER-"; /// MCU软件版本的获取； APP->MCU:-? ；MCU->APP:-Dx
    public static final String GMT = "GMT-"; ///时区获取和设置； APP->MCU:-? 或 APP->MCU:-Dx ；MCU->APP:-Dx
    public static final String TIME = "TIME-"; ///时间设置； APP->MCU:-Dx
    public static final String SYST = "SYST-"; ///系统信息的获取； APP 或 APP->MCU:-Dx ；MCU->APP:-Dx

    public static final String LPNUM = "LPNUM-"; ///屏数量的获取和设置； APP->MCU:-? ；MCU->APP:-Dx

    public static final String PTYPE = "PTYPE-"; ///屏类型的获取和设置； APP->MCU:-? ；MCU->APP:-Dx

    public static final String FONT = "FONT-"; ///字体的获取和设置； APP->MCU:-? ；MCU->APP:-Dx

    public static final String LPBRI = "LPBRI-"; ///光感应及屏亮度的获取和设置； APP->MCU:-? ；MCU->APP:-Dx

    public static final String RCPWM = "RCPWM-"; ///RCPWM的获取和设置； APP->MCU:-? ；MCU->APP:-Dx
    public static final String UART0 = "UART0-"; ///UART的获取和设置； APP->MCU:-? 或 APP->MCU:-Dx ；MCU->APP:-Dx

    public static final String UART1 = "UART1-"; ///UART的获取和设置； APP ； MCU->APP:-Dx
    public static final String UART2 = "UART2-"; //UART的获取和设置； APP ； MCU->APP:-Dx

    public static final String UART3 = "UART3-"; //UART的获取和设置； APP ； MCU->APP:-Dx
    public static final String ENETIP = "ENETIP-"; //网线IP的获取和设置； AP ；MCU->APP:-Dx
    public static final String DTITLE = "DTITLE-"; //显示标题的获取和设置； APP- 或 APP->MCU:-Dx ；MCU->APP:-Dx
    public static final String DGTYPE = "DGTYPE-"; //显示方式的获取和设置；
    public static final String DGSHOW = "DGSHOW-"; //显示内容的获取和设置；
    public static final String MODBUS = "MODBUS-"; //MODBUS的获取和设置；
    public static final String UPG = "UPG-"; //升级开始设置；
    public static final String RESET = "RESET-"; ///复位的设置; APP->MCU:-Dx


    public static byte[] commandToByte(String str) {
        String data = START + str + END;
        return data.getBytes(charset);
    }

    public static byte[] getCommandByte(String str) {
        String data = START + str + "?" + END;
        return data.getBytes(charset);
    }

    public static byte[] setCommandByte(String str, String value) {
        String data = START + str + value + END;
        return data.getBytes(charset);
    }

    public static final byte CMD_ISP_PROGRAM = (byte) 0x80;
    public static final byte CMD_ISP_ERASE = (byte) 0x81;
    public static final byte CMD_ISP_VERIFY = (byte) 0x82;
    public static final byte CMD_ISP_END = (byte) 0x83;
    public static final byte CMD_ISP_INFO = (byte) 0x84;

    private static final int IAP_LEN = 20;

    //573的地址除以16，CH579除以4
    public static byte[] getImageInfoCommand(){
        ByteBuffer byteBuffer=ByteBuffer.allocate(IAP_LEN);
        byteBuffer.put(CMD_ISP_INFO);
        byteBuffer.put((byte)((IAP_LEN-2)));
        return byteBuffer.array();
    }

    public static byte[] getEraseCommand(int addr,int block){
        ByteBuffer byteBuffer=ByteBuffer.allocate(IAP_LEN);
        byteBuffer.put(CMD_ISP_ERASE);
        byteBuffer.put((byte) 0x00);
        byteBuffer.put((byte)(addr/4));
        byteBuffer.put((byte)((addr/4)>>8));
        byteBuffer.put((byte)(block));
        byteBuffer.put((byte)(block >>8));
        return byteBuffer.array();
    }

    public static byte[] getProgrammeCommand(int addr,byte[] data,int offset){
        ByteBuffer byteBuffer=ByteBuffer.allocate(IAP_LEN);
        byteBuffer.put(CMD_ISP_PROGRAM);
        byteBuffer.put((byte) (IAP_LEN-4));
        byteBuffer.put((byte)(addr/4));
        byteBuffer.put((byte)((addr/4)>>8));
        int len=Math.min(IAP_LEN-4,data.length-offset);
        byteBuffer.put(data,offset,len);
        return byteBuffer.array();
    }

    public static int getProgrammeLength(byte[] data,int offset){
        return Math.min(IAP_LEN-4,data.length-offset);
    }

    public static byte[] getVerifyCommand(int addr,byte[] data,int offset){
        ByteBuffer byteBuffer=ByteBuffer.allocate(IAP_LEN);
        byteBuffer.put(CMD_ISP_VERIFY);
        byteBuffer.put((byte) (IAP_LEN-4));
        byteBuffer.put((byte)(addr/4));
        byteBuffer.put((byte)((addr/4)>>8));
        int len=Math.min(IAP_LEN-4,data.length-offset);
        byteBuffer.put(data,offset,len);
        return byteBuffer.array();
    }

    public static int getVerifyLength(byte[] data,int offset){
        return Math.min(IAP_LEN-4,data.length-offset);
    }

    public static byte[] getEndCommand(){
        ByteBuffer byteBuffer=ByteBuffer.allocate(IAP_LEN);
        byteBuffer.put(CMD_ISP_END);
        byteBuffer.put((byte)((IAP_LEN-2)));
        return byteBuffer.array();
    }

    public static List<byte[]> fen(byte[] data, int length) {
        ArrayList data2 = new ArrayList();
        int num = data.length / length;
        int num2 = data.length % length;
        if (data.length < length) {
            data2.add(data);
            return data2;
        } else {
            for (int buffer2 = 0; buffer2 < num; ++buffer2) {
                byte[] k = new byte[length];

                for (int j = 0; j < length; ++j) {
                    k[j] = data[j + buffer2 * length];
                }

                data2.add(k);
            }

            if (num2 != 0) {
                byte[] var8 = new byte[num2];

                for (int var9 = 0; var9 < num2; ++var9) {
                    var8[var9] = data[var9 + num * length];
                }

                data2.add(var8);
            }

            return data2;
        }
    }
}
