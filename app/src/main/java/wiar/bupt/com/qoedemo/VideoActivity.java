package wiar.bupt.com.qoedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lx.ratingbarlib.MyRatingBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.paramsEntity.MonitorParams;
import io.vov.vitamio.paramsEntity.ParamsToPost;
import io.vov.vitamio.paramsEntity.TestParams;
import io.vov.vitamio.widget.VideoView;
import io.vov.vitamio.widget.VideoviewForQoe;
import wiar.bupt.com.qoedemo.service.QOEservice;

public class VideoActivity extends AppCompatActivity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {
    MyReceiver receiver=new MyReceiver();
    private static final String TAG="VideoActivity";
    private boolean bufferEnd = false;
    private boolean isPost = false;
    private String videoUri = "";
    private Uri uri;
    private Double videoStreamBitRate=0.0;
    private int videoLength=0;
    private String mimeType="";
    private ProgressBar pb;
    private CustomMediaController mCustomMediaController;
    private VideoviewForQoe mVideoView;
    private View mosview = null;
    private View backDialogView = null;
    private MyRatingBar ratingBar = null;
    private TextView paramstext = null;//要用里面这个控件，记得加上view
    private TextView backDialogText = null;//点击返回按钮时弹出的Dialog中的textview
    private List<MonitorParams> monitorList = new ArrayList<>();
    private List<TestParams> testList = new ArrayList<>();
    private ParamsToPost paramsToPost = new ParamsToPost();
    private String jsonString = "";
    private String name="";
    private MediaMetadataRetriever retriever = new MediaMetadataRetriever();

    DecimalFormat df1 = new DecimalFormat("0.0");

