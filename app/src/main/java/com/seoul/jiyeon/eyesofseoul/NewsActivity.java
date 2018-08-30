package com.seoul.jiyeon.eyesofseoul;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn;
    TextView searchText;
    String keyword = "서울시";
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        btn = (Button)findViewById(R.id.btn);
        searchText = (TextView)findViewById(R.id.searchText);

        btn.setOnClickListener(this);
    }

    public String getNaverSearch(String keyword) {

        StringBuffer sb = new StringBuffer();
        String clientID = "qoV_o0JFXTXLeGQHxVp5";
        String clientSecret = "NB1rRZLIVm";

        try {
            String text = URLEncoder.encode("서울시", "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/news.xml?query=" + text;

            //StringBuffer sb = new StringBuffer();


            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Naver-Client-Id", clientID);
            conn.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            String tag;
            xpp.setInput(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if (tag.equals("item"));
                        else if (tag.equals("title")) {

                            sb.append("제목 : ");

                            xpp.next();
                        }
                        break;
                }

                eventType = xpp.next();

                return sb.toString();
            }

        } catch (Exception e) {
            return e.toString();

        }
        return sb.toString();
    }

    public void onClick(View v) {
        keyword = searchText.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                str = getNaverSearch(keyword);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchText.setText(str);

                    }
                });
            }
        }).start();
    }
}
