package io.vov.vitamio.widget;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.Timestamp;
import java.text.DecimalFormat;

import io.vov.vitamio.paramsEntity.TestParams;
import io.vov.vitamio.utils.Log;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * Created by pengfeng on 2017/6/19.
 */

public class VideoviewForQoe extends VideoView {
    private static final String TAG="VideoviewForQoe";
    private MediaMetadataRetriever retriever;
    private double cpu;//•	cpu主频/KHz
    private float memoryConsumption;//•	内存消耗量/%
    private int screenPixels;//•	屏幕像素密度/ppi
    private double videoStreamBitRate;//•	视频流平均比特率/kbps
    private String mimeType;
    private double initBuffer;//•	初始化设置的缓冲区大小/s
    private int videoLength;//•	视频长度/s
    private double messageDelay = -1;//•	消息时延/s
    private double latitude;
    private double longitude;
    private long initTime;//初始缓冲时间/ms
    private String MAC = "";
    private int width = 0;
    private int height = 0;
    private long videoBufferStart ;
    private float loss;
    private LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener;
    private LocationClientOption option;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static int BufferSettingSize = 1024 * 1024;//设置初始化缓冲区大小 Byte  0.5M
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle;
            switch (msg.what) {
                case 1://ping结束
                    bundle = msg.getData();
                    String delay_string = bundle.getString("delay");
                    String loss_string = bundle.getString("loss");
                    String[] strarr = delay_string.split("/");
                    if (strarr.length > 1)
                        messageDelay = Double.parseDouble(strarr[1]);
                    if(loss_string.length()>0)
                        loss = Float.parseFloat(loss_string);
                    //Log.e("loss: "+loss);
                    break;
                case 2:
                    //post线程完成
                    break;
            }
        }
    };

    public VideoviewForQoe(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoviewForQoe(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public VideoviewForQoe(Context context) {
        super(context);
        init();
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
        messageDelay = -1;//消息时延初始化
        getMessageDelay(getVideoURI());//开启ping线程，当正确ping通后，messagedelay参数会被修改
    }

    private void init() {
        //retriever = new MediaMetadataRetriever();
        super.setBufferSize(BufferSettingSize);//设置视频的缓冲大小，单位是byte
        mLocationClient = new LocationClient(getContext());
        mBDLocationListener = new MyBDLocationListener();
        // 注册定位监听
        mLocationClient.registerLocationListener(mBDLocationListener);
        option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式 高精度
        option.setCoorType("bd09ll");// 设置返回定位结果是百度经纬度 默认gcj02
        option.setScanSpan(3000);// 设置发起定位请求的时间间隔 单位ms
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 设置定位结果包含手机机头 的方向
        if (mLocationClient.isStarted())
            mLocationClient.stop();
        mLocationClient.setLocOption(option);
        // 启动定位
        mLocationClient.start();
        cpu = getMaxCpuFreq();
        screenPixels = getScreenSizeOfDevice2(getContext());
        MAC = getMac();
    }

    /*
    获取test参数信息的对象，每5s获取一次
     */
    public TestParams GetTestParams() {
        //重新获取地理信息
        if (mLocationClient.isStarted())
            mLocationClient.stop();
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        //重新获取消息时延
        getMessageDelay(getVideoURI());//开启ping线程，当正确ping通后，messagedelay参数会被修改
        //用戶打分及test时间戳在activity中set
        TestParams testParams  = new TestParams(0,0,0,messageDelay,longitude, latitude);
        return testParams;
    }
    //
    public TestParams GetShowParams() {
        //视频长度和高度,长度,初次开始缓冲时间戳
        width = getVideoWidth();
        height = getVideoHeight();
        videoBufferStart = getvideoBufferStart();
        //重新获取地理信息
        if (mLocationClient.isStarted())
            mLocationClient.stop();
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        //重新获取消息时延
        getMessageDelay(getVideoURI());//开启ping线程，当正确ping通后，messagedelay参数会被修改
        initTime = super.getiniTime();
        initBuffer = super.getBufferSize() * 8 / videoStreamBitRate / 1000;
        DecimalFormat df2 = new DecimalFormat("0.00");
        initBuffer = Double.parseDouble(df2.format(initBuffer));
        DecimalFormat df1 = new DecimalFormat("0.0");
        TestParams testParams  = null;
        //Log.e(TAG,"width: "+width+",height: "+height);
        return testParams;
    }
    /*
    post上传所收集的参数
     */
    public void postMetadata(final String Message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String Mes = Message;
                    OkHttpClient client = new OkHttpClient();
                    String responseData;
                    RequestBody body = RequestBody.create(JSON, Mes);
                    Request request = new Request.Builder()
                            .url("http://10.103.93.23:8080/qoe/api/detect/insert")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        responseData = response.body().string();
                    } else {
                        throw new IOException("Unexpected code " + response.toString() + "\n" + Mes);
                        //保存本次记录
                    }
                    Bundle bundle = new Bundle();
                    Message msg = new Message();
                    bundle.putString("postRes", responseData);
                    msg.what = 2;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //获取MAC
    public String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        Log.d(TAG,"macSerial"+macSerial);
        macSerial.replace(":","");
        Log.d(TAG,"replace 之后 ：macSerial"+macSerial);
        return macSerial;
    }

    //1.cpu主频
    public double getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return Double.parseDouble(result.trim()) / 1000; //khz
    }

    //2.获取内存消耗量
    public float getMemoryConsumption(Context context) {
        float totalMemory;//获取总内存
        float availMemory;//获取可用内存
        //获取ActivityManager管理，要获取【运行相关】的信息，与运行相关的信息有关
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();//outInfo对象里面包含了内存相关的信息
        activityManager.getMemoryInfo(outInfo);//把内存相关的信息传递到outInfo里面
        totalMemory = outInfo.totalMem;
        availMemory = outInfo.availMem;
        return (totalMemory - availMemory) / totalMemory * 100;
    }

    //3.获屏幕像素密度
    public int getScreenSizeOfDevice2(Context context) {
        Point point = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        double x = Math.pow(point.x / dm.xdpi, 2);
        double y = Math.pow(point.y / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        double ppi = Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2)) / screenInches;
        return (int) ppi;
    }

    //7.获取消息时延
    public double getMessageDelay(Uri url) {
        final String host = url.getHost();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process process;
                String line = null;
                BufferedReader successReader = null;
                String command = "ping -c 3 " + host;
                Bundle bundle = new Bundle();
                String delay = "";
                String loss = "";
                StringBuffer sb = new StringBuffer();
                try {
                    process = Runtime.getRuntime().exec(command);
                    if (process == null) {
                        bundle.putString("delay", "failPingStr");
                        Message msg = new Message();
                        msg.what = 1;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    } else {
                        successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        while ((line = successReader.readLine()) != null) {
                            if (line.contains("avg/max")) {
                                //Log.e("pf", "ping 的返回信息: " + line);
                                delay = line.substring(line.lastIndexOf("=") + 1, line.lastIndexOf("ms"));
                                //sb.append(line.substring(line.lastIndexOf("=") + 1, line.lastIndexOf("ms")));
                            }
                            if(line.contains("packet loss")){
                                int i= line.indexOf("received");
                                int j= line.indexOf("%");
                                loss = line.substring(i+10, j+1);
                            }
                        }
                        bundle.putString("delay", delay);
                        bundle.putString("loss",loss);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    bundle.putString("delay", "failPingStr");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
        return 0;
    }

    class MyBDLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // 非空判断
            if (location != null) {
                // 根据BDLocation 对象获得经纬度以及详细地址信息
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Message msg = new Message();
                msg.what = 3;
                mHandler.sendMessage(msg);
                if (mLocationClient.isStarted()) {
                    // 获得位置之后停止定位
                    mLocationClient.stop();
                }
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    public void setVideoStreamBitRate(double videoStreamBitRate) {
        this.videoStreamBitRate = videoStreamBitRate;
    }

    public double getVideoStreamBitRate() {
        return videoStreamBitRate;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(int videoLength) {
        this.videoLength = videoLength;
    }

    public int getScreenPixels() {
        return screenPixels;
    }
}
