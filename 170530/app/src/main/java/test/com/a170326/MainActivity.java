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

public class MainActivity extends AppCompatActivity {

    public static Context mContext = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    private TMapData Tmapdata = new TMapData();
    TMapView tmapview=null;
    LocationManager mLM;
    public static double Ddistance;
    String Distance;
    String mProvider = LocationManager.NETWORK_PROVIDER;
    private static String mApiKey = "759b5f01-999a-3cb1-a9ed-f05e2f121476";

    private Button search;
    private Button route;
    private Button btnShowLocation;

    private GpsInfo gps;

    //double latMe = 37.5657321;
    //double lonMe = 126.9786599;
    //TMapPoint start_point = null;
    //TMapPoint marker_point = new TMapPoint(latMe,lonMe);
    //TMapPoint dest_point = null;
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

    /***
     * HTTP CLIENT
     ***/

    /*@Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 0:
                String address = data.getStringExtra("input");
                double lat = Double.parseDouble(data.getStringExtra("lat"));
                double  lon = Double.parseDouble(data.getStringExtra("lon"));
                Toast.makeText(MainActivity.this,"hr"+address,Toast.LENGTH_SHORT).show();
                //Log.i("ghkrdlss:",address);
                //Log.i("ghkrdls:",lat);
                input_start.setText(address);
                start_point = new TMapPoint(lat,lon);
                break;
            case 1:
                address = data.getStringExtra("input");
                lat = Double.parseDouble(data.getStringExtra("lat"));
                lon = Double.parseDouble(data.getStringExtra("lon"));
                Toast.makeText(MainActivity.this,"hr"+address,Toast.LENGTH_SHORT).show();
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
        Log.d("mini","main");
        setContentView(R.layout.activity_main);

        start();

        mContext = this;

        //final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.map_view);

        input_start = (EditText) findViewById(R.id.search_sta);
        input_dest = (EditText) findViewById(R.id.search_dest);
        search = (Button) findViewById(R.id.search_button);
        route = (Button) findViewById(R.id.route);

        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        tmapview = (TMapView) findViewById(R.id.map_view);
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
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);

        /*tmapview = new TMapView(this);
        linearLayout.addView(tmapview);
        tmapview.setSKPMapApiKey(mApiKey);

        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);

        tmapgps = new TMapGpsManager(MainActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);    //연결된 인터넷으로 위치 파악
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);     //GPS로 위치 파악
        tmapgps.OpenGps();

        TMapPolyLine polyLine = new TMapPolyLine();
        polyLine.setLineWidth(3);*/

