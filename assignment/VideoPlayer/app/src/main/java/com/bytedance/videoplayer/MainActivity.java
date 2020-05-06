package com.bytedance.videoplayer;

import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    private static final int UPDATE_CURRENT_TIME=100;
    private VideoView videoView;
    private Button beginBtn, fullScreenBtn;
    private TextView totalTimeView, currentTimeView;
    private SeekBar seekBar;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        findViewById(R.id.activity_main).setBackgroundColor(Color.BLACK);
        videoView=findViewById(R.id.videoView);
        totalTimeView=findViewById(R.id.media_total_time);
        currentTimeView=findViewById(R.id.media_time);
        seekBar=findViewById(R.id.media_progress);
        beginBtn=findViewById(R.id.btn_begin_pause);
        fullScreenBtn=findViewById(R.id.media_full_screen);
        //通过外部实现URI播放
        Uri uri=getIntent().getData();
        videoView.setVideoURI(uri);
        //指定URI或本地path
//        videoView.setVideoPath("android.resource://" + this.getPackageName() + "/" + R.raw.bytedance);
//        videoView.setVideoURI(Uri.parse("http://jzvd.nathen.cn/video/1137e480-170bac9c523-0007-1823-c86-de200.mp4"));
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==UPDATE_CURRENT_TIME){
                    int currentTime=videoView.getCurrentPosition();
                    setTimeFromInt(currentTimeView,currentTime);
                    mHandler.sendEmptyMessageDelayed(UPDATE_CURRENT_TIME,100);
                    seekBar.setProgress(currentTime);
                }
            }
        };


        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.pause();
                }
                else{
                    videoView.start();
                    mHandler.sendEmptyMessage(UPDATE_CURRENT_TIME);
                }
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration=mp.getDuration();
                seekBar.setMax(duration);
                setTimeFromInt(totalTimeView,duration);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTimeFromInt(currentTimeView,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(UPDATE_CURRENT_TIME);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress=seekBar.getProgress();
                videoView.seekTo(progress);
                mHandler.sendEmptyMessage(UPDATE_CURRENT_TIME);
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //横屏时
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        }else{
            //竖屏时
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    public void setTimeFromInt(TextView view, int duration){
        int sec=duration/1000;
        int min=sec/60;
        sec=sec%60;
        int hour=min/60;
        min=min%60;
        String str=String.format("%d:%d:%2d",hour,min,sec);
        view.setText(str);
    }
}
