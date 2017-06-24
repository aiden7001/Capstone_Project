package test.com.a170326;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by juji6 on 2017-06-21.
 */

public class Navi extends AppCompatActivity {
    public static String left_check = "-1";
    public static String right_check = "-1";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        left_RetrieveRequestCon ret_l = new left_RetrieveRequestCon("dongguk-capstone","left_light");
        ret_l.start();
        right_RetrieveRequestCon ret_r = new right_RetrieveRequestCon("dongguk-capstone","right_light");
        ret_r.start();
        left_blink left = new left_blink();
        left.start();
        right_blink right = new right_blink();
        right.start();
    }

    public void do_left(){
        Log.d("error", "left");
        left_RetrieveRequestCon ret_l = new left_RetrieveRequestCon("dongguk-capstone","left_light");
        ret_l.start();
    }

    public void do_right(){
        Log.d("error", "right");
        right_RetrieveRequestCon ret_r = new right_RetrieveRequestCon("dongguk-capstone","right_light");
        ret_r.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView left_light = (TextView) findViewById(R.id.left_light );
            switch (msg.what) {
                case 1:     // 메시지로 넘겨받은 파라미터, 이 값으로 어떤 처리를 할지 결정
                    String str = (String)msg.obj;
                    if(str.equals("0")){
                        left_light.setBackgroundColor(Color.WHITE);
                    }
                    if(str.equals("1")){
                        left_light.setBackgroundColor(Color.YELLOW);
                    }
                    if(str.equals("2")){
                        left_light.setBackgroundColor(Color.RED);
                    }
            }
        }
    };

    Handler handlerr = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView right_light = (TextView) findViewById(R.id.right_light );
            switch (msg.what) {
                case 1:     // 메시지로 넘겨받은 파라미터, 이 값으로 어떤 처리를 할지 결정
                    String str = (String)msg.obj;
                    if(str.equals("0")){
                        right_light.setBackgroundColor(Color.WHITE);
                    }
                    if(str.equals("1")){
                        right_light.setBackgroundColor(Color.YELLOW);
                    }
                    if(str.equals("2")){
                        right_light.setBackgroundColor(Color.RED);
                    }
            }
        }
    };

    class left_blink extends Thread{
        @Override
        public void run(){
            int check = -1;
            while(true) {
                Log.d("mini","left = " + left_check);
                if (left_check.equals("0") && check != 0) {
                    handler.sendMessage(Message.obtain(handler, 1, "0"));
                    check = 0;
                }
                if (left_check.equals("1")) {
                    handler.sendMessage(Message.obtain(handler, 1, "1"));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendMessage(Message.obtain(handler, 1, "0"));
                    check = 1;
                }
                if (left_check.equals("2")) {
                    handler.sendMessage(Message.obtain(handler, 1, "2"));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendMessage(Message.obtain(handler, 1, "0"));
                    check = 2;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class right_blink extends Thread{
        @Override
        public void run(){
            int check = -1;
            while(true) {
                Log.d("mini","right = " + right_check);
                if (right_check.equals("0") && check != 0) {
                    handlerr.sendMessage(Message.obtain(handlerr, 1, "0"));
                    check = 0;
                }
                if (right_check.equals("1")) {
                    handlerr.sendMessage(Message.obtain(handlerr, 1, "1"));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handlerr.sendMessage(Message.obtain(handlerr, 1, "0"));
                    check = 1;
                }
                if (right_check.equals("2")) {
                    handlerr.sendMessage(Message.obtain(handlerr, 1, "2"));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handlerr.sendMessage(Message.obtain(handlerr, 1, "0"));
                    check = 2;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class MobiusConfig{
        public final static String MOBIUS_ROOT_URL = "http://203.253.128.151:7579/mobius-yt";
    }

    public interface IReceived{
        void getResponseBody(String msg);
    }

    class RetrieveRequest extends Thread {

        private final Logger LOG = Logger.getLogger(RetrieveRequest.class.getName());

        private IReceived receiver;

        private String ae_name = ""; //change to your ae name
        private String container_name = ""; //change to your sensing data container name

        public RetrieveRequest(String aeName, String containerName){
            this.ae_name = aeName;
            this.container_name = containerName;
        }

        public void setReceiver(IReceived hanlder){
            this.receiver = hanlder;
        }

        @Override
        public void run() {
            try{
                StringBuilder sb = new StringBuilder();
                sb.append(MobiusConfig.MOBIUS_ROOT_URL).append("/")
                        .append(ae_name).append("/")
                        .append(container_name).append("/")
                        .append("latest");

                URL mUrl = new URL(sb.toString());

                HttpURLConnection conn = (HttpURLConnection)mUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(false);

                conn.setRequestProperty("Accept", "application/xml");
                conn.setRequestProperty("X-M2M-RI", "12345");
                conn.setRequestProperty("X-M2M-Origin", "SOrigin");
                conn.setRequestProperty("nmtype", "long");

                conn.connect();

                String strResp = "";

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String strLine;
                while((strLine = in.readLine()) != null) {
                    strResp += strLine;
                }

                if(receiver != null){
                    receiver.getResponseBody(strResp);
                }
                conn.disconnect();

            }catch(Exception exp){
                LOG.log(Level.WARNING, exp.getMessage());
            }
        }
    }

    class left_RetrieveRequestCon extends Thread {

        private final Logger LOG = Logger.getLogger(RetrieveRequest.class.getName());

        private IReceived receiver;

        private String ae_name = ""; //change to your ae name
        private String container_name = ""; //change to your sensing data container name
        int start;
        int finish;

        public left_RetrieveRequestCon(String aeName, String containerName){
            this.ae_name = aeName;
            this.container_name = containerName;
        }

        public void setReceiver(IReceived hanlder){
            this.receiver = hanlder;
        }

        @Override
        public void run() {
            try{
                while(true){
                    String con;
                    int con_check = -1;
                    StringBuilder sb = new StringBuilder();
                    sb.append(MobiusConfig.MOBIUS_ROOT_URL).append("/")
                            .append("dongguk-capstone").append("/")
                            .append("left_light").append("/")
                            .append("latest");
                    URL mUrl = new URL(sb.toString());
                    HttpURLConnection conn = (HttpURLConnection)mUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(false);
                    conn.setRequestProperty("Accept", "application/xml");
                    conn.setRequestProperty("X-M2M-RI", "12345");
                    conn.setRequestProperty("X-M2M-Origin", "SOrigin");
                    conn.setRequestProperty("nmtype", "long");
                    conn.connect();
                    String strResp = "";
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String strLine;
                    while ((strLine = in.readLine()) != null) {
                        strResp += strLine;
                    }
                    // 받아온 데이터에서 원하는 값만을 뽑아올 수 있도록 한다.
                    start = strResp.indexOf("<con>");
                    finish = strResp.indexOf("</con>");
                    con = strResp.substring(start + 5, finish);
                    Log.d("mini","left_check = " + con);
                    if( con.equals("0") && con_check != 0){
                        Navi.left_check = "0";
                        con_check = 0;
                    }
                    if( con.equals("1") && con_check != 1){
                        Navi.left_check = "1";
                        con_check = 1;
                    }
                    if( con.equals("2") && con_check != 2){
                        Navi.left_check = "2";
                        con_check = 2;
                    }
                    Thread.sleep(1000);
                    conn.disconnect();
                }
            }catch(Exception exp){
                LOG.log(Level.WARNING, exp.getMessage());
                do_left();
                Log.d("error","doleft");
            }
        }
    }

    class right_RetrieveRequestCon extends Thread {

        private final Logger LOG = Logger.getLogger(RetrieveRequest.class.getName());

        private IReceived receiver;

        private String ae_name = ""; //change to your ae name
        private String container_name = ""; //change to your sensing data container name
        int start;
        int finish;

        public right_RetrieveRequestCon(String aeName, String containerName){
            this.ae_name = aeName;
            this.container_name = containerName;
        }

        public void setReceiver(IReceived hanlder){
            this.receiver = hanlder;
        }

        @Override
        public void run() {
            try{
                while(true){
                    String con;
                    int con_check = -1;
                    StringBuilder sb = new StringBuilder();
                    sb.append(MobiusConfig.MOBIUS_ROOT_URL).append("/")
                            .append(ae_name).append("/")
                            .append(container_name).append("/")
                            .append("latest");
                    URL mUrl = new URL(sb.toString());
                    HttpURLConnection conn = (HttpURLConnection)mUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(false);
                    conn.setRequestProperty("Accept", "application/xml");
                    conn.setRequestProperty("X-M2M-RI", "12345");
                    conn.setRequestProperty("X-M2M-Origin", "SOrigin");
                    conn.setRequestProperty("nmtype", "long");
                    conn.connect();
                    String strResp = "";
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String strLine;
                    while ((strLine = in.readLine()) != null) {
                        strResp += strLine;
                    }
                    // 받아온 데이터에서 원하는 값만을 뽑아올 수 있도록 한다.
                    start = strResp.indexOf("<con>");
                    finish = strResp.indexOf("</con>");
                    con = strResp.substring(start + 5, finish);
                    Log.d("mini","right_check = " + con);
                    if( con.equals("0") && con_check!=0){
                        Navi.right_check = "0";
                        con_check = 0;
                    }
                    if( con.equals("1") && con_check!=1){
                        Navi.right_check = "1";
                        con_check = 1;
                    }
                    if( con.equals("2") && con_check != 2){
                        Navi.right_check = "2";
                        con_check = 2;
                    }
                    Thread.sleep(1000);
                    conn.disconnect();
                }
            }catch(Exception exp){
                LOG.log(Level.WARNING, exp.getMessage());
                do_right();
                Log.d("error","doright");
            }
        }
    }
}
