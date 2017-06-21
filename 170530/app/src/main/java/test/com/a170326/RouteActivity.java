package test.com.a170326;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.Transaction;
import com.skp.Tmap.TMapAddressInfo;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapOverlayItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

public class RouteActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{

    public static Context mContext = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    private TMapData Tmapdata = new TMapData();
    TMapView tmapview = null;
    LocationManager mLM;
    String Distance;
    String mProvider = LocationManager.NETWORK_PROVIDER;
    private static String mApiKey = "759b5f01-999a-3cb1-a9ed-f05e2f121476";

    private Button search;
    private Button route;
    private Button btnShowLocation;
    private Button guide;
    private Button handle;
    ListView listView;

    private GpsInfo gps;

    String input_start;
    String input_dest;
    private TextView routename;
    TMapPoint start_point = null;
    TMapPoint dest_point = null;
    String dest_lat;
    String dest_lon;
    String start_lat;
    String start_lon;
    String mdes;
    int mturn;
    String start_add;
    String dest_add;

    Double Ddistance;

    Intent intentTonavi;

    TMapAddressInfo addressInfoSave = new TMapAddressInfo();

    ArrayList<String> saveDescription = new ArrayList<String>();
    ArrayList<String> saveTurn = new ArrayList<String>();
    ArrayList<MapPoint> saveRoutePoint = new ArrayList<MapPoint>();
    ArrayList<TMapPoint> saveRouteTurnPoint = new ArrayList<TMapPoint>();
    ArrayList<TMapPoint> saveRouteTurn = new ArrayList<TMapPoint>();
    //private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public String received_user_name[] = new String[100];
    public String received_user_email[] = new String[100];

