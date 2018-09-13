package com.seoul.jiyeon.eyesofseoul;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class DescActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    CrwalerTask crwalerTask = new CrwalerTask();
    TextToSpeech tts;
    String crwaledDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);

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
        tts.speak(crwaledDesc, TextToSpeech.QUEUE_FLUSH,null);
    }

    class CrwalerTask extends AsyncTask<String, Void, String>{

        //Intent intent = getIntent();
        //String articleURL = intent.getStringExtra("newslink");
        String testURL = "https://news.naver.com/main/read.nhn?mode=LSD&mid=sec&sid1=103&oid=028&aid=0002424560";

        @Override
        protected String doInBackground(String... strings) {
            Document doc = null;
            try {
                doc = Jsoup.connect(testURL).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements ele = doc.select("div#articleBodyContents");
            String str = ele.text();

            Log.d("bbbbbbbbbbbbbb",str);

            return str;
        }
    }
}
