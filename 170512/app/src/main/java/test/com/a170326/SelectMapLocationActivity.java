package test.com.a170326;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapOverlay;
import com.skp.Tmap.TMapOverlayItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

/**
 * Created by Heera on 2017-05-02.
 */

public class SelectMapLocationActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {


    private Context mContext = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    private TMapData Tmapdata = new TMapData();
    private TMapView tmapview = null;
    private static String mApiKey = "759b5f01-999a-3cb1-a9ed-f05e2f121476";
    private static int mMarkerID;
    private boolean m_bOverlayMode = false;
    private ArrayList<Bitmap> mOverlayList;

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
        setContentView(R.layout.activity_selectmaplocation);

        mContext = this;

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.map_view2);
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
        tmapview.setSightVisible(true);



    }

}
