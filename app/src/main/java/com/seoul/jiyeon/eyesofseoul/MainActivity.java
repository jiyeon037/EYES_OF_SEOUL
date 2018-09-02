package com.seoul.jiyeon.eyesofseoul;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener  {

    RelativeLayout r1;
    TextToSpeech tts;
    TextSwitcher tSwitch;
    String textToShow[] = {"안녕하세요", "서울의 눈 입니다","아무 곳이나 눌러주세요"};
    int tCount = textToShow.length;
    Handler hand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        r1 = findViewById(R.id.relative1);
        tSwitch = findViewById(R.id.textSwitch);

        tts = new TextToSpeech(this,this);
        permissionCheck();

        tSwitch.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView myText = new TextView(MainActivity.this);
                myText.setGravity(Gravity.CENTER);
                myText.setTextSize(50);
                myText.setTextColor(Color.WHITE);
                myText.setTypeface(null, Typeface.BOLD);
                return myText;
            }
        });

        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.fade_out);

        tSwitch.setInAnimation(in);
        tSwitch.setOutAnimation(out);

        hand = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                tSwitch.setText(textToShow[msg.arg1]);
            }
        };
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while(count<tCount){
                    Message msg = hand.obtainMessage();

                    msg.arg1 = count;
                    count++;
                    hand.sendMessage(msg);
                    try {
                        Thread.sleep(2000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        });

        th.start();

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
        String isIntroduce = "안녕하세요. 서울의 눈 입니다. 계속하시려면 아무 곳이나 눌러주세요.";
        tts.speak(isIntroduce, TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

}