        input_start.setOnClickListener(new View.OnClickListener() {
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

        /*btnShowLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gps = new GpsInfo(MainActivity.this);
                if(gps.isGetLocation()){
                    lonMe = gps.getLongitude();
                    latMe = gps.getLatitude();
                    tmapview.setCenterPoint(lonMe,latMe);
                    tmapview.setLocationPoint(lonMe,latMe);
                }else{
                    gps.showSettingsAlert();
                }
            }
        });*/

        /*tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                Toast.makeText(MainActivity.this, "클릭", Toast.LENGTH_SHORT).show();
            }
        });*/

        Log.i("hhr", String.valueOf(tmapview.getLatitude()));

        /*search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputtext = input.getText().toString();
                Log.i("ee", "ee");
                start_point = new TMapPoint(latMe,lonMe);

                Tmapdata.findAllPOI(inputtext, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem){
                        for (int i = 0; i< poiItem.size(); i++){
                            TMapPOIItem item = poiItem.get(i);

                            Log.d("주소로 찾기","POI Name: "+item.getPOIName().toString()+", "+
                                    "Address: "+item.getPOIAddress().replace("null","")+", "+ "Point: "
                                    + item.getPOIPoint().toString());


                        }
                        TMapPOIItem item = poiItem.get(0);
                        if(poiItem.isEmpty()==false){
                            for(int i=0;i<poiItem.size();i++){
                                TMapPOIItem item = POIItem.get(i);
                                tmapview.removeMarkerItem(item.getPOIID());
                            }
                            poiItem.clear();
                        }
                        navigationMode = true;

                        for(int i=0;i<saveRoutePoint.size();i++){
                            TMapMarkerItem routeMarker = new TMapMarkerItem();

                            routeMarker.setID("routeMarker"+i);
                            routeMarker.setTMapPoint(saveRoutePoint.get(i));
                            //routeMarker.latitude = saveRoutePoint.get(i).getLatitude();
                            //routeMarker.longitude = saveRoutePoint.get(i).getLongitude();
                            routeMarker.setVisible(routeMarker.VISIBLE);
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_spot);
                            routeMarker.setIcon(bitmap);
                            routeMarker.setPosition((float)0.5, (float)0.5);
                            tmapview.addMarkerItem(routeMarker.getID(), routeMarker);

                        }
                        //firstGps=0;
                        //gps1.setBackgroundDrawable((BitmapDrawable)getResources().getDrawable(R.drawable.gpsslected));
                        //gps1.setText("");

                        Log.d("주소로 찾기", "POI Name: " + item.getPOIName().toString() + ", " +
                                "Address: " + item.getPOIAddress().replace("null", "") + ", " + "Point: "
                                + item.getPOIPoint().toString());

                        search_lat = item.getPOIPoint().getLatitude();
                        search_lon = item.getPOIPoint().getLongitude();
                        Log.i(Double.toString(search_lat), Double.toString(search_lon));

                        tmapview.setTrackingMode(false);
                        tmapview.setCenterPoint(search_lon, search_lat);
                        Log.i("hr", String.valueOf(tmapview.getLatitude()));
                        tmapview.refreshMap();
                        Log.i("hhrr", String.valueOf(tmapview.getLatitude()));


                    }
                });
            }
        });*/

        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dest_point = new TMapPoint(search_lat, search_lon);

                //Log.i("dd" + start_point.toString(), dest_point.toString());
                //start_lat = start_point.getLatitude();
                //start_lon = start_point.getLongitude();
                //Log.i("hh: " + Double.toString(start_lon), Double.toString(start_lat));
                Tmapdata.findPathData(start_point, dest_point, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine tMapPolyLine) {
                        //polyLine.setLineColor(Color.BLUE);
                        //polyLine.setLineWidth(10);
                        //Ddistance = polyLine.getDistance();
                        tmapview.addTMapPath(tMapPolyLine);
                        tmapview.setTrackingMode(true);
                    }
                });
                //getJsonData(start_point, dest_point);
                Send_Login_Info(String.valueOf(start_point.getLongitude()), String.valueOf(start_point.getLatitude()), String.valueOf(dest_point.getLongitude()), String.valueOf(dest_point.getLatitude()), "WGS84GEO", "WGS84GEO");
                /*ArrayList<TMapPoint> point = new ArrayList<TMapPoint>();
                point.add(start_point);
                point.add(dest_point);
                TMapInfo info = tmapview.getDisplayTMapInfo(point);
                tmapview.setCenterPoint(info.getTMapPoint().getLongitude(),info.getTMapPoint().getLatitude());
                tmapview.setZoomLevel(info.getTMapZoomLevel());*/
            }
        });

    }

    public void Send_Login_Info(String _start_x, String _start_y, String _end_x, String _end_y, String _req_coordtype, String _res_coordtype) {
        Log.i("psj", "heera : 00001");
        RequestRoad requestlogin = new RequestRoad();
        requestlogin.execute(URI_RECEIVE_USER_ID, _start_x, _start_y, _end_x, _end_y, _req_coordtype, _res_coordtype);

    }

    /*public void getJsonData(final TMapPoint start_point, final TMapPoint dest_point) {
        Thread thread = new Thread() {
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();

                String urlString = "https://apis.skplanetx.com/tmap/routes?callback=&bizAppId=&version=1&format=json&appKey=759b5f01-999a-3cb1-a9ed-f05e2f121476";

                TMapPolyLine jsonPolyline = new TMapPolyLine();
                jsonPolyline.setLineColor(Color.RED);
                jsonPolyline.setLineWidth(2);

                try {
                    URI url = new URI(urlString);

                    HttpPost httpPost = new HttpPost();
                    httpPost.setURI(url);

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("startX", Double.toString(start_point.getLongitude())));
                    nameValuePairs.add(new BasicNameValuePair("startY", Double.toString(start_point.getLatitude())));
                    nameValuePairs.add(new BasicNameValuePair("endX", Double.toString(dest_point.getLongitude())));
                    nameValuePairs.add(new BasicNameValuePair("endY", Double.toString(dest_point.getLatitude())));
                    nameValuePairs.add(new BasicNameValuePair("reqCoordType", "WGS84GEO"));
                    nameValuePairs.add(new BasicNameValuePair("resCoordType", "WGS84GEO"));
                    //nameValuePairs.add(new BasicNameValuePair("startName", "서울역"));
                    //nameValuePairs.add(new BasicNameValuePair("endName", "공덕역"));

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);
                    String responseString = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

                    String strData = "";

                    for (int i = 0; i < saveRoutePoint.size(); i++) {
                        TMapMarkerItem routeMarker = new TMapMarkerItem();
                        routeMarker.setID("routeMarker" + i);
                        tmapview.removeMarkerItem(routeMarker.getID());
                    }

                    saveRouteTurnPoint.clear();
                    saveRoutePoint.clear();
                    saveRouteTurn.clear();

                    JSONObject jAr = new JSONObject(responseString);

                    JSONArray features = jAr.getJSONArray("features");


                    for (int i = 0; i < features.length(); i++) {

                        JSONObject JObject = features.getJSONObject(i);
                        JSONObject properties = JObject.getJSONObject("properties");

                        JSONObject geometry = JObject.getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        String geoType = geometry.getString("type");
                        if (geoType.equals("Point")) {
                            double lonJson = coordinates.getDouble(0);
                            double latJson = coordinates.getDouble(1);

                            Log.i("whrcp14", "-");
                            Log.i("whrcp15", lonJson + "," + latJson + "\n");
                            TMapPoint jsonPoint = new TMapPoint(latJson, lonJson);

                            jsonPolyline.addLinePoint(jsonPoint);
                            Log.i("whrcp16", jsonPoint.getLatitude() + "-" + jsonPoint.getLongitude());

                            int turnType = properties.getInt("turnType");
                            saveRouteTurn.add(turnType);
                            saveRouteTurnPoint.add(jsonPoint);

                        }

                        if (geoType.equals("LineString")) {
                            for (int j = 0; j < coordinates.length(); j++) {
                                JSONArray JLinePoint = coordinates.getJSONArray(j);
                                double lonJson = JLinePoint.getDouble(0);
                                double latJson = JLinePoint.getDouble(1);

                                Log.i("whrcp16", "-");
                                Log.i("whrcp17", lonJson + "," + latJson + "\n");
                                TMapPoint jsonPoint = new TMapPoint(latJson, lonJson);

                                jsonPolyline.addLinePoint(jsonPoint);
                                Log.i("whrcp18", jsonPoint.getLatitude() + "-" + jsonPoint.getLongitude());

                                saveRoutePoint.add(jsonPoint);
                            }
                        }
                    }
                    DashPathEffect dashPath = new DashPathEffect(new float[]{20, 10}, 1); //점선

                    jsonPolyline.setPathEffect(dashPath);
                    jsonPolyline.setLineColor(Color.GREEN);
                    jsonPolyline.setLineAlpha(0);
                    tmapview.addTMapPolyLine("jsonPolyline", jsonPolyline);

                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (ClientProtocolException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

        };
        thread.start();
    }*/

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



                    //received_user_name[i] = JObject.getString("description");
                    //received_user_email[i] = JObject.getString("description");
                    Log.i("whrcp",result);
                    Log.i("whrcp2",pro);
                    //Log.i("whrcp3",String.valueOf(dd));


                    /*rsiv_message[i] = JObject.getString("message");
                    rsiv_msg_id[i] = Integer.parseInt(JObject.getString("msg_id"));
                    rsiv_like_count[i] = Integer.parseInt(JObject.getString("like_count"));
                    rsiv_like_flag[i] = Boolean.valueOf(JObject.getString("like_flag")).booleanValue();
                    rsiv_profile_pic[i]    = JObject.getString("profile_pic");
                    rsiv_uid[i]         = Integer.parseInt(JObject.getString("uid"));
                    rsiv_pic[i]         = JObject.getString("pic");
                    rsiv_comment_count[i] = Integer.parseInt(JObject.getString("comment_count"));*/

                    //messageindex++;
                    //m_arr.add(new Item(rsiv_username[i],rsiv_message[i],rsiv_msg_id[i],rsiv_like_count[i],rsiv_like_flag[i],rsiv_profile_pic[i],rsiv_uid[i],rsiv_pic[i],rsiv_comment_count[i]));
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
        //        mapView.setSightVisible(true);
        //        mapView.setCompassMode(true);
        //        mapView.setTrafficInfo(true);
        //        mapView.setTrackingMode(true);
        if (cacheLocation != null) {
            moveMap(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
        }
        /*mapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                String message = null;
                switch (typeView.getCheckedRadioButtonId()){
                    case R.id.radio_start:
                        start = tMapMarkerItem.getTMapPoint();
                        message = "start";
                        break;
                    case R.id.radio_end:
                        end = tMapMarkerItem.getTMapPoint();
                        message = "end";
                        break;
                }
                Toast.makeText(MainActivity.this,message + " setting",Toast.LENGTH_SHORT).show();
            }
        });*/
    }

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


