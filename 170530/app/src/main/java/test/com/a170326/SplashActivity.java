package test.com.a170326;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

//import com.felipecsl.gifimageview.library.GifImageView;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksm95 on 2017-05-07.
 */

public class SplashActivity extends AppCompatActivity {
    //private GifImageView gifImageView;
    //private ProgressBar progressBar;
    private static String taasKey = "0NhsehhJAtunv%2BdlkgySNms8ZLzhBnAr1n43Cj76AQLZNNQdu5r4JAkT7pLjTD4D";

    public String URI_RECEIVE_TAAS_DATA = "http://taas.koroad.or.kr/data/rest/frequentzone/bicycle?authKey="+taasKey+"&searchYearCd=2016147&siDo=11&gugun=&DEATH=N";
    public static HttpClient httpclient;
    HttpPost httppost;
    private JSONArray countriesArray;
    private JSONArray countries;

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

        start();
        Send_Request_Info();

        //Wait for 3 seconds and start MainActivity
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                SplashActivity.this.startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                SplashActivity.this.finish();
            }
        },3000); //3000=3seconds
    }

    public void start() {
        httpclient = new DefaultHttpClient();
        /***  time out  ***/
        httpclient.getParams().setParameter("http.protocol.expect-continue", false);
        httpclient.getParams().setParameter("http.connection.timeout", 10000);
        httpclient.getParams().setParameter("http.socket.timeout", 10000);
        Log.i("psj", "heera : 00001");


    }

    public void Send_Request_Info() {
        Log.i("psj", "heera : 00001");
        RequestData requestlogin = new RequestData();
        requestlogin.execute(URI_RECEIVE_TAAS_DATA);

    }

    public class RequestData extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String uri = params[0];
            String result_lat = "";
            String result_lon = "";
            String temp = "";

            /*** Add data to send ***/
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
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
                countriesArray = root.getJSONArray("frequentzone"); // 자료 갯수

                       /* -- No data --*/
                if (countriesArray.length() < 1) {
                    return "FALSE";
                }

                  /* -- Save data --*/
                for (int i = 0; i < countriesArray.length(); i++) {

                    JSONObject JObject = countriesArray.getJSONObject(i);
                    result_lat = JObject.getString("x_crd");
                    result_lon = JObject.getString("y_crd");

                    Log.i("whrsp:",result_lat);
                    Log.i("whrsp2:",result_lon);

                    /*rsiv_message[i] = JObject.getString("message");
                    rsiv_msg_id[i] = Integer.parseInt(JObject.getString("msg_id"));
                    rsiv_like_count[i] = Integer.parseInt(JObject.getString("like_count"));
                    rsiv_like_flag[i] = Boolean.valueOf(JObject.getString("like_flag")).booleanValue();
                    rsiv_profile_pic[i]    = JObject.getString("profile_pic");
                    rsiv_uid[i]         = Integer.parseInt(JObject.getString("uid"));
                    rsiv_pic[i]         = JObject.getString("pic");
                    rsiv_comment_count[i] = Integer.parseInt(JObject.getString("comment_count"));*/

                    //messageindex++;
                    //m_arr.add(new Item(rsiv_username[i],rsiv_message[i],rsiv_msg_id[i],rsiv_like_count[i],rsiv_like_flag[i],rsiv_profile_pic[i],rsiv_uid[i],rsiv_pic[i],rsiv_comment_count[i]));
                }




            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //return "true";
            if(result_lat.equals("1"))
            {
                return "TRUE";
            }
            else{
                return "FALSE";
            }

        }

        protected void onPostExecute(String result) {

        }
    }


}
