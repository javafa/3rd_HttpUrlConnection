package com.veryworks.android.httpurlconnection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements TaskInterface{

    /* 기초정보
        url : http://openAPI.seoul.go.kr:8088/(인증키)/xml/SearchPublicToiletPOIService/1/5/
        인증키 : 4c425976676b6f643437665377554c
     */
    static final String URL_PREFIX = "http://openAPI.seoul.go.kr:8088/";
    static final String URL_CERT   = "4c425976676b6f643437665377554c";
    static final String URL_MID    = "/json/SearchPublicToiletPOIService/";
    // 한 페이지에 불러오는 데이터 수
    static final int PAGE_OFFSET = 10;

    int pageBegin = 1;
    int pageEnd = 10;



    TextView textView;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);

        // 최초 호출시 첫번째 집합을 불러온다.
        setPage(1);

        setUrl(pageBegin, pageEnd);

        Remote.newTask(this);
    }

    private void setPage(int page){
        pageEnd = page * PAGE_OFFSET;
        pageBegin = pageEnd - PAGE_OFFSET + 1;
    }

    private void setUrl(int begin, int end){
        // String
        // StringBuffer
        // StringBuilder

        // String 연산.........
        // String result = "문자열" + "문자열" + "문자열";
        //                  ----------------
        //                    메모리공간 할당
        //                   ---------------------------
        //                         메모리공간 할당
        url = URL_PREFIX + URL_CERT + URL_MID +begin+"/"+end;
    }

    @Override
    public String getUrl(){
        return url;
    }

    @Override
    public void postExecute(String result){
        textView.setText(result);
    }
}
