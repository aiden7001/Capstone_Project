package test.com.a170326;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.w3c.dom.Text;
import org.xml.sax.helpers.LocatorImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static test.com.a170326.RouteActivity.httpclient;

/**
 * Created by Heera on 2017-06-20.
 */

public class NaviActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TMapGpsManager.onLocationChangedCallback {

    private static String mApiKey = "759b5f01-999a-3cb1-a9ed-f05e2f121476";
    public String URI_RECEIVE_DISTANCE_INFO;

    public static HttpClient httpclient;
    HttpPost httppost;
    private JSONArray countriesArray;
    private JSONArray countries;
    ArrayList<MapPoint> list;
    ArrayList<String> turn_list;
    ArrayList<String> desc_list;
    ArrayList<Double> line_list;

    public static Context mContext = null;
    private boolean m_bTrackingMode = true;
    private TMapGpsManager tmapgps = null;
    GpsInfo info_gps;

    String dest_lat;
    String dest_lon;
    String sta_lat;
    String sta_lon;
    String dest_add;
    String d_distance;
    private TextView dest_info;
    private TextView showtime;
    private TextView showdistance;
    private TextView marker_distance;
    private TextToSpeech myTTS;
    private TextView currenttime;
    private ImageView arrow;
    //private TextView currentspeed;

    Double time;
    Double distance;
    Double fdistance;
    Double ddistance = 0.0;
    int hour,minute;
    int km,m;
    int point_index = 0;
    int entrance = 0;
    int compare;
    Double speed;
    double myspeed;
    String lefttime;
    String leftdistance;

