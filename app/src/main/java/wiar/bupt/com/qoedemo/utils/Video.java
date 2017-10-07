package wiar.bupt.com.qoedemo.utils;

import android.graphics.Bitmap;

/**
 * Created by Mr.Chen on 2017/9/18.
 */
public class Video {
    private Bitmap ImageBitmap;
    private Bitmap PicBitmap;
    private  int ImageId;
    private int PicId;
    private String videoType;
    private String[] name;
    private String[] url;
    private Double[] rate;
    private int[] duration;
    private String[]  mimeType;

    public Video(int imageId, int picId, String videoType,String[] name,String[] url) {
        ImageId = imageId;
        PicId = picId;
        this.videoType = videoType;
        this.url = url;
        this.name = name;
    }

    public Video(Bitmap picBitmap,Bitmap imageBitmap,  String videoType,String[] name,String[] url) {
        ImageBitmap = imageBitmap;
        PicBitmap = picBitmap;
        this.videoType = videoType;
        this.url = url;
        this.name = name;
    }

    public Bitmap getImageBitmap() {return ImageBitmap;}

    public Bitmap getPicBitmap() {return PicBitmap;}

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public int[] getDuration() {
        return duration;
    }

    public void setDuration(int[] duration) {
        this.duration = duration;
    }

    public String[] getMimeType() {
        return mimeType;
    }

    public void setMimeType(String[] mimeType) {
        this.mimeType = mimeType;
    }

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public Double[] getRate() {
        return rate;
    }

    public void setRate(Double[] rate) {
        this.rate = rate;
    }

    public String[] getUrl() {
        return url;
    }

    public void setUrl(String[] url) {
        this.url = url;
    }
}