    /***
     * HTTP CLIENT
     ***/
    public String URI_RECEIVE_USER_ID = "https://apis.skplanetx.com/tmap/routes?callback=&version=1&format=json&appKey=" + mApiKey;
    public static HttpClient httpclient;
    HttpPost httppost;
    private JSONArray countriesArray;
    private JSONArray countries;

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroute);
        Log.d("mini", "main");

        listView = (ListView)findViewById(R.id.navilist);
        guide = (Button)findViewById(R.id.guide);
        intentTonavi = new Intent(RouteActivity.this, NaviActivity.class);


        start();

        handle = (Button) findViewById(R.id.handle);

        mContext = this;
        Intent intent = getIntent();
        start_lat= intent.getExtras().getString("start_lat");
        start_lon= intent.getExtras().getString("start_lon");
        dest_lat= intent.getExtras().getString("dest_lat");
        dest_lon= intent.getExtras().getString("dest_lon");
        start_point = new TMapPoint(Double.parseDouble(start_lat),Double.parseDouble(start_lon));
        dest_point = new TMapPoint(Double.parseDouble(dest_lat),Double.parseDouble(dest_lon));

        routename = (TextView) findViewById(R.id.routeshow);

        start_add = intent.getExtras().getString("start_address");
        Log.d("mini",start_add);
        dest_add = intent.getExtras().getString("dest_address");
        Log.d("mini",dest_add);

        routename.setText(start_add+"->"+dest_add);

        //final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.map_view);

        //input_start = (EditText) findViewById(R.id.search_sta);
        //input_dest = (EditText) findViewById(R.id.search_dest);
        //search = (Button) findViewById(R.id.search_button);
        //route = (Button) findViewById(R.id.route);

        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        tmapview = (TMapView) findViewById(R.id.map_view2);
        guide = (Button) findViewById(R.id.guide);
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //intent.putExtra("start_address",start_add);
                //Log.d("mini2", String.valueOf(input_start));
                //intentTonavi.putExtra("totalTime",ttime);
                //intent.putExtra("dest_lat",String.valueOf(dest_point.getLatitude()));
                //intent.putExtra("dest_lon",String.valueOf(dest_point.getLongitude()));

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        intentTonavi.putExtra("dest_address", dest_add);
                        //Log.d("mini2", String.valueOf(input_dest));
                        intentTonavi.putExtra("dest_lat",dest_lat);
                        intentTonavi.putExtra("dest_lon",dest_lon);
                        intentTonavi.putExtra("totalDistance",Ddistance);

                        intentTonavi.putExtra("list",saveRoutePoint);
                        intentTonavi.putExtra("turn_list",saveTurn);
                        intentTonavi.putExtra("desc_list",saveDescription);
                        mContext.startActivity(intentTonavi);
                    }
                },250);
            }
        });
        /*tmapview.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKPMapApikeySucceed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showRoute();
                    }
                });
            }

            @Override
            public void SKPMapApikeyFailed(String s) {

            }
        });*/
        tmapview.setSKPMapApiKey(mApiKey);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);

        tmapgps = new TMapGpsManager(RouteActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);    //연결된 인터넷으로 위치 파악
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);     //GPS로 위치 파악
        tmapgps.OpenGps();


        //Log.i("hhr", String.valueOf(tmapview.getLatitude()));

        showRoute();

        /*final Handler handler = new Handler(Looper.getMainLooper()){
            public void handleMessage(Message msg){
                setnavilist();
            }
        };*/


        //Log.i("tkdlwm:",String.valueOf(size));
        //Log.i("ghkrdls:",saveDescription.get(0));

        /*route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Send_Login_Info(String.valueOf(start_point.getLongitude()), String.valueOf(start_point.getLatitude()), String.valueOf(dest_point.getLongitude()), String.valueOf(dest_point.getLatitude()), "WGS84GEO", "WGS84GEO");

            }
        });*/

    }

    public void showRoute(){
        //Intent intent = getIntent();
        //String id = intent.getExtras().getString("routename");
        //TMapPolyLine polyline = tmapview.getPolyLineFromID(id);
        //tmapview.removeAllMarkerItem();
        //polyline.setLineColor(Color.BLUE);
        //polyline.setLineWidth(10);
        //tMapPolyLine.setID("path");
        //Ddistance = tMapPolyLine.getDistance();
        //tmapview.addTMapPath(polyline);
        //tmapview.setTrackingMode(true);

        Tmapdata.findPathData(start_point, dest_point, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                tmapview.removeAllMarkerItem();
                tMapPolyLine.setLineColor(Color.BLUE);
                tMapPolyLine.setLineWidth(10);
                tMapPolyLine.setID("path");
                Ddistance = tMapPolyLine.getDistance();
                //saveRouteTurn = tMapPolyLine.getPassPoint();
                Log.d("minig", String.valueOf(Ddistance));
                tmapview.addTMapPath(tMapPolyLine);
                tmapview.setTrackingMode(true);
            }
        });

        Send_Login_Info(String.valueOf(start_point.getLongitude()), String.valueOf(start_point.getLatitude()), String.valueOf(dest_point.getLongitude()), String.valueOf(dest_point.getLatitude()), "WGS84GEO", "WGS84GEO");

    }

    public void Send_Login_Info(String _start_x, String _start_y, String _end_x, String _end_y, String _req_coordtype, String _res_coordtype) {
        Log.i("psj", "heera : 00001");
        RequestRoad requestlogin = new RequestRoad();
        requestlogin.execute(URI_RECEIVE_USER_ID, _start_x, _start_y, _end_x, _end_y, _req_coordtype, _res_coordtype);

    }

    public class RequestRoad extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String uri = params[0];
            String start_x = params[1];
            String start_y = params[2];
            String end_x = params[3];
            String end_y = params[4];
            String req_coordtype = params[5];
            String res_coordtype = params[6];
            String result = "";
            String pro = "";
            String proo = "";
            int turn ;
            String prox = "";
            String proy = "";
            String temp = "";
            String result2 = "";
            Double dx=0.0;
            Double dy=0.0;
            double coorx;
            Double time = 0.0;
            Double distance = 0.0;


            /*** Add data to send ***/
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("startX", start_x));
            nameValuePairs.add(new BasicNameValuePair("startY", start_y));
            nameValuePairs.add(new BasicNameValuePair("endX", end_x));
            nameValuePairs.add(new BasicNameValuePair("endY", end_y));
            nameValuePairs.add(new BasicNameValuePair("reqCoordType", req_coordtype));
            nameValuePairs.add(new BasicNameValuePair("resCoordType", res_coordtype));
            StringBuilder builder = new StringBuilder();

            /*** Send post message ***/
            httppost = new HttpPost(uri);
            try {
                UrlEncodedFormEntity urlendoeformentity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
                httppost.setEntity(urlendoeformentity);
                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();


                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    String line;
                    int j = 0;

                    while ((line = reader.readLine()) != null) {
                        Log.i("psj", "server result " + line);
                        if (j > 0) {
                            temp = temp + "\n";
                        }
                        temp = temp + line;
                        j++;
                    }
                    Log.i("psj", "server result " + temp);
                    builder.append(temp);

                }


            } catch (Exception e) {
                Log.i("psj", "Exception try1:" + e.getStackTrace());
                e.printStackTrace();
            }

             /* -- Treat JSON data to string array  --*/
            try {
                JSONObject root = new JSONObject(builder.toString());
                countriesArray = root.getJSONArray("features");
                //countries = root.getJSONArray("coordinates");// 자료 갯수

                       /* -- No data --*/

                if (countriesArray.length() < 1) {
                    return "FALSE";
                }


                  /* -- Save data --*/
                for (int i = 0; i < countriesArray.length(); i++) {

                    JSONObject JObject = countriesArray.getJSONObject(i);

                    result = JObject.getString("geometry");
                    result2 = JObject.getString("properties");
                    proo = JObject.getJSONObject("properties").getString("description");
                    //time = JObject.getJSONObject("properties").getDouble("totalTime");
                    //distance = JObject.getJSONObject("properties").getDouble("totalDistance");
                    pro = JObject.getJSONObject("geometry").getString("type");
                    prox = JObject.getJSONObject("geometry").getJSONArray("coordinates").getString(0);
                    proy = JObject.getJSONObject("geometry").getJSONArray("coordinates").getString(1);


                    Log.i("gmlfk:",proo);



                    try{
                        dx = Double.parseDouble(prox);
                        dy = Double.parseDouble(proy);

                        //saveRoutePoint.add(new TMapPoint(dy,dx));
                        addMarker(dy,dx,proo);
                        showMarker();
                        //ttime = time;
                        //tdistance = distance;
                        //Intent intent = new Intent(mContext, NaviActivity.class);
                        //intent.putExtra("totalTime", time);
                        //intent.putExtra("totalDistance",distance);

                        //mContext.startActivity(intent);
                        //Log.d("minig", String.valueOf(ttime));
                        //Log.d("minig", String.valueOf(tdistance));

                    } catch (NumberFormatException e){

                    }

                    if(pro.equals("Point")){
                        turn = JObject.getJSONObject("properties").getInt("turnType");
                        String navidesc = JObject.getJSONObject("properties").getString("description");
                        Log.i("ghkrdls:",String.valueOf(turn));

                        saveArray(turn, navidesc);

                    }


                    //proo = subJObject.optString("type");
                    //coorx = ;

                    //Log.i("whrcp3:",String.valueOf(i)+proo);
                    //Log.i("whrcp3:",String.valueOf(i)+turn);

                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //return "true";
            if(result.equals("1"))
            {
                return "TRUE";
            }
            else{
                return "FALSE";
            }

        }

        protected void onPostExecute(String result) {
            Log.i("psj", "heera : login 00001 ttt"+result);

            NaviListViewAdapter adapter;

            adapter = new NaviListViewAdapter();

            listView = (ListView)findViewById(R.id.navilist);
            listView.setAdapter(adapter);
            adapter.clearItem();

            for(int i=0; i<saveTurn.size(); i++){
                Log.i("xjs:",String.valueOf(saveTurn.get(i)));
                if (saveTurn.get(i).equals("11")){
                    adapter.addItem(ContextCompat.getDrawable(RouteActivity.mContext,R.drawable.upward), saveDescription.get(i));
                }
                else if(saveTurn.get(i).equals("12")){
                    adapter.addItem(ContextCompat.getDrawable(RouteActivity.mContext,R.drawable.back), saveDescription.get(i));
                }
                else if(saveTurn.get(i).equals("13")){
                    adapter.addItem(ContextCompat.getDrawable(RouteActivity.mContext,R.drawable.forward), saveDescription.get(i));
                }
                else {
                    adapter.addItem(ContextCompat.getDrawable(RouteActivity.mContext,R.drawable.forward), saveDescription.get(i));
                }

            }



        }
    }

    private void addMarker(double lat, double lng, String title) {
        saveRoutePoint.add(new MapPoint(title,lat,lng));
        /*TMapMarkerItem item = new TMapMarkerItem();
        TMapPoint point = new TMapPoint(lat, lng);

        //Log.d("mini", String.valueOf(saveRoutePoint.get(1)));
        item.setTMapPoint(point);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_add_marker);
        //item.setIcon(bitmap);
        item.setPosition(0.5f, 1);
        item.setCalloutTitle(title);
        //item.setCalloutSubTitle("sub " + title);
        //Bitmap left = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_alert)).getBitmap();
        //item.setCalloutLeftImage(left);
        //Bitmap right = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_get)).getBitmap();
        //item.setCalloutRightButtonImage(right);
        item.setCanShowCallout(true);
        tmapview.addMarkerItem("m" + id, item);
        id++;*/
    }

    public void showMarker() {
        for(int i=0;i<saveRoutePoint.size();i++){
            TMapPoint point = new TMapPoint(saveRoutePoint.get(i).getLatitude(),saveRoutePoint.get(i).getLongitude());
            Log.d("mini", String.valueOf(saveRoutePoint.get(i).getLatitude()));
            Log.d("mini", String.valueOf(saveRoutePoint.get(i).getLongitude()));
            Log.d("mini", String.valueOf(saveRoutePoint.get(i)));
            TMapMarkerItem item1 = new TMapMarkerItem();
            Bitmap bitmap = null;
            item1.setTMapPoint(point);
            //item1.setName(saveRoutePoint.get(i).getName());
            item1.setVisible(item1.VISIBLE);
            item1.setCalloutTitle(saveRoutePoint.get(i).getName());
            item1.setCanShowCallout(true);
            tmapview.addMarkerItem("m" + id, item1);
            id++;

        }
    }

    int id = 0;

    boolean isInitialized = false;

    /*private void setupMap() {
        isInitialized = true;
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);

        tmapgps = new TMapGpsManager(RouteActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);    //연결된 인터넷으로 위치 파악
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);     //GPS로 위치 파악
        tmapgps.OpenGps();
        //        mapView.setSightVisible(true);
        //        mapView.setCompassMode(true);
        //        mapView.setTrafficInfo(true);
        //        mapView.setTrackingMode(true);
        if (cacheLocation != null) {
            moveMap(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
        }

    }*/
    public void start() {
        httpclient = new DefaultHttpClient();
        /***  time out  ***/
        httpclient.getParams().setParameter("http.protocol.expect-continue", false);
        httpclient.getParams().setParameter("http.connection.timeout", 10000);
        httpclient.getParams().setParameter("http.socket.timeout", 10000);
        Log.i("psj", "heera : 00002");

    }


    @Override
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

    private void moveMap(double lat, double lng) {
        tmapview.setCenterPoint(lng, lat);
    }

    private void setMyLocation(double lat, double lng) {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_place);
        //tmapview.setIcon(bitmap);
        tmapview.setLocationPoint(lng, lat);
        tmapview.setIconVisibility(true);
    }

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


    public void saveArray(int turn, String navidesc){
        mdes = navidesc;
        mturn = turn;
        saveTurn.add(String.valueOf(mturn));
        saveDescription.add(mdes);
    }

}


