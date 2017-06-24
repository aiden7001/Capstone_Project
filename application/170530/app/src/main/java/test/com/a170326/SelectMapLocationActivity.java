package test.com.a170326;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapOverlay;
import com.skp.Tmap.TMapOverlayItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Heera on 2017-05-02.
 */

public class SelectMapLocationActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {


    private Context mContext = null;
    private boolean m_bTrackingMode = true;
    Handler handler = new Handler();

    private TMapGpsManager tmapgps = null;
    private TMapData Tmapdata = new TMapData();
    private TMapView tmapview = null;
    private TMapPoint center_p;
    private static String mApiKey = "759b5f01-999a-3cb1-a9ed-f05e2f121476";
    private static int mMarkerID;
    private boolean m_bOverlayMode = false;
    private ArrayList<Bitmap> mOverlayList;
    private ImageView ima;
    private Bitmap bitmap;
    private TextView txt1;
    private String address;
    private Button bt1;
    Intent intent_select;

    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        win.setContentView(R.layout.activity_selectmaplocation);

        mContext = this;

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.map_view2);
        ima = (ImageView)findViewById(R.id.ima1);
        txt1 = (TextView)findViewById(R.id.txt1);
        bt1 = (Button)findViewById(R.id.bt1);


        tmapview = new TMapView(this);
        linearLayout.addView(tmapview);
        tmapview.setSKPMapApiKey(mApiKey);

        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapgps = new TMapGpsManager(SelectMapLocationActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);    //연결된 인터넷으로 위치 파악
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);     //GPS로 위치 파악
        tmapgps.OpenGps();
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(false);
        tmapview.setIconVisibility(false);
        tmapview.setCompassMode(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linear = (LinearLayout)inflater.inflate(R.layout.fixed_mark,null);

        LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
        win.addContentView(linear, paramlinear);



        tmapview.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                center_p = tmapview.getCenterPoint();
                Log.i("TOUCH:",center_p.toString());
                Tmapdata.convertGpsToAddress(center_p.getLatitude(), center_p.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                    @Override
                    public void onConvertToGPSToAddress(String s) {
                        address = s;

                    }

                });

                if(address != null){
                    txt1.setText(address);

                }
                return false;
            }



        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_select = new Intent();
                intent_select.putExtra("input", address);
                //Toast.makeText(SelectMapLocationActivity.this, address, Toast.LENGTH_SHORT).show();
                intent_select.putExtra("lat", Double.toString(center_p.getLatitude()));
                intent_select.putExtra("lon", Double.toString(center_p.getLongitude()));
                setResult(1, intent_select);
                finish();

            }
        });

    }


}
