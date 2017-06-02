package test.com.a170326;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity1 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        int check = 4;

        LatLng[] bicycle = new LatLng[10];
        bicycle[0] = new LatLng(37.561459, 126.995650);
        bicycle[1] = new LatLng(37.558364, 126.998153);
        bicycle[2] = new LatLng(37.557253, 126.999662);
        bicycle[3] = new LatLng(37.560999, 126.998740);

        int check_location = Integer.parseInt(FirstActivity.location);
        Log.d("mini","location = "+check_location);

        if(check_location == 39){
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(bicycle[0])
                    .title("충무로역 1번출구")
            );
            check = 0;
        }
        else{
            mMap.addMarker(new MarkerOptions()
                    .position(bicycle[0])
                    .title("충무로역 1번출구")
            );
        }

        if(check_location == 234){
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(bicycle[1])
                    .title("동국대학교 신공학관")
            );
            check = 1;
        }
        else{
            mMap.addMarker(new MarkerOptions()
                    .position(bicycle[1])
                    .title("동국대학교 신공학관")
            );
        }

        if(check_location == 2){
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(bicycle[2])
                    .title("동국대학교 상록원")
            );
            check = 2;
        }
        else{
            mMap.addMarker(new MarkerOptions()
                    .position(bicycle[2])
                    .title("동국대학교 상록원")
            );
        }

        if(check_location == 3){
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(bicycle[3])
                    .title("제일병원앞")
            );
            check = 3;
        }
        else{
            mMap.addMarker(new MarkerOptions()
                    .position(bicycle[3])
                    .title("제일병원앞")
            );
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bicycle[check], 17));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


}