package wiar.bupt.com.qoedemo.utils;


/**
 * Created by Mr.Chen on 2017/6/16.
 */
public class VideoUrls {
    private String name;
    private String url;
    private Double rate;
    private int duration;
    private String  mimeType;
    public VideoUrls(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getDuration() {
        return duration;
    }

    public Double getRate() {
        return rate;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getText() {
        return "《" + name + "》" + "\n\t" + "码率：" + rate + "kbps" + "\n\t" + "长度：" + duration + "s"+ "\n\t" + "视频类型： "+mimeType;
    }
}
