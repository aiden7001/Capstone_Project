package test.com.a170326;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

//import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ksm95 on 2017-05-07.
 */

public class SplashActivity extends AppCompatActivity {
    //private GifImageView gifImageView;
    //private ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*gifImageView = (GifImageView)findViewById(R.id.gifImageView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(progressBar.VISIBLE);

        //Set GIFImageView resource
        try{
            InputStream inputStream = getAssets().open("giphy (2).gif");
            byte[] bytes = IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
            gifImageView.startAnimation();
        }catch (IOException ex){

        }*/

        //Wait for 3 seconds and start MainActivity
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                SplashActivity.this.startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                SplashActivity.this.finish();
            }
        },3000); //3000=3seconds
    }
}
