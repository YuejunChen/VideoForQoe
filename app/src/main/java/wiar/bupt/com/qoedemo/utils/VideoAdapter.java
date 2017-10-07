package wiar.bupt.com.qoedemo.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wiar.bupt.com.qoedemo.R;
import wiar.bupt.com.qoedemo.VideoActivity;

/**
 * Created by Mr.Chen on 2017/9/18.
 */
public class VideoAdapter extends ArrayAdapter<Video> {

    private int resourceId;
    private List<Video> videoList=new ArrayList<>();
    public VideoAdapter(Context context, int textViewResourceId,
                        List<Video> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.videoList = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Video video = getItem(position);
        View view;
        final ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.videoPik = (ImageView) view.findViewById (R.id.pic_one);
            viewHolder.videoPic = (ImageView) view.findViewById (R.id.pic_two);
            viewHolder.videoType=(TextView)view.findViewById(R.id.video_type);
            viewHolder.text1 = (TextView)view.findViewById(R.id.text_one);
            viewHolder.text2 = (TextView)view.findViewById(R.id.text_two);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.videoPik.setImageBitmap(video.getPicBitmap());
        viewHolder.videoPic.setImageBitmap(video.getImageBitmap());
        viewHolder.text1.setText(video.getName()[0]);
        viewHolder.text2.setText(video.getName()[1]);
        viewHolder.videoType.setText(video.getVideoType());
        final String VideoType =viewHolder.videoType.getText().toString();
        if(VideoType.equals("体育")){
            viewHolder.videoType.setTextColor(Color.parseColor("#3366CC"));
        }else if(VideoType.equals("动漫")){
            viewHolder.videoType.setTextColor(Color.parseColor("#FF1493"));
        }else{
            viewHolder.videoType.setTextColor(Color.parseColor("#CC0000"));
        }
        //设置监听事件
        viewHolder.videoPik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isWifi = Util.isNetworkAvaliable(getContext());
                if(isWifi){
                    Intent intent = new Intent(v.getContext(), VideoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uri", videoList.get(position).getUrl()[0]);
                    bundle.putString("videoname", videoList.get(position).getName()[0]);
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(),"请连接WiFi",Toast.LENGTH_SHORT).show();
                }

            }
        });
        viewHolder.videoPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isWifi = Util.isNetworkAvaliable(getContext());
                if(isWifi){
                    Intent intent = new Intent(v.getContext(), VideoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uri", videoList.get(position).getUrl()[1]);
                    bundle.putString("videoname", videoList.get(position).getName()[1]);
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(),"请连接WiFi",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    class ViewHolder {
        ImageView videoPik;
        ImageView videoPic;
        TextView  videoType;
        TextView text1;
        TextView text2;
    }

}
