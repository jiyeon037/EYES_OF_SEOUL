package com.seoul.jiyeon.eyesofseoul;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NewsActivity extends AppCompatActivity {

    TextView searchText;
    StringBuffer sb = new StringBuffer();
    SearchAsynkTask task = new SearchAsynkTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        searchText = (TextView)findViewById(R.id.searchText);

        task.execute();
    }

    class SearchAsynkTask extends AsyncTask<String, String, StringBuffer> {
        @Override
        protected StringBuffer doInBackground(String ... search) {
            String clientID = "qoV_o0JFXTXLeGQHxVp5";
            String clientSecret = "NB1rRZLIVm";
            int display = 1; // 검색 결과 개수

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

            }
            return sb;
        }

        @Override
        protected void onPostExecute(StringBuffer stringBuffer) {
            searchText.setText(sb.toString());
        }
    }
}
