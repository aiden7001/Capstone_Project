package test.com.a170326;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by ksm95 on 2017-05-07.
 */

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private TextView register;
    private TextView login2;
    final Context context = this;
    //private Button google;
    //private Button facebook;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button)findViewById(R.id.loginButton);
        register = (TextView) findViewById(R.id.registerButton);
        login2 = (TextView)findViewById(R.id.otherLogin);

        login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,FirstActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
                Log.d("mini","login");
            }
        });

        login2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "Google", "Facebook"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setTitle("로그인방법을 선택하세요");
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                                // 프로그램을 종료한다
                                Toast.makeText(getApplicationContext(),
                                        items[id] + " 선택했습니다.",
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

// 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

// 다이얼로그 보여주기
                alertDialog.show();
            }
        });

    }
}
