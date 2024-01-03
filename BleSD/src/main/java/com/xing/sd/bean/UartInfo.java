package com.xing.sd.bean;

/**
 * @ClassName UartInfo
 * @Description TODO
 * @Author 星哥的
 * @Date 2023/12/25 16:02
 * @Version 1.0
 */
public class UartInfo {

    public int workBit;
    public int baud;
    public int dataBit;
    public int stopBit;
    public int checkBit;
    public int comm;

    public UartInfo() {
    }

    public UartInfo(int workBit, int baud, int dataBit, int stopBit, int checkBit, int comm) {
        this.workBit = workBit;
        this.baud = baud;
        this.dataBit = dataBit;
        this.stopBit = stopBit;
        this.checkBit = checkBit;
        this.comm = comm;
    }
}
