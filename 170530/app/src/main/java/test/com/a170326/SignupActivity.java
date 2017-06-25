package test.com.a170326;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by ksm95 on 2017-05-09.
 */

public class SignupActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    private Button buttonsignup;
    private EditText editTextEmail;
    private EditText editTextPassword;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("mini","signup");
        setContentView(R.layout.activity_signup);

        buttonsignup = (Button)findViewById(R.id.signup);
        progressDialog = new ProgressDialog(this);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        //파이어베이스 연동
        firebaseAuth = firebaseAuth.getInstance();
        buttonsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //email과 password가 비어있는지 아닌지를 체크
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Email을 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Password를 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }

        //email과 password가 제대로 입력되어 있다면 계속 진행된다.
        progressDialog.setMessage("등록중입니다. 기다려 주세요...");
        progressDialog.show();

        //새로운 회원 생성
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } else {
                            //에러발생시
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SignupActivity.this);
                            dialog.setTitle("회원가입 오류")
                                    .setMessage("이미 등록된 회원입니다.\n새로 회원가입 하시겠습니까?")
                                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        }
                                    });
                            dialog.create();
                            dialog.show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

}