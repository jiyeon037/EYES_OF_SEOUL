package com.seoul.jiyeon.eyesofseoul;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherActivity2 extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextView wText, highTemp, lowTemp;
    Document doc = null;
    RelativeLayout layout;
    ImageView wIcon;
    TextToSpeech tts;
    GestureDetector gd;

    String highTmp = "";
    String lowTmp = "";
    String amWeather = "";
    String pmWeather = "";
    String sMonth = "";
    String sDate = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_2);

        layout = findViewById(R.id.rl1);
        highTemp = findViewById(R.id.highTemp);
        lowTemp = findViewById(R.id.lowTemp);
        wText = findViewById(R.id.wText);
        wIcon = findViewById(R.id.wIcon);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat curMonth = new SimpleDateFormat("MM");
        SimpleDateFormat curDate = new SimpleDateFormat("dd");

        sMonth = curMonth.format(date);
        sDate = curDate.format(date);

        int idate = Integer.parseInt(sDate) + 1;
        sDate =  Integer.toString(idate);

        GetXMLTask task = new GetXMLTask();
        task.execute("http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=1171056100");

        tts = new TextToSpeech(this,this);
        permissionCheck();

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
                Intent intent = new Intent(getApplicationContext(), WeatherActivity2.class);
                startActivity(intent);
                finish();
                super.onLongPress(e);
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
        String isIntroduce = "내일의 서울 날씨 입니다. 내일 오전 날씨는 "+ amWeather + ". 오후 날씨는 "+ pmWeather +" 입니다. " +
                "내일의 최고 기온은"+ highTmp + "도. 최저 기온은 " + lowTmp + "도 입니다." +
                " 다시 들으시려면 화면을 길게 터치해주세요. 초기 메뉴로 돌아가시려면 화면을 두 번 터치해주세요.";
        tts.speak(isIntroduce, TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
    private class GetXMLTask extends AsyncTask<String,Void,Document> {
        @Override
        protected Document doInBackground(String... strings) {
            URL url;
            try{
                url = new URL(strings[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            }catch (Exception e){
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document document) {

            float fLowTmp,fHighTmp;
            int iLowTmp,iHighTmp;

            String day="";
            String hour="";
            NodeList nodeList = doc.getElementsByTagName("data");

            for(int i=0; i<nodeList.getLength();i++){

                Node node = nodeList.item(i);

                Element element = (Element) node;

                NodeList dayList = element.getElementsByTagName("day");
                day = dayList.item(0).getChildNodes().item(0).getNodeValue();

                NodeList hourList = element.getElementsByTagName("hour");
                hour = hourList.item(0).getChildNodes().item(0).getNodeValue();

                if(day.equals("1")){
                    if(hour.equals("9")){
                        NodeList lowTmpList = element.getElementsByTagName("tmn");
                        lowTmp = lowTmpList.item(0).getChildNodes().item(0).getNodeValue();
                        fLowTmp = Float.parseFloat(lowTmp);
                        iLowTmp = (int)fLowTmp;
                        lowTmp = Integer.toString(iLowTmp);

                        NodeList highTmpList = element.getElementsByTagName("tmx");
                        highTmp = highTmpList.item(0).getChildNodes().item(0).getNodeValue();
                        fHighTmp = Float.parseFloat(highTmp);
                        iHighTmp = (int)fHighTmp;
                        highTmp = Integer.toString(iHighTmp);

                        NodeList amWeatherList = element.getElementsByTagName("wfKor");
                        amWeather = amWeatherList.item(0).getChildNodes().item(0).getNodeValue();
                    }
                    if(hour.equals("15")){
                        NodeList pmWeatherList = element.getElementsByTagName("wfKor");
                        pmWeather = pmWeatherList.item(0).getChildNodes().item(0).getNodeValue();
                    }

                }
            } //end of for

            lowTemp.setText(lowTmp+"º");
            highTemp.setText(highTmp+"º");
            wText.setText(amWeather);

            if(amWeather.equals("맑음")){
                wIcon.setImageResource(R.drawable.sunny);
            }else if(amWeather.equals("구름 조금") || amWeather.equals("구름 많음")) {
                wIcon.setImageResource(R.drawable.suncloud);
            }else if(amWeather.equals("흐림")){
                wIcon.setImageResource(R.drawable.cloudy);
            }else if(amWeather.equals("비")){
                wIcon.setImageResource(R.drawable.rainy);
            }

            super.onPostExecute(document);
        }
    }
}
