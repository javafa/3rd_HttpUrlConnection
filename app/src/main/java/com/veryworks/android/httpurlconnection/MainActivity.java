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
    int page = 0;

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

        setViews();
        setListener();
        setMap();
    }

    private void setViews(){
        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,datas);
        listView.setAdapter(adapter);
    }

    private void setListener(){
        // 스크롤의 상태값을 체크해주는 리스너
        listView.setOnScrollListener(scrollListener);
    }

    private void setMap(){
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
                loadPage();
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(totalItemCount <= firstVisibleItem + visibleItemCount){
                lastItemVisible = true;
            }else{
                lastItemVisible = false;
            }
        }
    };

    // 페이지를 로드
    private void loadPage(){
        nextPage();
        setUrl();
        Remote.newTask(MainActivity.this);
    }

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

        Data data = convertJson(jsonString);

        int totalCount = data.getSearchPublicToiletPOIService().getList_total_count();
        Row items[] = data.getSearchPublicToiletPOIService().getRow();

        setItemCount(totalCount);

        addDatas(items);

        addMarkers(items);

        LatLng sinsa = new LatLng(37.516066, 127.019361);
        moveMapPosition(sinsa);

        // 그리고 adapter 를 갱신해준다.
        adapter.notifyDataSetChanged();
    }

    // 지도 이동
    private void moveMapPosition(LatLng position){
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
    }

    // datas 에 데이터 더하기
    private void addDatas(Row[] items){
        for(Row item : items){
            datas.add(item.getFNAME());
        }
    }

    // 지도에 마커 생성
    private void addMarkers(Row[] items){
        for(Row row : items){
            // row를 돌면서 화장실 하나하나의 좌표를 생성한다.
            MarkerOptions marker = new MarkerOptions();
            LatLng tempCoord = new LatLng(row.getY_WGS84(), row.getX_WGS84());
            marker.position(tempCoord);
            marker.title(row.getFNAME());

            myMap.addMarker(marker);
        }
    }

    // 총개수를 화면에 출력
    private void setItemCount(int totalCount){
        textView.setText("총 개수 : "+ totalCount);
    }

    // json 스트링을 Data 오브젝트로 변환
    public Data convertJson(String jsonString){
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Data.class);
    }

    GoogleMap myMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        loadPage();
    }
}
