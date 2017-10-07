package wiar.bupt.com.qoedemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import wiar.bupt.com.qoedemo.utils.Util;
import wiar.bupt.com.qoedemo.utils.Video;
import wiar.bupt.com.qoedemo.utils.VideoAdapter;
import wiar.bupt.com.qoedemo.utils.VideoUrls;

public class ListActivity extends Activity {
    private Banner banner;

    private List<Video> videoList = new ArrayList<>();
    private List<VideoUrls> videoList_banner = new ArrayList<>();
    String[] images = new String[]{};
    String[] titles = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_list);
        initBannerVideos();
        initTypeVideos();
        ListView listView = (ListView) findViewById(R.id.list_view);
        VideoAdapter adapter = new VideoAdapter(ListActivity.this, R.layout.video_item, videoList);
        listView.setAdapter(adapter);

        banner = (Banner) findViewById(R.id.banner);
        banner.setBannerStyle(Banner.CIRCLE_INDICATOR_TITLE);
        banner.setIndicatorGravity(Banner.CENTER);
        banner.setBannerTitle(titles);
        banner.isAutoPlay(true);
        banner.setDelayTime(5000);

        banner.setImages(images, new Banner.OnLoadImageListener() {
            @Override
            public void OnLoadImage(ImageView view, Object url) {
                Glide.with(getApplicationContext()).load(url).into(view);
            }
        });
        //设置点击事件，下标是从1开始
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
            @Override
            public void OnBannerClick(View view, int position) {
                boolean isWifi = Util.isWifiNetwrokType(ListActivity.this);
                if (isWifi) {
                    VideoUrls bannervideo = videoList_banner.get(position-1);
                    Intent intent = new Intent(ListActivity.this, VideoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uri", bannervideo.getUrl());
                    bundle.putString("videoname", bannervideo.getName());
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(ListActivity.this,"请先打开WiFi",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void initTypeVideos() {
        //分类视频初始化
        Video v1 = new Video(first(R.drawable.shijiaqiu), first(R.drawable.suya), "体育",
                new String[]{"懂球帝一周十佳球", "“无解”苏神西甲狂轰86球"},
                new String[]{"https://o6yh618n9.qnssl.com/%E5%8D%81%E4%BD%B3%E7%90%83.mp4", "https://o6yh618n9.qnssl.com/fQDTeU4q_1431136051.mp4"}
        );
        videoList.add(v1);
        Video v2 = new Video(first(R.drawable.sishi), first(R.drawable.xiaochouhuihun), "影视",
                new String[]{"死侍", "《小丑回魂》正片片段"},
                new String[]{"http://10.103.93.48:8080/WiBUPTInfo/sishi.mp4", "http://10.103.93.48:8080/WiBUPTInfo/xiaochouhuihun.mp4"}
        );
        videoList.add(v2);

        Video v3 = new Video(first(R.drawable.rain), first(R.drawable.wark), "自然",
                new String[]{"超慢运动中的雨滴", "步行穿过大麦田"},
                new String[]{"http://10.103.93.48:8080/WiBUPTInfo/rain.mov", "http://img95.699pic.com/videos/2016/09/18/9e1d06a6-2e0a-4e29-bafc-027e808dbdbc.mp4"}
        );
        videoList.add(v3);
        Video v4 = new Video(first(R.drawable.jiqimao), first(R.drawable.kenan), "动漫",
                new String[]{"哆啦A梦全集第1集", "名侦探柯南"},
                new String[]{"http://10.103.93.48:8080/WiBUPTInfo/duolaameng.mp4", "http://10.103.93.48:8080/WiBUPTInfo/kenan.mp4"}
        );
        videoList.add(v4);

    }


    private void initBannerVideos() {
        //banner初始化
        videoList_banner.add(new VideoUrls("年度大戏之谦谦君子", "http://10.103.93.48:8080/WiBUPTInfo/xuezhiqian.mp4"));
        videoList_banner.add(new VideoUrls("党的十九大召开", "http://10.103.93.48:8080/WiBUPTInfo/shijiuda.mp4"));

        titles = new String[]{"年度大戏之谦谦君子", "党的十九大召开"};
        images = new String[]{
                "http://img4.duitang.com/uploads/item/201611/16/20161116211301_HnkyE.jpeg",
                "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1274483882,274555467&fm=27&gp=0.jpg"};

    }
    //图片添加播放按钮
    public Bitmap first(int video) {
        // 防止出现Immutable bitmap passed to Canvas constructor错误
        Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(),
                video).copy(Bitmap.Config.ARGB_8888, true), 400, 400);//指定视频播放图片大小
        Bitmap bitmap2 = ThumbnailUtils.extractThumbnail(((BitmapDrawable) getResources().getDrawable(
                R.drawable.go)).getBitmap(), 100, 100);//指定播放按钮图片大小
        Bitmap newBitmap = null;
        newBitmap = Bitmap.createBitmap(bitmap1);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        int w = bitmap1.getWidth();
        int h = bitmap1.getHeight();
        int w_2 = bitmap2.getWidth();
        int h_2 = bitmap2.getHeight();
        paint.setColor(Color.GRAY);
        paint.setAlpha(125);
        canvas.drawRect(0, 0, bitmap1.getWidth(), bitmap1.getHeight(), paint);
        paint = new Paint();
        canvas.drawBitmap(bitmap2, Math.abs(w - w_2) / 2,
                Math.abs(h - h_2) / 2, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储新合成的图片
        canvas.restore();
        return newBitmap;
    }
}

