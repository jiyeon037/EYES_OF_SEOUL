package com.seoul.jiyeon.eyesofseoul;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class NewsActivity extends AppCompatActivity {

    TextView titleText;
    TextView desText;
    StringBuffer forAsynkTaskSb;
    String forAsynkTaskS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        titleText = (TextView) findViewById(R.id.titleText);
        desText = (TextView) findViewById(R.id.desText);

        try {
            forAsynkTaskSb = new SearchAsynkTask().execute(forAsynkTaskS).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        forAsynkTaskS = forAsynkTaskSb.toString();

        String[] newsarray = new String[1];
        try {
            newsarray = newsJsonParser(forAsynkTaskS);

            Log.d("파싱전스트링",newsarray[0]);
            Log.d("파싱전스트링",newsarray[1]);

            for(int i=0; i<2; i++) {
                StringTokenizer st = new StringTokenizer(newsarray[i], "<b>");
                while (st.hasMoreTokens()) {
                    newsarray[i] = st.nextToken();
                }
                st = new StringTokenizer(newsarray[i], "<b/>");
                while (st.hasMoreTokens()) {
                    newsarray[i] = st.nextToken();
                }
                st = new StringTokenizer(newsarray[i], "&quot;");
                while (st.hasMoreTokens()) {
                    newsarray[i] = st.nextToken();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("파싱후스트링",newsarray[0]);
        Log.d("파싱후스트링",newsarray[1]);

        titleText.setText(newsarray[0]);
        desText.setText(newsarray[1]);

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
                String description = jObject.getString("description");

                newsarray[0] = title;
                newsarray[1] = description;
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return newsarray;
    }


    class SearchAsynkTask extends AsyncTask<String, String, StringBuffer> {
        @Override
        protected StringBuffer doInBackground(String... search) {
            StringBuffer sb = new StringBuffer();
            String clientID = "qoV_o0JFXTXLeGQHxVp5";
            String clientSecret = "NB1rRZLIVm";
            int display = 1; // 검색 결과 개수

            try {
                String text = URLEncoder.encode("박원순","UTF-8");
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