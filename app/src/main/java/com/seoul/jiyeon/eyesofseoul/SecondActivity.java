package com.seoul.jiyeon.eyesofseoul;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class SecondActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    TextToSpeech tts;
    LinearLayout layout1;
    GestureDetector gd=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        layout1 = findViewById(R.id.secondLinear);
        tts = new TextToSpeech(this,this);
        permissionCheck();

        gd = new GestureDetector(layout1.getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Intent intent = new Intent(SecondActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                startActivity(intent);
                finish();

                return super.onDoubleTap(e);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gd.onTouchEvent(event);
    }

    private void permissionCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }
    @Override
    public void onInit(int i) {
        String isIntroduce = "날씨 예보를 들으시려면 화면을 한 번, 오늘의 뉴스를 들으시려면 화면을 두 번 터치해주세요. ";
        tts.speak(isIntroduce, TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

}
