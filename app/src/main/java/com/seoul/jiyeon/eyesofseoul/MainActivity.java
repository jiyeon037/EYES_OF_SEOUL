package com.seoul.jiyeon.eyesofseoul;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {
    
    TextToSpeech tts;   // TextToSpeech 객체
    Button nbtn; // 뉴스로 넘어가는 임시 버튼
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nbtn = (Button) findViewById(R.id.nbtn);
        tts = new TextToSpeech(this,this);
        nbtn.setOnClickListener(this);
        permissionCheck();
    }

    private void permissionCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }
    }

    @Override
    public void onInit(int i) {
        String isIntroduce = "안녕하세요. 서울의 눈 입니다.";
        tts.speak(isIntroduce, TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.wbtn:
                break;
            case R.id.nbtn:
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(intent);

        }
    }
}
