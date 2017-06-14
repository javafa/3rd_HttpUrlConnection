package com.veryworks.android.httpurlconnection;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.veryworks.android.httpurlconnection.domain.Data;
import com.veryworks.android.httpurlconnection.domain.Row;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements TaskInterface, OnMapReadyCallback{

    /* 기초정보
        url : http://openAPI.seoul.go.kr:8088/4c425976676b6f643437665377554c/json/SearchPublicToiletPOIService/1/5/
        인증키 : 4c425976676b6f643437665377554c
     */
    static final String URL_PREFIX = "http://openAPI.seoul.go.kr:8088/";
    static final String URL_CERT   = "4c425976676b6f643437665377554c";
    static final String URL_MID    = "/json/SearchPublicToiletPOIService/";
    // 한 페이지에 불러오는 데이터 수
    static final int PAGE_OFFSET = 10;
    int page = 1;

    ListView listView;
    TextView textView;
    String url = "";

    // 아답터
    ArrayAdapter<String> adapter;

    // 아답터에서 사용할 데이터 공간
    final List<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textView);

        // 데이터 - 위에서 공간 할당 됨
        // 아답터
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,datas);
        listView.setAdapter(adapter);

        // 스크롤의 상태값을 체크해주는 리스너
        listView.setOnScrollListener(scrollListener);

        // 맵을 세팅
        FragmentManager manager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) manager.findFragmentById(R.id.mapView);
        // 로드되면 onReady 호출하도록
        mapFragment.getMapAsync(this);
    }

    // 리스트의 마지막 아이템이 보이는지 여부
    boolean lastItemVisible = false;
    // 스크롤 리스너
    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                    && lastItemVisible){
                nextPage();
                setUrl();
                Remote.newTask(MainActivity.this);
            }
        }
        // firstVisibleItem = 현재 보여지는 첫번째 아이템의 번호
        // visibleItemCount = 현재 화면에 보여지는 아이템의 개수
        // totalItemCount   = 리스트에 담겨있는 전체 아이템의 개수
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if(totalItemCount <= firstVisibleItem + visibleItemCount){
                lastItemVisible = true;
            }else{
                lastItemVisible = false;
            }
        }
    };

    private void nextPage(){
        page = page + 1;
    }

    private void setUrl(){
        int end = page * PAGE_OFFSET;
        int begin = end - PAGE_OFFSET + 1;

        url = URL_PREFIX + URL_CERT + URL_MID +begin+"/"+end;
    }

    @Override
    public String getUrl(){
        return url;
    }

    @Override
    public void postExecute(String jsonString){
        Gson gson = new Gson();
        // 1. json String -> class 로 변환
        Data data = gson.fromJson(jsonString, Data.class);

        // 총개수를 화면에 세팅
        textView.setText("총 개수 : "+data.getSearchPublicToiletPOIService().getList_total_count());
        // 건물의 이름을 listView 에 세팅

        Row rows[] = data.getSearchPublicToiletPOIService().getRow();

        // 네트웍에서 가져온 데이터를 꺼내서 datas에 담아준다.
        for(Row row : rows){
            datas.add(row.getFNAME());

            // row를 돌면서 화장실 하나하나의 좌표를 생성한다.
            MarkerOptions marker = new MarkerOptions();
            LatLng tempCoord = new LatLng(row.getY_WGS84(), row.getX_WGS84());
            marker.position(tempCoord);
            marker.title(row.getFNAME());

            myMap.addMarker(marker);
        }

        // 지도 컨트롤
        LatLng sinsa = new LatLng(37.516066, 127.019361);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sinsa, 10));

        // 그리고 adapter 를 갱신해준다.
        adapter.notifyDataSetChanged();
    }

    GoogleMap myMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        // 최초 호출시 첫번째 집합을 불러온다.
        setUrl();
        Remote.newTask(this);
    }
}
