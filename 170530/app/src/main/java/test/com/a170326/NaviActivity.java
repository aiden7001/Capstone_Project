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
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

import static test.com.a170326.RouteActivity.httpclient;

/**
 * Created by Heera on 2017-06-20.
 */

public class NaviActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{

    private static String mApiKey = "759b5f01-999a-3cb1-a9ed-f05e2f121476";
    public String URI_RECEIVE_DISTANCE_INFO;

    public static HttpClient httpclient;
    HttpPost httppost;
    private JSONArray countriesArray;
    private JSONArray countries;

    public static Context mContext = null;
    private boolean m_bTrackingMode = true;
    private TMapGpsManager tmapgps = null;
    GpsInfo info_gps;

    TMapPoint now_point;

    String dest_lat;
    String dest_lon;
    String dest_add;
    String d_distance;
    private TextView dest_info;


    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {

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

        info_gps = new GpsInfo(NaviActivity.this);

        mContext = this;
        now_point = tmapgps.getLocation();

        Intent naviTointent = getIntent();
        dest_lat = naviTointent.getExtras().getString("dest_lat");
        dest_lon = naviTointent.getExtras().getString("dest_lon");
        dest_add = naviTointent.getExtras().getString("dest_address");
        dest_info = (TextView) findViewById(R.id.dest_info);

        Log.i("whkvy1",dest_lat);
        Log.i("whkvy2",dest_lon);
        Log.i("whkvy3",String.valueOf(info_gps.getLongitude()));

        dest_info.setText(dest_add);

        URI_RECEIVE_DISTANCE_INFO = "https://apis.skplanetx.com/tmap/routes/distance?startX="+String.valueOf(info_gps.getLongitude())+"&startY="+String.valueOf(info_gps.getLatitude())+"&endX="+dest_lon+"&reqCoordType=WGS84GEO&endY="+dest_lat+"&callback=&version=1&format=json&appKey=" + mApiKey;

        try
        {
            URL url = new URL(URI_RECEIVE_DISTANCE_INFO);
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
                    Log.i("error:","gmlfk");
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

}