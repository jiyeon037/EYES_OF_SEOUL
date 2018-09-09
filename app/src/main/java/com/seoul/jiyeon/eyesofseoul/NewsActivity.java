package com.seoul.jiyeon.eyesofseoul;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class NewsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextToSpeech tts;
    TextView titleText;
    TextView desText;
    StringBuffer forAsynkTaskSb;
    String forAsynkTaskS;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        tts = new TextToSpeech(this,this);
        titleText = (TextView) findViewById(R.id.titleText);
        layout = (LinearLayout) findViewById(R.id.newslayout);
        permissionCheck();

        try {
            forAsynkTaskSb = new SearchAsynkTask().execute(forAsynkTaskS).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        forAsynkTaskS = forAsynkTaskSb.toString();

        String newstitle;
        String newslink = null;
        String[] newsarray = new String[2];
        
        try {
            newsarray = newsJsonParser(forAsynkTaskS);

            newstitle = newsarray[0];
            newslink = newsarray[1];
            
                StringTokenizer st = new StringTokenizer(newstitle, "<b>|</b>|&quot;");
                while (st.hasMoreTokens()) {
                    newstitle = st.nextToken().toString();
                }

            titleText.setText(newstitle);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalNewslink = newslink;
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DescActivity.class);
                intent.putExtra("newslink", finalNewslink);
                startActivity(intent);
                finish();
            }
        });

        /*  // 다른 뉴스 원할 경우..
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });
        */
    }

    // 일단 1개짜리 해놓고 어레이리스트 사용하는걸로
    public String[] newsJsonParser(String jsonString) throws JSONException {

        String[] newsarray = new String[2];

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);

                String title = jObject.getString("title");
                String link = jObject.getString("link");

                newsarray[0] = title;
                newsarray[1] = link;
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return newsarray;
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
        // String tts_title = 파싱한 뉴스제목 읽어 줄 것
        // tts.speak(tts_title, TextToSpeech.QUEUE_ADD,null);
        String tts_desc = "이 뉴스를 들으시려면 한 번 터치, 다른 뉴스를 들으시려면 길게 터치해주세요";
        tts.speak(tts_desc, TextToSpeech.QUEUE_FLUSH,null);
    }


    class SearchAsynkTask extends AsyncTask<String, String, StringBuffer> {
        @Override
        protected StringBuffer doInBackground(String... search) {
            StringBuffer sb = new StringBuffer();
            String clientID = "qoV_o0JFXTXLeGQHxVp5";
            String clientSecret = "NB1rRZLIVm";
            int display = 50; // 검색 결과 개수

            try {
                String text = URLEncoder.encode("서울시","UTF-8");
                String apiURL = "https://openapi.naver.com/v1/search/news?query="+ text + "&display=" + display + "&"; // JSON 결과

                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-Id", clientID);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200){  // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {    // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

            }catch (Exception e){
                e.printStackTrace();
            }
            return sb;
        }

        @Override
        protected void onPostExecute(StringBuffer stringBuffer) {

        }
    }

}