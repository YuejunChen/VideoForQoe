package io.vov.vitamio.paramsEntity;

import java.io.Serializable;

/**
 * Created by pengfeng on 2017/9/17.
 */

public class MonitorParams implements Serializable{
    int sendSpeed = 0;
    //当前的下载速度
    int netSpeed = 0;
    //缓冲百分比
    int bufferPercentage = 0;
    //播放时间
    long monitorTime = 0;
    //时间戳
    long monitorTimeStamp = 0;
    //内存
    float memoryConsumption = 0;
    //cpu
    double currentCpu = 0;

    public MonitorParams(int sendSpeed, int netSpeed, int bufferPercentage, long monitorTime, long monitorTimeStamp , float memoryConsumption,double currentCpu) {
        this.bufferPercentage = bufferPercentage;
        this.monitorTime = monitorTime;
        this.netSpeed = netSpeed;
        this.sendSpeed = sendSpeed;
        this.memoryConsumption = memoryConsumption;
        this.monitorTimeStamp = monitorTimeStamp;
        this.currentCpu = currentCpu;
    }

    @Override
    public String toString() {
        return "MonitorParams{" +
                "bufferPercentage=" + bufferPercentage +
                ", sendSpeed=" + sendSpeed +
                ", netSpeed=" + netSpeed +
                ", monitorTime=" + monitorTime +
                ", monitorTimeStamp=" + monitorTimeStamp +
                ", memoryConsumption=" + memoryConsumption +
                ", currentCpu=" + currentCpu +
                '}';
    }

    public int getBufferPercentage() {
        return bufferPercentage;
    }

    public void setBufferPercentage(int bufferPercentage) {
        this.bufferPercentage = bufferPercentage;
    }

    public float getMemoryConsumption() {
        return memoryConsumption;
    }

    public void setMemoryConsumption(float memoryConsumption) {
        this.memoryConsumption = memoryConsumption;
    }

    public long getMonitorTime() {
        return monitorTime;
    }

    public void setMonitorTime(long monitorTime) {
        this.monitorTime = monitorTime;
    }

    public int getNetSpeed() {
        return netSpeed;
    }

    public void setNetSpeed(int netSpeed) {
        this.netSpeed = netSpeed;
    }

    public int getSendSpeed() {
        return sendSpeed;
    }

    public void setSendSpeed(int sendSpeed) {
        this.sendSpeed = sendSpeed;
    }

    public long getMonitorTimeStamp() {
        return monitorTimeStamp;
    }

    public void setMonitorTimeStamp(long monitorTimeStamp) {
        this.monitorTimeStamp = monitorTimeStamp;
    }

    public double getCurrentCpu() {
        return currentCpu;
    }

    public void setCurrentCpu(double currentCpu) {
        this.currentCpu = currentCpu;
    }
}
