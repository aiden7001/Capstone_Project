package test.com.a170326;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

//import com.felipecsl.gifimageview.library.GifImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.io.IOUtils;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksm95 on 2017-05-07.
 */

public class SplashActivity extends AppCompatActivity {
    //private GifImageView gifImageView;
    //private ProgressBar progressBar;
    private static String taasKey = "0NhsehhJAtunv%2BdlkgySNms8ZLzhBnAr1n43Cj76AQLZNNQdu5r4JAkT7pLjTD4D";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private int count = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream buf = null;
        try {
            //[URL 지정과 접속]

            //웹서버 URL 지정
            url= new URL("http://taas.koroad.or.kr/data/rest/frequentzone/bicycle?authKey="+taasKey+"&searchYearCd=2016147&siDo=11&gugun=&DEATH=N");

            //URL 접속
            urlConnection = (HttpURLConnection) url.openConnection();

            //[웹문서 소스를 버퍼에 저장]
            //데이터를 버퍼에 기록

            //buf = new BufferedInputStream(urlConnection.getInputStream());
            //BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf,"UTF-8"));

            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
            //Log.d("line:",bufreader.toString());

            String line = null;
            String page = "";

            //버퍼의 웹문서 소스를 줄단위로 읽어(line), Page에 저장함
            while((line = bufreader.readLine())!=null){
                //Log.d("line:",line);
                page+=line;
            }

            //읽어들인 JSON포맷의 데이터를 JSON객체로 변환
            JSONObject json = new JSONObject(page);
            JSONObject json2 = json.getJSONObject("searchResult");

            //ksk_list 에 해당하는 배열을 할당
            JSONArray jArr = json2.getJSONArray("frequentzone");

            //배열의 크기만큼 반복하면서, name과 address의 값을 추출함
            for (int i=0; i<jArr.length(); i++){

                //i번째 배열 할당
                JSONObject json3 = jArr.getJSONObject(i);

                //ksNo,korName의 값을 추출함
                String taas_lon = json3.getString("x_crd");
                String taas_lat = json3.getString("y_crd");

                //ksNo,korName의 값을 출력함
                Log.i("ksno:",taas_lon);
                Log.i("korname:",taas_lat);

            }

        } catch (Exception e) {
            e.printStackTrace();

        }finally{
            //URL 연결 해제
            urlConnection.disconnect();
        }

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
