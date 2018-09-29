package com.seoul.jiyeon.eyesofseoul;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class DescActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    CrwalerTask crwalerTask = new CrwalerTask();
    TextToSpeech tts;
    String crwaledDesc;
    ImageView ivSpeaker;
    AnimationDrawable ani;
    LinearLayout layout;
    GestureDetector gd = null;
    String articleURL;
    int seq;
    int intro = 0;

    Elements articeBody = null;
    Elements articleBodyContents = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);

        ivSpeaker = findViewById(R.id.ivSpeaker);
        layout = findViewById(R.id.layout);

        ani=(AnimationDrawable)ivSpeaker.getDrawable();
        ani.setOneShot(false);

        ani.start();

        articeBody = null;
        articleBodyContents = null;

        Intent intent = getIntent();
        articleURL = intent.getStringExtra("newslink");
        seq = intent.getIntExtra("seq",0);
        intro = intent.getIntExtra("intro",0);

        gd = new GestureDetector(layout.getContext(), new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
                finish();

                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Intent intent = new Intent(getApplicationContext(),NewsActivity.class);
                seq += 1;
                intent.putExtra("seq",seq);
                intent.putExtra("intro",1);
                startActivity(intent);
                finish();
                super.onLongPress(e);
            }
        });


        tts = new TextToSpeech(this,this);
        permissionCheck();



        try {
            crwaledDesc = crwalerTask.execute(crwaledDesc).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.d("cccccccccccccc",crwaledDesc);

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

        if(intro == 0){
            tts.speak("뉴스를 재생합니다. 중간에 화면을 길게 터치하면 다음 뉴스 제목을 재생합니다. 화면을 두 번 터치하시면 초기 메뉴로 돌아갑니다. " + crwaledDesc, TextToSpeech.QUEUE_FLUSH,null);
        }else if(intro != 0){
            tts.speak(crwaledDesc, TextToSpeech.QUEUE_FLUSH,null);
        }

    }

    class CrwalerTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            Document doc = null;
            try {
                doc = Jsoup.connect(articleURL).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String str = "";


            articeBody = doc.select("div#articeBody");

            articleBodyContents = doc.select("div#articleBodyContents");



            if(!articeBody.isEmpty()){
                str = articeBody.text();
            }


            if(!articleBodyContents.isEmpty()) {
                str = articleBodyContents.text();
            }

            Log.d("bbbbbbbbbbbbbb",str);

            return str;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}
