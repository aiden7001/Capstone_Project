package test.com.a170326;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Heera on 2017-05-01.
 */



public class SearchLocationActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{

    private TMapData Tmapdata = new TMapData();
    private TMapGpsManager tmapgps2 = null;

    private EditText input_location;
    private String location;
    private Button location_bt;
    private Button current_loc;
    private String address;
    String start_lat;
    String start_lon;
    Intent intent_return;
    ListView listView = null;
    private GpsInfo start_dot;
    TMapView tmapview;


    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Button select_loc;
    //private TMapPoint start_dot = null;

    @Override
    public void onLocationChange(Location location) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchlocation);

        tmapgps2 = new TMapGpsManager(SearchLocationActivity.this);
        tmapgps2.setMinTime(1000);
        tmapgps2.setMinDistance(5);
        tmapgps2.setProvider(tmapgps2.NETWORK_PROVIDER);    //연결된 인터넷으로 위치 파악
        //tmapgps.setProvider(tmapgps2.GPS_PROVIDER);     //GPS로 위치 파악
        tmapgps2.OpenGps();
        //start_dot = tmapgps2.getLocation();

        final ListViewAdapter adapter;

        adapter = new ListViewAdapter();

        listView = (ListView) findViewById(R.id.listview1);
        listView.setAdapter(adapter);

        Tmapdata.findAllPOI("명동성당", new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                for (int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = poiItem.get(i);

                    Log.d("통합검색", "POI Name: " + item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "") + ", " + "Point: "
                            + item.getPOIPoint().toString());

                    //adapter.addItem(item.getPOIName(),null);

                }
            }

        });

        current_loc = (Button) findViewById(R.id.current_loc);
        current_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_dot = new GpsInfo(SearchLocationActivity.this);
                if (start_dot.isGetLocation()) {
                    start_lat = Double.toString(start_dot.getLatitude());
                    start_lon = Double.toString(start_dot.getLongitude());
                    Log.i("eee:" + start_lat, start_lon);

                    if (start_lat != "0.0" || start_lon != "0.0") {
                        Tmapdata.convertGpsToAddress(start_dot.getLatitude(), start_dot.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                            @Override
                            public void onConvertToGPSToAddress(String s) {
                                address = s;

                            }

                        });

                    }
                    if (address != null) {
                        intent_return = new Intent();
                        intent_return.putExtra("input", address);
                        Toast.makeText(SearchLocationActivity.this, address, Toast.LENGTH_SHORT).show();
                        intent_return.putExtra("lat", start_lat);
                        intent_return.putExtra("lon", start_lon);
                        setResult(1, intent_return);
                        finish();

                    }

                } else {
                    start_dot.showSettingsAlert();
                }
            }
        });


                /*start_dot = tmapgps2.getLocation();
                start_lat = Double.toString(start_dot.getLatitude());
                start_lon= Double.toString(start_dot.getLongitude());
                Log.i("eee:"+start_lat,start_lon);

                if(start_lat!="0.0" || start_lon!="0.0"){
                    Tmapdata.convertGpsToAddress(start_dot.getLatitude(), start_dot.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                        @Override
                        public void onConvertToGPSToAddress(String s) {
                            address = s;

                        }

                    });
                }
                if(address!=null){
                    intent_return = new Intent();
                    intent_return.putExtra("input",address);
                    Toast.makeText(SearchLocationActivity.this,address,Toast.LENGTH_SHORT).show();
                    intent_return.putExtra("lat",start_lat);
                    intent_return.putExtra("lon",start_lon);
                    setResult(RESULT_OK,intent_return);
                    finish();

                }

            }
        });*/

        select_loc = (Button) findViewById(R.id.select_loc);
        select_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_map = new Intent(SearchLocationActivity.this, SelectMapLocationActivity.class);
                startActivity(intent_map);
            }
        });

        input_location = (EditText)findViewById(R.id.search_location);
        /*input_location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {

                //String filterText = s.toString();
                //((ListviewAdapter)listView.getAdapter()).getFilter().filter(filterText);

            }
        });*/

        location_bt = (Button)findViewById(R.id.location_bt);
        location_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = input_location.getText().toString();

                Tmapdata.findAllPOI(location, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                        for (int i = 0; i< poiItem.size(); i++){
                            TMapPOIItem item = poiItem.get(i);

                            Log.d("주소로 찾기","POI Name: "+item.getPOIName().toString()+", "+
                                    "Address: "+item.getPOIAddress().replace("null","")+", "+ "Point: "
                                    + item.getPOIPoint().toString());

                            adapter.addItem(item.getPOIName(),item.getPOIAddress(),item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });


            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                String titleStr = item.getTitle();
                String addressStr = item.getDesc();
                String latStr = String.valueOf(item.getLat());
                String lonStr = String.valueOf(item.getLon());

                intent_return = new Intent();
                intent_return.putExtra("input", titleStr);
                //Toast.makeText(SearchLocationActivity.this, address, Toast.LENGTH_SHORT).show();
                intent_return.putExtra("lat", latStr);
                intent_return.putExtra("lon", lonStr);
                setResult(2, intent_return);
                finish();
            }
        });
    }
}