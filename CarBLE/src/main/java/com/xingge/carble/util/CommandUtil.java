package com.xingge.carble.util;

public class CommandUtil {

    public static final String SEND_SID = "ffe5";
    public static final String SEND_CID = "ffe9";

    public static final String READ_SID = "ffe0";
    public static final String READ_CID = "ffe4";

    private static String START = "VGH:";
    private static String END = "\r\n";

    public static final String CONNECT = "CONNECT"; //已连接上； APP连接上发此命令。 APP->MCU:
    public static final String OK = "OK"; //响应符；APP接收到命令和数据后的发此响应。APP->MCU:
    public static final String ERR = "ERR"; //响应符；APP接收到命令和数据后的发此响应。APP->MCU:
    public static final String GALL = "GALL"; ///获取全部信息的命令； APP->MCU: 如：VGH:GALL
    public static final String SALL = "SALL"; ///保存设置的命令； APP->MCU:
    public static final String VER = "VER-"; /// MCU软件版本的获取； APP->MCU:-? ；MCU->APP:-Dx
    public static final String GMT = "GMT-"; ///时区获取和设置； APP->MCU:-? 或 APP->MCU:-Dx ；MCU->APP:-Dx
    public static final String TIME = "TIME-"; ///时间设置； APP->MCU:-Dx
    public static final String ACCIN = "ACCIN-"; ///电源检测的获取设置； APP 或 APP->MCU:-Dx ；MCU->APP:-Dx
    public static final String VOLT = "VOLT-"; ///电压的获取； APP->MCU:-? ；MCU->APP:-Dx

    public static final String SLOPE = "SLOPE-"; ///坡度的获取； APP->MCU:-? ；MCU->APP:-Dx
    public static final String TEMP = "TEMP-"; ///温度的获取； APP->MCU:-? ；MCU->APP:-Dx
    public static final String HPA = "HPA-"; ///气压的获取； APP->MCU:-? ；MCU->APP:-Dx
    public static final String DHIGH = "DHIGH-"; ///显示海拔的获取； APP->MCU:-? ；MCU->APP:-Dx
    public static final String GMDF = "GMDF-"; //GPS模式等的获取和设置； APP->MCU:-? 或 APP->MCU:-Dx ；MCU->APP:-Dx
    public static final String GINFO = "GINFO-"; //GPS当前信息的获取； APP ； MCU->APP:-Dx
    public static final String GTIME = "GTIME-"; //G 或 APP->MCU:-Dx ；MCU->APP:-Dx
    public static final String GTRKI = "GTRKI-"; //G ；MCU->APP:-Dx
    public static final String GTRKD = "GTRKD-"; //GPS轨迹数据的获取； AP ；MCU->APP:-Dx
    public static final String RFPVS = "RFPVS-"; //对讲信息的获取和设置； APP- 或 APP->MCU:-Dx ；MCU->APP:-Dx

    public static final String RFCTRL = "RFCTRL-";
    public static final String RFREQ = "RFREQ-"; //对讲频率的获取和设置； APP- 或 APP->MCU:-Dx ；MCU->APP:-Dx
    public static final String DERR = "DERR-"; //异常信息的获取和设置； APP- 或 APP->MCU:-Dx ；MCU->APP:-Dx
    public static final String RESET = "RESET-"; ///复位的设置; APP->MCU:-Dx

    public static byte[] commandToByte(String str) {
        String data = START + str + END;
        return data.getBytes();
    }

    public static byte[] getCommandByte(String str) {
        String data = START + str + "?" + END;
        return data.getBytes();
    }

    public static byte[] setCommandByte(String str, String value) {
        String data = START + str + value + END;
        return data.getBytes();
    }
}
