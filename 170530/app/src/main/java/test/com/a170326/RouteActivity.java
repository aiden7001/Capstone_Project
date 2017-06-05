package test.com.a170326;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
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

public class RouteActivity extends AppCompatActivity {

    public static Context mContext = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    private TMapData Tmapdata = new TMapData();
    TMapView tmapview = null;
    LocationManager mLM;
    public static double Ddistance;
    String Distance;
    String mProvider = LocationManager.NETWORK_PROVIDER;
    private static String mApiKey = "759b5f01-999a-3cb1-a9ed-f05e2f121476";

    private Button search;
    private Button route;
    private Button btnShowLocation;

    private GpsInfo gps;

    private EditText input_start;
    private EditText input_dest;
    TMapPoint start_point = null;
    TMapPoint dest_point = null;
    Double dest_lat = null;
    Double dest_lon = null;
    Double start_lat = null;
    Double start_lon = null;


    TMapAddressInfo addressInfoSave = new TMapAddressInfo();

    ArrayList<TMapPoint> saveRoutePoint = new ArrayList<TMapPoint>();
    ArrayList<TMapPoint> saveRouteTurnPoint = new ArrayList<TMapPoint>();
    ArrayList<Integer> saveRouteTurn = new ArrayList<Integer>();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("mini", "main");
        setContentView(R.layout.activity_showroute);

        start();

        mContext = this;

        //final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.map_view);

        //input_start = (EditText) findViewById(R.id.search_sta);
        //input_dest = (EditText) findViewById(R.id.search_dest);
        //search = (Button) findViewById(R.id.search_button);
        //route = (Button) findViewById(R.id.route);

        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        tmapview = (TMapView) findViewById(R.id.map_view);
        tmapview.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
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
        });
        tmapview.setSKPMapApiKey(mApiKey);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);


        //Log.i("hhr", String.valueOf(tmapview.getLatitude()));



        /*route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Send_Login_Info(String.valueOf(start_point.getLongitude()), String.valueOf(start_point.getLatitude()), String.valueOf(dest_point.getLongitude()), String.valueOf(dest_point.getLatitude()), "WGS84GEO", "WGS84GEO");

            }
        });*/

    }

    public void showRoute(){
        Intent intent = getIntent();
        //String id = intent.getExtras().getString("routename");
        //TMapPolyLine polyline = tmapview.getPolyLineFromID(id);
        //tmapview.removeAllMarkerItem();
        //polyline.setLineColor(Color.BLUE);
        //polyline.setLineWidth(10);
        //tMapPolyLine.setID("path");
        //Ddistance = tMapPolyLine.getDistance();
        //tmapview.addTMapPath(polyline);
        //tmapview.setTrackingMode(true);
        double start_lat= intent.getExtras().getDouble("start_lat");
        double start_lon= intent.getExtras().getDouble("start_lon");
        double dest_lat= intent.getExtras().getDouble("dest_lat");
        double dest_lon= intent.getExtras().getDouble("dest_lon");
        start_point = new TMapPoint(start_lat,start_lon);
        dest_point = new TMapPoint(dest_lat,dest_lon);

        Tmapdata.findPathData(start_point, dest_point, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                tmapview.removeAllMarkerItem();
                tMapPolyLine.setLineColor(Color.BLUE);
                tMapPolyLine.setLineWidth(10);
                tMapPolyLine.setID("path");
                Ddistance = tMapPolyLine.getDistance();
                tmapview.addTMapPath(tMapPolyLine);
                tmapview.setTrackingMode(true);
            }
        });
        Send_Login_Info(String.valueOf(start_point.getLongitude()), String.valueOf(start_point.getLatitude()), String.valueOf(dest_point.getLongitude()), String.valueOf(dest_point.getLatitude()), "WGS84GEO", "WGS84GEO");
    }

    public void Send_Login_Info(String _start_x, String _start_y, String _end_x, String _end_y, String _req_coordtype, String _res_coordtype) {
        Log.i("psj", "heera : 00001");
        RouteActivity.RequestRoad requestlogin = new RequestRoad();
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
            String prox = "";
            String proy = "";
            String temp = "";
            String result2 = "";
            String proo = "";
            Double dx=0.0;
            Double dy=0.0;
            double coorx;

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

                Log.i("ljw",String.valueOf(statusLine.getStatusCode()));
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
                    JSONObject JObject2 = countriesArray.getJSONObject(i);

                    result = JObject.getString("geometry");
                    result2 = JObject2.getString("properties");
                    proo = JObject2.getJSONObject("properties").getString("description");
                    pro = JObject.getJSONObject("geometry").getString("type");
                    prox = JObject.getJSONObject("geometry").getJSONArray("coordinates").getString(0);
                    proy = JObject.getJSONObject("geometry").getJSONArray("coordinates").getString(1);


                    try{
                        dx = Double.parseDouble(prox);
                        dy = Double.parseDouble(proy);

                        addMarker(dy,dx,proo);

                    } catch (NumberFormatException e){
                    }


                    //proo = subJObject.optString("type");
                    //coorx = ;


                    Log.i("whrcp",result);
                    Log.i("whrcp2",pro);

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

        }
    }

    private void addMarker(double lat, double lng, String title) {
        TMapMarkerItem item = new TMapMarkerItem();
        TMapPoint point = new TMapPoint(lat, lng);
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
        id++;
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

}
