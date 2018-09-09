package com.seoul.jiyeon.eyesofseoul;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DescActivity extends AppCompatActivity {

    CrwalerTask crwalerTask = new CrwalerTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);

        crwalerTask.execute();

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