    TestParams testParamsObj = null;
    AlertDialog.Builder builder = null;
    AlertDialog.Builder builder_back = null;
    AlertDialog alertDialog = null;
    AlertDialog alertDialog_back = null;
    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            //告知handle,并将获取的qoe参数传过去
            Bundle bundle = new Bundle();
            //获取参数对象
            TestParams testParams = mVideoView.GetTestParams();
            bundle.putSerializable("test", testParams);
            Message msg = new Message();
            msg.setData(bundle);
            msg.what = 101;
            handler.sendMessage(msg);
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    //暂停timer
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (timerTask != null) {
                        timerTask.cancel();
                        timerTask = null;
                    }
                    //显示dialog
                    mVideoView.pause();
                    TestParams tp = (TestParams) msg.getData().getSerializable("test");
                    testParamsObj = tp;
                    if (alertDialog != null) {
                        alertDialog.show();
                    }
                    break;
            }
        }
    };
    //广播的定时器
    Timer broadcasttimer = new Timer();
    TimerTask broadcastttask;
    final Intent broadCastIntent=new Intent("monitorService");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = VideoActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        //必须写这个，初始化加载库文件
        Vitamio.isInitialized(this);
        setContentView(R.layout.activity_video);
        Bundle bundle = this.getIntent().getExtras();
        //获取viewList活动传来的参数:url,长度，码率
        videoUri = bundle.getString("uri");
        name=bundle.getString("videoname");
        try{
            retriever.setDataSource(videoUri, new HashMap<String, String>());
            videoStreamBitRate = Double.parseDouble(df1.format(Double.parseDouble(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)) / 1000));
            videoLength = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
            mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        }catch (Exception e){
            new AlertDialog.Builder(VideoActivity.this)
                    .setTitle("抱歉，视频初始化失败")
                    .setIcon(R.drawable.emoji)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
        initView();
        initData();//初始化vitamio所需参数
        //
        mVideoView.setVideoStreamBitRate(videoStreamBitRate);//设置已经获取的参数：码率
        mVideoView.setMimeType(mimeType);
        mVideoView.setVideoLength(videoLength);

        //启动服务
        Intent intent=new Intent(this,QOEservice.class);
        startService(intent);
        //准备向monitor服务发送广播
        broadCastIntent.putExtra("QoeOrder",1);
        broadcastttask = new TimerTask() {
            @Override
            public void run() {
                broadCastIntent.putExtra("BufferPercentage",mVideoView.getBufferPercentage());
                long currenttime = mVideoView.getCurrentPosition();
                broadCastIntent.putExtra("CurrentTime",currenttime);
                sendBroadcast(broadCastIntent);
            }
        };
        //每一秒发送一次广播1
        broadcasttimer.schedule(broadcastttask,0,1000);
        //注册广播接收器,接受monitor服务传回的list数据
        IntentFilter filter=new IntentFilter();
        filter.addAction("monitorParam from monitorService");
        VideoActivity.this.registerReceiver(receiver,filter);
    }


    //初始化控件
    private void initView() {
        mVideoView = (VideoviewForQoe) findViewById(R.id.buffer);
        mCustomMediaController = new CustomMediaController(this, mVideoView, this);
        mCustomMediaController.setVideoName(name);
        pb = (ProgressBar) findViewById(R.id.probar);
        VideoActivity.this.setFinishOnTouchOutside(false);
    }
    //初始化数据
    private void initData() {
        uri = Uri.parse(videoUri);
        //设置mVideoView播放器
        mVideoView.setVideoURI(uri);//设置视频播放地址

        mCustomMediaController.show(5000);
        mVideoView.setMediaController(mCustomMediaController);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//高画质
        mVideoView.requestFocus();
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                new AlertDialog.Builder(VideoActivity.this)
                        .setTitle("抱歉，当前网络无法连接视频")
                        .setIcon(R.drawable.emoji)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
                return true;
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 播放结束后的动作，清楚定时器和任务队列
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                //结束定时通知1
                broadcasttimer.cancel();
                broadcastttask.cancel();
                //发送通知2
                Intent broadCastIntent_2=new Intent("monitorService");
                broadCastIntent_2.putExtra("QoeOrder",2);
                sendBroadcast(broadCastIntent_2);
                isPost = true;//已经发送了通知2
            }
        });
        //获取自定义dialog view中的控件
        mosview = (RelativeLayout) getLayoutInflater().inflate(R.layout.mosdialog, null);
        backDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.backdialogview, null);
        backDialogText = (TextView)backDialogView.findViewById(R.id.backDialogText) ;
        ratingBar = (MyRatingBar) mosview.findViewById(R.id.ratingbar);
        //设置dialog
        builder = new AlertDialog.Builder(this);
        builder.setTitle("QoE评价");
        builder.setView(mosview);//这里添加上这个view
        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Toast.makeText(VideoActivity.this,"你好",Toast.LENGTH_SHORT).show();
            }
        });
        /*
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mVideoView.start();
                //获取用户主观Mos
                testParamsObj.setMos_sub(Double.parseDouble(ratingBar.getRating() + ""));
                testParamsObj.setTestTimeStamp(System.currentTimeMillis());
                testParamsObj.setTestTime(mVideoView.getCurrentPosition());
                //将该次test结果加入list
                testList.add(testParamsObj);
                //post
                Log.d(TAG, "testList中加入本次test结果: " + testParamsObj.toString());
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        //告知handle午时已到,并将获取的qoe参数传过去
                        Bundle bundle = new Bundle();
                        //获取参数对象
                        TestParams testParams = mVideoView.GetTestParams();
                        bundle.putSerializable("test", testParams);
                        Message msg = new Message();
                        msg.setData(bundle);
                        msg.what = 101;
                        handler.sendMessage(msg);
                    }
                };
                timer = new Timer();
                //重新启动timer
                timer.schedule(timerTask, 5000);//开始
            }
        });
        */
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mVideoView.start();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        //告知handle,并将获取的test参数传过去
                        Bundle bundle = new Bundle();
                        //获取参数对象
                        TestParams testParams = mVideoView.GetTestParams();
                        bundle.putSerializable("test", testParams);
                        Message msg = new Message();
                        msg.setData(bundle);
                        msg.what = 101;
                        handler.sendMessage(msg);
                    }
                };
                timer = new Timer();
                //重启timer
                timer.schedule(timerTask, 5000);//开始
            }
        });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        //设置定时器，当视频缓冲结束时，开始定时任务
        if (bufferEnd) {
            //如果已经缓冲完
            timer.schedule(timerTask, 5000);//开始
            Log.d(TAG, "1——开始 5s 定时任务！！！");
        }
        else {//若还未缓冲完,每过一秒钟判断一次是否缓冲完了
            Log.d(TAG,"还没缓冲完");
            final Timer t2 = new Timer();
            final TimerTask tt2 = new TimerTask() {
                @Override
                public void run() {
                    if(bufferEnd){
                        timer.schedule(timerTask, 5000);//开始
                        Log.d(TAG, "2——开始定时任务:若还未缓冲完,每过一秒钟判断一次是否缓冲完了");
                        t2.cancel();
                    }
                }
            };
            t2.schedule(tt2,0,1000);//每一秒执行一次
        }

        //设置点击返回按钮后的alertDialog
        builder_back = new AlertDialog.Builder(this);
        builder_back.setTitle("结束测试");
        builder_back.setView(backDialogView);//这里添加上这个view
        builder_back.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder_back.setNegativeButton("否",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收monitor服务传回的monitorList
            monitorList=(List<MonitorParams>)intent.getSerializableExtra("monitorList");
            //设置paramsToPost
            paramsToPost.setMonitor(monitorList);
            paramsToPost.setTest(testList);
            paramsToPost.setCpu(mVideoView.getMaxCpuFreq());
            paramsToPost.setMac(mVideoView.getMac());
            paramsToPost.setScreenPixels(mVideoView.getScreenPixels());

            paramsToPost.setMimeType(mimeType);
            paramsToPost.setHeight(mVideoView.getHeight());
            paramsToPost.setWidth(mVideoView.getWidth());
            paramsToPost.setVideoStreamBitRate(videoStreamBitRate);

            paramsToPost.setVideoLength(videoLength);

            paramsToPost.setInitTime(mVideoView.getiniTime());
            paramsToPost.setVideoBufferStart(mVideoView.getvideoBufferStart());
            //dopost
            Gson gson = new Gson();
            jsonString = gson.toJson(paramsToPost);
            Log.d(TAG,"最终生成的json字符串：  "+jsonString);
            mVideoView.postMetadata(jsonString);
            //接受到monitor参数并且post后，backmonitor_text上更新显示
            backDialogText.setText(" 感谢您的测试，是否上传？\njson长度："+jsonString.length());
        }
    }
    @Override
    public void onBackPressed() {
        //停止 5s 定时任务
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (alertDialog != null) {
            alertDialog = null;
        }
        //停止 1s 广播
        if(broadcasttimer!=null){
            broadcasttimer.cancel();
        }
        if(broadcastttask!=null){
            broadcastttask.cancel();
        }
        //显示提示框
        if( backDialogView.getParent() != null){
            ((ViewGroup) backDialogView.getParent()).removeView(backDialogView);
        }
        alertDialog_back = builder_back.create();
        alertDialog_back.setCanceledOnTouchOutside(false);
        alertDialog_back.show();

        //发送通知2
        if(!isPost){
            Intent broadCastIntent_2=new Intent("monitorService");
            broadCastIntent_2.putExtra("QoeOrder",2);
            sendBroadcast(broadCastIntent_2);
            isPost = true;
        }

    }
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer. MEDIA_ERROR_UNKNOWN:
                Toast.makeText(VideoActivity.this,"无法播放",Toast.LENGTH_SHORT).show();
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(VideoActivity.this,"网络不给力",Toast.LENGTH_SHORT).show();
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    //Log.e(TAG, "           缓冲开始！！！！！！！缓冲百分比："+mVideoView.getBufferPercentage());
                    pb.setVisibility(View.VISIBLE);//显示加载
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                pb.setVisibility(View.GONE);
                //Log.e(TAG, "缓冲结束！！！！！！！缓冲百分比："+mVideoView.getBufferPercentage());
                //开启定时服务
                bufferEnd = true;
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                //extra:下载速度
                break;
        }
        return true;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //屏幕切换时，设置全屏
        if (mVideoView != null) {
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        }
        super.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onStop() {
        //停止 5s 定时任务
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (alertDialog != null) {
            alertDialog = null;
        }
        //停止 1s 广播
        if(broadcasttimer!=null){
            broadcasttimer.cancel();
        }
        if(broadcastttask!=null){
            broadcastttask.cancel();
        }
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Log.e(TAG,"bufferPercentage: "+ percent);
    }
}


