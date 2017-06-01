package test.com.a170326;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

/**
 * Created by ksm95 on 2017-06-01.
 */

public class FirstActivity extends AppCompatActivity {
    private Button findroute;
    private Button setting;
    private Button showmap;
    private ToggleButton mode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("mini", "main");
        setContentView(R.layout.activity_first);

        findroute = (Button) findViewById(R.id.findroute);
        setting = (Button) findViewById(R.id.setting);
        showmap = (Button) findViewById(R.id.map);
        mode = (ToggleButton) findViewById(R.id.bicyclemode);

        findroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstActivity.this, MainActivity.class));
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        showmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstActivity.this, MapActivity.class));
            }
        });
    }
}
