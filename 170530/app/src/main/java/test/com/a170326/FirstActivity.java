package test.com.a170326;

import android.app.NotificationManager;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.app.PendingIntent;
import android.graphics.BitmapFactory;
import android.content.res.Resources;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FirstActivity extends AppCompatActivity {
    private Button findroute;
    private Button setting;
    private Button showmap;
    private ToggleButton mode;
    private TextView state;

    static String location;
    String check_state = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("mini", "main");
        setContentView(R.layout.activity_first);

        findroute = (Button) findViewById(R.id.findroute);
        setting = (Button) findViewById(R.id.setting);
        showmap = (Button) findViewById(R.id.map);
        mode = (ToggleButton) findViewById(R.id.bicyclemode);
        state = (TextView) findViewById(R.id.showstate);


        findroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mini","1");
                startActivity(new Intent(FirstActivity.this, MainActivity.class));
                Log.d("mini","2");
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
                RetrieveRequest req = new RetrieveRequest("dongguk-capstone", "parking_location");
                req.setReceiver(new IReceived() {
                    @Override
                    public void getResponseBody(final String msg) {
                        int start = msg.indexOf("<con>");
                        int finish = msg.indexOf("</con>");
                        location = new String(msg.substring(start+5,finish));
                        Log.d("mini", location);
                    }
                });
                req.start();
                startActivity(new Intent(FirstActivity.this, MapsActivity1.class));
            }
        });

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode.isChecked()){
                    Toast.makeText(FirstActivity.this, R.string.parking_off, Toast.LENGTH_SHORT).show();
                    ControlRequest req = new ControlRequest("dongguk-capstone", "parking_mode", "1");
                    req.start();
                    RetrieveRequestCon rew = new RetrieveRequestCon("dongguk-capstone","parking_state");
                    rew.start();
                }
                else{
                    Toast.makeText(FirstActivity.this, R.string.parking_off, Toast.LENGTH_SHORT).show();
                    ControlRequest req = new ControlRequest("dongguk-capstone", "parking_mode", "0");
                    req.start();
                }
            }
        });
    }


    public void NotificationSomethings(String message) {
        Resources res = getResources();
        Intent notificationIntent = new Intent(this, FirstActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle("Bicycle is \""+message)
                .setContentText("\" state, checkout your bicycle")
                .setTicker("Bicycle is \""+message+"\" state!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }

    //모비우스 서버의 루트 주소를 MobiusConfig.MOBIUS_ROOT_URL로 저장한다.
    public class MobiusConfig{
        public final static String MOBIUS_ROOT_URL = "http://203.253.128.151:7579/mobius-yt";
    }

    //response callback interface
    public interface IReceived{
        void getResponseBody(String msg);
    }


    // 계속해서 값을 전달받을수 있도록 만든 쓰레드이다.
    class RetrieveRequestCon extends Thread {

        private final Logger LOG = Logger.getLogger(RetrieveRequest.class.getName());

        private IReceived receiver;

        private String ae_name = ""; //change to your ae name
        private String container_name = ""; //change to your sensing data container name
        int start;
        int finish;



        public RetrieveRequestCon(String aeName, String containerName){
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
                    Log.d("mini","con = "+con);
                    if(mode.isChecked()){
                        if(con.equals("stolen")){
                            NotificationSomethings(con);
                            check_state = "stolen";
                            Log.d("mini","stolen");
                            break;
                        }
                        if(con.equals("safe") && check_state != "safe"){
                            check_state = "safe";
                            Log.d("mini","safe");
                        }
                        if(con.equals("nobeacon") && check_state != "nobeacon"){
                            check_state = "nobeacon";
                            Log.d("mini","nobeacon");
                        }
                    }
                    else {
                        Log.d("mini", "con = " + con);
                        break;
                    }
                    Thread.sleep(1000);
                    conn.disconnect();
                }
            }catch(Exception exp){
                LOG.log(Level.WARNING, exp.getMessage());
            }
        }
    }

    // container의 정보를 한 번만 받아온다.
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

    //모비우스 서버로 데이터를 보내는 스레드
    class ControlRequest extends Thread{
        private final Logger LOG = Logger.getLogger(ControlRequest.class.getName());

        private IReceived receiver;

        //두개의 변수 ae_name과 container_name 생성
        private String ae_name = "";
        private String container_name = "";

        //aename, containername, content를 묶어놓은 클래스인 ContentInstnaceObject을 이용하여 Instance변수 생성
        private ContentInstanceObject instance;

        //ControlRequest클래스의 생성자 이다. 세 개의 인자를 받고 aeName, containerName, comm
        public ControlRequest(String aeName, String containerName, String comm){

            this.ae_name = aeName;
            this.container_name = containerName;

            //instance변수를 객체생성 하고 생성자로부터 받은 세 개의 인자를 이용해 생성된 객체의 필드를 채운다.
            instance = new ContentInstanceObject();
            instance.setAeName(ae_name);
            instance.setContainerName(container_name);
            instance.setContent(comm);
        }

        public void setReceiver(IReceived hanlder){
            this.receiver = hanlder;
        }

        @Override
        public void run() {
            try{
                //stringbuilder를 사용하여 sb객체를 생성한다. sb.append()를 사용하여 string을 만든다.
                // http://203.253.128.151:7579/mobius-yt/ae_name/container_name형식으로 string을 만든다.
                StringBuilder sb = new StringBuilder();
                sb.append(MobiusConfig.MOBIUS_ROOT_URL).append("/");
                sb.append(ae_name).append("/");
                sb.append(container_name);

                //sb.tostring()으로 URL을 string으로 출력하고 이것을 URL클래스의 인자로 전달하여 mURL객체를 생성한다.
                URL mUrl = new URL(sb.toString());

                //mURL.openConnection()을 이용하여 HttpURLConnection클래스의 변수인 conn에 열어준 connection을 전달한다.
                HttpURLConnection conn = (HttpURLConnection)mUrl.openConnection();
                //값을 읽어오는 것이 아닌 값을 변경해줄 것이기 때문에 POST를 인자로 취해준다.
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setInstanceFollowRedirects(false);

                //Postman의 header에 해당되는 것들을 속성으로 추가해 준다.
                conn.setRequestProperty("Accept", "application/xml");
                conn.setRequestProperty("Content-Type", "application/vnd.onem2m-res+xml; ty=4");
                conn.setRequestProperty("X-M2M-RI", "12345");
                conn.setRequestProperty("X-M2M-Origin", "/0.2.481.1.21160310105204806");

                //ContentInstanceObject객체인 instance에서 makeBodyXML메서드를 사용하여 저장해 놓은 comm을 불러올 수 있는
                //XML코드를 만들어 reqContent에 String형식으로 저장해 준다.
                String reqContent = instance.makeBodyXML();
                conn.setRequestProperty("Content-Length", String.valueOf(reqContent.length()));

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.write(reqContent.getBytes());
                dos.flush();
                dos.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                //데이터를 저장하는곳
                String resp = "";
                //한 줄씩 읽으면서 저장하는 버퍼
                String strLine;
                //마지막줄을 읽을때 까지 수행한다.
                while((strLine = in.readLine()) != null) {
                    resp += strLine;
                }

                if(receiver != null){
                    receiver.getResponseBody(resp);
                }
                conn.disconnect();

            }catch(Exception exp){
                LOG.log(Level.SEVERE, exp.getMessage());
            }
        }
    }

    //  서버에 데이터인 aename, containername, content를 전송할 때 필요한 것들을 객체로 묶어 놓앗다.
    class ContentInstanceObject {
        private String aeName = "";
        private String containerName = "";
        private String content = "";

        //set 과 get을 이용하여 aename, containername, content을 참조하고 갱신한다.
        public void setAeName(String value){
            this.aeName = value;
        }

        public void setContainerName(String value){
            this.containerName = value;
        }

        public String getAeName(){
            return this.aeName;
        }

        public String getContainerName(){
            return this.containerName;
        }

        public void setContent(String value){
            this.content = value;
        }

        public String getContent(){
            return this.content;
        }

        public String makeBodyXML(){
            String xml = "";
            xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            xml += "<m2m:cin ";
            xml += "xmlns:m2m=\"http://www.onem2m.org/xml/protocols\" ";
            xml += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
            xml += "<cnf>text</cnf>";
            xml += "<con>" + content + "</con>";
            xml += "</m2m:cin>";
            // xml 형식에 맞추어 String 변수를 리턴해 준다.
            return xml;
        }
    }
}
