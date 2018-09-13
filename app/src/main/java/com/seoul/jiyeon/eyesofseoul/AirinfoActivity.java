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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AirinfoActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    Document doc = null;
    RelativeLayout layout;
    GestureDetector gd = null;
    TextToSpeech tts;

    TextView airtext, airnum,dusttext, dustnum;
    ImageView airimg;

    String grade = "";
    String gradeNum = "";
    String dust = "";
    String sdust = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airinfo);

        layout = findViewById(R.id.layout);
        airtext = findViewById(R.id.airtext);
        airnum = findViewById(R.id.airnum);
        airimg = findViewById(R.id.airimg);
        dustnum = findViewById(R.id.dustnum);
        dusttext = findViewById(R.id.dusttext);

        GetXMLTask task = new GetXMLTask();
        task.execute("http://openapi.seoul.go.kr:8088/sample/xml/ListAvgOfSeoulAirQualityService/1/5/");

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
                Intent intent = new Intent(getApplicationContext(), AirinfoActivity.class);
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
        String isIntroduce = "현재 서울시 대기정보 입니다. 대기 환경지수는 " + gradeNum +"... " + grade + "입니다."+ " 미세먼지 양은 " + dust +".. " + sdust + "입니다." +
                ".다시 들으시려면 화면을 길게. 초기 메뉴로 돌아가시려면 화면을 두 번 터치해주세요.";
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

            int idust;

            NodeList nodeList = doc.getElementsByTagName("row");

            Node node = nodeList.item(0);
            Element element = (Element) node;

            NodeList gradeList = element.getElementsByTagName("GRADE");
            grade = gradeList.item(0).getChildNodes().item(0).getNodeValue();

            NodeList gradeNumList = element.getElementsByTagName("IDEX_MVL");
            gradeNum = gradeNumList.item(0).getChildNodes().item(0).getNodeValue();

            NodeList dustList = element.getElementsByTagName("PM10");
            dust = dustList.item(0).getChildNodes().item(0).getNodeValue();
            idust = Integer.parseInt(dust);

            airtext.setText(grade);
            airnum.setText(gradeNum);
            dustnum.setText(dust+"㎍/㎡");
            if(idust >= 0 && idust <= 30){
                sdust = "좋음";
                dusttext.setText(sdust);
            }else if(idust >= 31 && idust <= 80){
                sdust = "보통";
                dusttext.setText(sdust);
            }else if(idust >= 81 && idust <= 150){
                sdust = "나쁨";
                dusttext.setText(sdust);
            }else if(idust >= 151){
                sdust = "매우 나쁨";
                dusttext.setText(sdust);
            }


            if(grade.equals("좋음")){
                airimg.setImageResource(R.drawable.bigsmile);
            }else if(grade.equals("보통")){
                airimg.setImageResource(R.drawable.smallsmile);
            }else if(grade.equals("나쁨")){
                airimg.setImageResource(R.drawable.bad);
            }else if(grade.equals("매우나쁨")){
                airimg.setImageResource(R.drawable.sobad);
            }
            super.onPostExecute(document);
        }
    }
}
