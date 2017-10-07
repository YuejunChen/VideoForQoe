package io.vov.vitamio.paramsEntity;

import java.util.List;

/**
 * Created by pengfeng on 2017/9/19.
 */

public class ParamsToPost {
    private String mac = "";
    private double cpu;//•	cpu主频/KHz
    private double screenPixels;//•	屏幕像素密度/ppi
    private String mimeType;
    private int height = 0;
    private int width = 0;
    private double videoStreamBitRate;//•	视频流平均比特率/kbps
    private int videoLength;//•	视频长度/s
    private long initTime;//初始缓冲时间/ms
    private long videoBufferStart = -1;
    private List test;//
    private List monitor;

    public String toJson() {
        return "";
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getInitTime() {
        return initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public List getMonitor() {
        return monitor;
    }

    public void setMonitor(List monitor) {
        this.monitor = monitor;
    }

    public double getScreenPixels() {
        return screenPixels;
    }

    public void setScreenPixels(double screenPixels) {
        this.screenPixels = screenPixels;
    }

    public List getTest() {
        return test;
    }

    public void setTest(List test) {
        this.test = test;
    }

    public long getVideoBufferStart() {
        return videoBufferStart;
    }

    public void setVideoBufferStart(long videoBufferStart) {
        this.videoBufferStart = videoBufferStart;
    }

    public int getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(int videoLength) {
        this.videoLength = videoLength;
    }

    public double getVideoStreamBitRate() {
        return videoStreamBitRate;
    }

    public void setVideoStreamBitRate(double videoStreamBitRate) {
        this.videoStreamBitRate = videoStreamBitRate;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
