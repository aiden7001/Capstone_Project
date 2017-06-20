package test.com.a170326;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static test.com.a170326.RouteActivity.httpclient;

/**
 * Created by Heera on 2017-06-20.
 */

public class NaviActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{

    private static String mApiKey = "759b5f01-999a-3cb1-a9ed-f05e2f121476";
    public String URI_RECEIVE_DISTANCE_INFO = "https://apis.skplanetx.com/tmap/routes/distance ?callback=&version=1&format=json&appKey=" + mApiKey;

    public static HttpClient httpclient;
    HttpPost httppost;
    private JSONArray countriesArray;
    private JSONArray countries;

    public static Context mContext = null;
    private boolean m_bTrackingMode = true;
    private TMapGpsManager tmapgps = null;

    TMapPoint now_point;

    String dest_lat;
    String dest_lon;
    String dest_add;
    private TextView dest_info;


    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {

            now_point = tmapgps.getLocation();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        tmapgps = new TMapGpsManager(NaviActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);    //연결된 인터넷으로 위치 파악
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);     //GPS로 위치 파악
        tmapgps.OpenGps();

        start();


        mContext = this;


        Intent naviTointent = getIntent();
        dest_lat = naviTointent.getExtras().getString("dest_lat");
        dest_lon = naviTointent.getExtras().getString("dest_lon");
        dest_add = naviTointent.getExtras().getString("dest_address");

        dest_info = (TextView) findViewById(R.id.dest_info);


        dest_info.setText(dest_add);


    }

    public void start() {
        httpclient = new DefaultHttpClient();
        /***  time out  ***/
        httpclient.getParams().setParameter("http.protocol.expect-continue", false);
        httpclient.getParams().setParameter("http.connection.timeout", 10000);
        httpclient.getParams().setParameter("http.socket.timeout", 10000);
        Log.i("psj", "heera : 00002");

    }

    public void Send_Distance_Info(String _start_x, String _start_y, String _end_x, String _end_y, String _req_coordtype) {
        Log.i("psj", "heera : 00001");
        NaviActivity.RequestRoad requestlogin = new NaviActivity.RequestRoad();
        requestlogin.execute(URI_RECEIVE_DISTANCE_INFO, _start_x, _start_y, _end_x, _end_y, _req_coordtype);

    }

    public class RequestRoad extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String uri = params[0];
            String start_x = params[1];
            String start_y = params[2];
            String end_x = params[3];
            String end_y = params[4];
            String req_coordtype = params[5];
            String result = "";
            String pro = "";
            String proo = "";
            int turn ;
            String prox = "";
            String proy = "";
            String temp = "";
            String result2 = "";


            /*** Add data to send ***/
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("startX", start_x));
            nameValuePairs.add(new BasicNameValuePair("startY", start_y));
            nameValuePairs.add(new BasicNameValuePair("endX", end_x));
            nameValuePairs.add(new BasicNameValuePair("endY", end_y));
            nameValuePairs.add(new BasicNameValuePair("reqCoordType", req_coordtype));
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
                    pro = JObject.getJSONObject("geometry").getString("type");
                    prox = JObject.getJSONObject("geometry").getJSONArray("coordinates").getString(0);
                    proy = JObject.getJSONObject("geometry").getJSONArray("coordinates").getString(1);


                    Log.i("gmlfk:",proo);

                    if(pro.equals("Point")){
                        turn = JObject.getJSONObject("properties").getInt("turnType");
                        String navidesc = JObject.getJSONObject("properties").getString("description");
                        Log.i("ghkrdls:",String.valueOf(turn));

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
            //Log.i("psj", "heera : login 00001 ttt"+result);
        }
    }
}