    long now = System.currentTimeMillis();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLocationChange(Location location) {
        sta_lon = String.valueOf(info_gps.getLongitude());
        sta_lat = String.valueOf(info_gps.getLatitude());

        URI_RECEIVE_DISTANCE_INFO = make_url(String.valueOf(info_gps.getLongitude()), String.valueOf(info_gps.getLatitude()),String.valueOf(list.get(point_index).getLongitude()),String.valueOf(list.get(point_index).getLongitude()));
        try{
            compare = Integer.parseInt(url_connetion(URI_RECEIVE_DISTANCE_INFO));
        } catch (NumberFormatException e){
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        Log.i("compare:",String.valueOf(compare));
        fdistance = distance - (line_list.get(point_index) - compare) - ddistance;
        //leftdistance = String.valueOf(fdistance);

        marker_distance.setText(String.valueOf(compare));

        if(entrance == 0){

            if(compare<=10){
                myTTS.speak(desc_list.get(point_index), TextToSpeech.QUEUE_ADD, null);
                if (turn_list.get(point_index).equals("11")){
                    arrow.setImageDrawable(ContextCompat.getDrawable(NaviActivity.mContext,R.drawable.upward));
                }
                else if(turn_list.get(point_index).equals("12")){
                    arrow.setImageDrawable(ContextCompat.getDrawable(NaviActivity.mContext,R.drawable.back));
                }
                else if(turn_list.get(point_index).equals("13")){
                    arrow.setImageDrawable(ContextCompat.getDrawable(NaviActivity.mContext,R.drawable.forward));
                }
                else {
                    arrow.setImageDrawable(ContextCompat.getDrawable(NaviActivity.mContext,R.drawable.forward));
                }

            }

            entrance = 1;

        }

        if (compare <= 1){

            for(int i=0; i<(point_index-1); i++){
                ddistance = ddistance+line_list.get(i);
            }
            point_index++;
            entrance = 0;

        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        myTTS = new TextToSpeech(this, this);
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        String formatDate = sdfNow.format(date);
        arrow = (ImageView)findViewById(R.id.arrow);
        marker_distance = (TextView)findViewById(R.id.direct_info);

        tmapgps = new TMapGpsManager(NaviActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);    //연결된 인터넷으로 위치 파악
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);     //GPS로 위치 파악
        tmapgps.OpenGps();

        info_gps = new GpsInfo(NaviActivity.this);

        mContext = this;

        Intent naviTointent = getIntent();
        dest_lat = naviTointent.getExtras().getString("dest_lat");
        dest_lon = naviTointent.getExtras().getString("dest_lon");
        dest_add = naviTointent.getExtras().getString("dest_address");
        list = (ArrayList<MapPoint>) getIntent().getSerializableExtra("list");
        turn_list = (ArrayList<String>) getIntent().getSerializableExtra("turn_list");
        desc_list = (ArrayList<String>) getIntent().getSerializableExtra("desc_list");
        line_list = (ArrayList<Double>) getIntent().getSerializableExtra("line_list");

        Log.i("tkdlwm:",String.valueOf(list.size()));
        Log.i("tkdlwm2:",String.valueOf(turn_list.size()));
        Log.i("tkdlwm3:",String.valueOf(desc_list.size()));
        Log.i("tkdlwm4:",String.valueOf(line_list.size()));

        //time = naviTointent.getExtras().getDouble("totalTime");
        distance = naviTointent.getExtras().getDouble("totalDistance");
        fdistance = distance;
        Log.d("minig", String.valueOf(distance));
        speed = 15000.0/3600.0;
        time = fdistance/speed;
        hour = (int)(Math.round(time)/3600.0);
        minute = (int)(Math.round(time)%3600.0/60.0);
        km = (int)(Math.round(fdistance)/1000.0);
        m = (int)(Math.round(fdistance)%1000.0/100.0);
        myspeed = 0;
        //Toast.makeText(NaviActivity.this, (int) myspeed,Toast.LENGTH_LONG).show();

        dest_info = (TextView) findViewById(R.id.dest_info);
        showtime = (TextView) findViewById(R.id.totaltime);
        showdistance = (TextView) findViewById(R.id.totaldistance);
        currenttime = (TextView) findViewById(R.id.time);
        //currentspeed = (TextView) findViewById(R.id.velocity);

        if(hour==0)
            lefttime = minute + "분";
        else
            lefttime = hour + "시간 " + minute + "분";
        if(km==0)
            leftdistance = m*100 + "m";
        else
            leftdistance = km + "." + m + "km";

        Log.i("whkvy1",dest_lat);
        Log.i("whkvy2",dest_lon);
        Log.i("whkvy3",String.valueOf(info_gps.getLongitude()));

        dest_info.setText(dest_add);

        //URI_RECEIVE_DISTANCE_INFO = "https://apis.skplanetx.com/tmap/routes/distance?startX="+String.valueOf(info_gps.getLongitude())+"&startY="+String.valueOf(info_gps.getLatitude())+"&endX="+dest_lon+"&reqCoordType=WGS84GEO&endY="+dest_lat+"&callback=&version=1&format=json&appKey=" + mApiKey;

        showtime.setText(lefttime);
        showdistance.setText(leftdistance);
        currenttime.setText(formatDate);
        //currentspeed.setText((int) myspeed);
        Log.d("minig", String.valueOf(hour));
        Log.d("minig", String.valueOf(minute));
        Log.d("minig", String.valueOf(km));
        Log.d("minig", String.valueOf(m));

        //URI_RECEIVE_DISTANCE_INFO = make_url(String.valueOf(info_gps.getLongitude()), String.valueOf(info_gps.getLatitude()),dest_lon,dest_lat);
        //url_connetion(URI_RECEIVE_DISTANCE_INFO);

    }

    public String make_url(String start_lon, String start_lat, String destination_lon, String destination_lat){
        String url = "https://apis.skplanetx.com/tmap/routes/distance?startX="+start_lon+"&startY="+start_lat+"&endX="+destination_lon+"&reqCoordType=WGS84GEO&endY="+destination_lat+"&callback=&version=1&format=json&appKey=" + mApiKey;

        return url;
    }

    public String url_connetion(String input_url){

        try
        {
            URL url = new URL(input_url);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection)urlConnection;

            int responseCode = httpUrlConnection.getResponseCode();

            Log.i("tkdxo:",String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                InputStream inputStream = httpUrlConnection.getInputStream();
                String str = convertStreamToString(inputStream);


                try{
                    JSONObject root = new JSONObject(str);
                    Log.i("strr1:",root.getString("distanceInfo"));
                    Log.i("strr2:",root.getJSONObject("distanceInfo").getString("distance"));
                    d_distance = root.getJSONObject("distanceInfo").getString("distance");

                }catch (JSONException e){
                    Log.e("error", "JSONException");
                }
                Log.i("strr:", str);
            }
        } catch (MalformedURLException e) {
            Log.e("error", "url fault");
            e.printStackTrace();

        } catch (IOException e) {
            Log.e("error", "I/O exception");
            e.printStackTrace();
        }

        return d_distance;

    }

    public static String convertStreamToString(InputStream inputStream) {

        if (inputStream != null)
        {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];

            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                int n;

                while ((n=reader.read(buffer))!=-1) {
                    writer.write(buffer, 0, n);

                }

                return writer.toString();

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
        }

        return null;

    }

    @Override
    public void onInit(int status) {
        String myText1 = "길 안내를 시작합니다.";
        String myText2 = "";
        String myText3 = "";
        String myText4 = "안전운전 하세요.";
        if(hour==0)
            myText3 = "총 소요시간은 " + minute + "분 입니다.";
        else
            myText3 = "총 소요시간은 " + hour + "시간" + minute + "분 입니다.";
        if(km==0)
            myText2 = "목적지까지의 거리는 " + m*100 + "미터";
        else
            myText2 = "목적지까지의 거리는 " + km + "점" + m + "킬로미터";
        myTTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
        myTTS.speak(myText2, TextToSpeech.QUEUE_ADD, null);
        myTTS.speak(myText3, TextToSpeech.QUEUE_ADD, null);
        myTTS.speak(myText4, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}