package com.xingge.carble.bluetooth;


public class SendValue {

    /**
     * 0:send  1:setNotification
     */
    public int type = 0;
    public String serviceId;
    public String characteristicId;
    public String deviceId;
    public byte[] value;

    public boolean enable;

    public SendValue(String serviceId, String characteristicId, String deviceId, byte[] value) {
        this.serviceId = serviceId;
        this.characteristicId = characteristicId;
        this.deviceId = deviceId;
        this.value = value;
    }

    public SendValue(int type, String serviceId, String characteristicId, String deviceId, boolean enable) {
        this.type = type;
        this.serviceId = serviceId;
        this.characteristicId = characteristicId;
        this.deviceId = deviceId;
        this.enable = enable;
    }
}
