package io.vov.vitamio.paramsEntity;

import java.io.Serializable;

/**
 * Created by pengfeng on 2017/9/10.
 */

public class TestParams implements Serializable {
    private double mos_sub = 0;
    private long testTime = -1;
    private long testTimeStamp = -1;
    private double messageDelay = -1;//•消息时延/ms
    private double longitude;
    private double latitude;

    public TestParams(double mos_sub,long testTime,long testTimeStamp,double messageDelay,double longitude,double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.messageDelay = messageDelay;
        this.mos_sub = mos_sub;
        this.testTimeStamp = testTimeStamp;
        this.testTime = testTime;
    }

    @Override
    public String toString() {
        return "TestParams{" +
                "latitude=" + latitude +
                ", mos_sub=" + mos_sub +
                ", testTime=" + testTime +
                ", testTimeStamp=" + testTimeStamp +
                ", messageDelay=" + messageDelay +
                ", longitude=" + longitude +
                '}';
    }

    public long getTestTime() {
        return testTime;
    }

    public void setTestTime(long testTime) {
        this.testTime = testTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getMessageDelay() {
        return messageDelay;
    }

    public void setMessageDelay(double messageDelay) {
        this.messageDelay = messageDelay;
    }

    public double getMos_sub() {
        return mos_sub;
    }

    public void setMos_sub(double mos_sub) {
        this.mos_sub = mos_sub;
    }

    public long getTestTimeStamp() {
        return testTimeStamp;
    }

    public void setTestTimeStamp(long testTimeStamp) {
        this.testTimeStamp = testTimeStamp;
    }
}
