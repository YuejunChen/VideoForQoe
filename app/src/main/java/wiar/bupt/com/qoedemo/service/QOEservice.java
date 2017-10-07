package wiar.bupt.com.qoedemo.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.paramsEntity.MonitorParams;

public class QOEservice extends Service {
    private static final String TAG = "QOEservice";
    MyReceiver serviceReceiver;
    private long total_data = TrafficStats.getTotalRxBytes();
    private long send_data = TrafficStats.getTotalTxBytes();
    private final int count = 1;
    private List<MonitorParams> list = new ArrayList<MonitorParams>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        serviceReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("monitorService");
        registerReceiver(serviceReceiver, filter);
    }

    //得到当前的下载速度
    private int getNetSpeed() {
        long traffic_data = TrafficStats.getTotalRxBytes() - total_data;
        total_data = TrafficStats.getTotalRxBytes();
        return (int) traffic_data / count;
    }

    //得到当前的上传速度
    private int getSendSpeed() {
        long traffic_data = TrafficStats.getTotalTxBytes() - send_data;
        send_data = TrafficStats.getTotalTxBytes();
        return (int) traffic_data / count;
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

    private final static String CurPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";//保存当前CPU频率
    //获取当前CPU频率
    public static double getCurCPU() {
        double result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(CurPath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Double.parseDouble(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result/1000;
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            int control = intent.getIntExtra("QoeOrder", -1);
            switch (control) {
                case 1:
                    //当前的上传速度
                    int sendspeed = getSendSpeed();//bps
                    //当前的下载速度
                    int netspeed = getNetSpeed();
                    //缓冲百分比
                    int bufferpercentage = intent.getIntExtra("BufferPercentage", 0);
                    //播放时间
                    long currenttime = intent.getLongExtra("CurrentTime", 0);
                    //当前时间戳
                    long timestamp = System.currentTimeMillis();
                    //内存
                    float memoryconsumption = getMemoryConsumption(getBaseContext());
                    double currentcpu = getCurCPU();
                    MonitorParams monitorParams = new MonitorParams(sendspeed, netspeed, bufferpercentage, currenttime, timestamp, memoryconsumption,currentcpu);
                    list.add(monitorParams);
                    Log.d(TAG, monitorParams.toString());
                    break;
                case 2:
                    //向主线程返回list对象
                    //发送广播
                    Intent back_intent = new Intent();
                    back_intent.putExtra("monitorList", (Serializable) list);
                    back_intent.setAction("monitorParam from monitorService");
                    sendBroadcast(back_intent);
                    list.clear();
                    break;
                default:
                    break;
            }
        }
    }
}
