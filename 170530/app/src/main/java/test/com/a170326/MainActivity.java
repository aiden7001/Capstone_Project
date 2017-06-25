package test.com.a170326;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{

    public static Context mContext = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    TMapView tmapview;
    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;
    private static String mApiKey = "759b5f01-999a-3cb1-a9ed-f05e2f121476"; //Tmap API 키 설정

    private Button route;

    private EditText input_start;
    private EditText input_dest;
    TMapPoint start_point = null;
    TMapPoint dest_point = null;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    public void onLocationChange(Location location) {
        // 내 위치가 변할때마다 지도의 중심점을 내 위치로 설정
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 장소 검색을 위한 액티비티에서 값을 받아 다시 main 액티비티로 돌아온다.

        switch (requestCode){
            case 0:
                String address = data.getStringExtra("input");   // 검색한 위치의 주소
                double lat = Double.parseDouble(data.getStringExtra("lat"));    // 검색한 위치의 경도
                double  lon = Double.parseDouble(data.getStringExtra("lon"));   //  검색한 위치의 위도
                input_start.setText(address);
                start_point = new TMapPoint(lat,lon);
                break;
            case 1:
                address = data.getStringExtra("input");
                lat = Double.parseDouble(data.getStringExtra("lat"));
                lon = Double.parseDouble(data.getStringExtra("lon"));
                input_dest.setText(address);
                dest_point = new TMapPoint(lat, lon);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        tmapview = (TMapView) findViewById(R.id.map_view);

        input_start = (EditText) findViewById(R.id.search_sta);
        input_dest = (EditText) findViewById(R.id.search_dest);
        route = (Button) findViewById(R.id.route);   // 설정한 출발지와 도착지간의 경로를 받아오기 위한 버튼

        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //API 키가 확인되면 지도 설정
        tmapview.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKPMapApikeySucceed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupMap();
                    }
                });
            }

            @Override
            public void SKPMapApikeyFailed(String s) {

            }
        });
        tmapview.setSKPMapApiKey(mApiKey);

        TMapPolyLine polyLine = new TMapPolyLine();
        polyLine.setLineWidth(3);

        input_start.setOnClickListener(new View.OnClickListener() {
            // edit text 를 클릭시 장소를 검색할 수 있는 액티비티로 이동
            @Override
            public void onClick(View v) {
                Intent start_intent = new Intent(MainActivity.this, SearchLocationActivity.class);
                startActivityForResult(start_intent,0);
            }
        });

        input_dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dest_intent = new Intent(MainActivity.this, SearchLocationActivity.class);
                startActivityForResult(dest_intent,1);
            }
        });

        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //경로 찾기 버튼 클릭시 intent로 출발지, 목적지의 위도,경도,주소이름을 route activity로 넘김
                Intent intent = new Intent(mContext, RouteActivity.class);

                intent.putExtra("start_address",String.valueOf(input_start.getText()));
                Log.d("mini2", String.valueOf(input_start));
                intent.putExtra("dest_address", String.valueOf(input_dest.getText()));
                Log.d("mini2", String.valueOf(input_dest));
                intent.putExtra("start_lat",String.valueOf(start_point.getLatitude()));
                intent.putExtra("start_lon",String.valueOf(start_point.getLongitude()));
                intent.putExtra("dest_lat",String.valueOf(dest_point.getLatitude()));
                intent.putExtra("dest_lon",String.valueOf(dest_point.getLongitude()));

                mContext.startActivity(intent);

            }
        });

    }

    boolean isInitialized = false;

    //지도 초기 값 설정
    private void setupMap() {
        isInitialized = true;
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);

        tmapgps = new TMapGpsManager(MainActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);


        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);    //연결된 인터넷으로 위치 파악
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);     //GPS로 위치 파악
        tmapgps.OpenGps();
        if (cacheLocation != null) {
            moveMap(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
        }

    }

    @Override
    //액티비티 실행 시 인터넷 연결여부 확인, 위치확인
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLM.getLastKnownLocation(mProvider);
        if (location != null) {
            mListener.onLocationChanged(location);
        }
        mLM.requestSingleUpdate(mProvider, mListener, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLM.removeUpdates(mListener);
    }

    Location cacheLocation = null;

    //지도 중심 설정
    private void moveMap(double lat, double lng) {
        tmapview.setCenterPoint(lng, lat);
    }

    private void setMyLocation(double lat, double lng) {
        tmapview.setLocationPoint(lng, lat);
        tmapview.setIconVisibility(true);
    }

    //지도를 움직였을 때 지도 중심점과 내 위치 설정
    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (isInitialized) {
                moveMap(location.getLatitude(), location.getLongitude());
                setMyLocation(location.getLatitude(), location.getLongitude());
            } else {
                cacheLocation = location;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
