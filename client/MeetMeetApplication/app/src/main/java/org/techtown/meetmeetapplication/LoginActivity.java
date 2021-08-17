package org.techtown.meetmeetapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.meetmeetapp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText login_id, login_password;
    private Button login_button, join_button;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        login_id = findViewById( R.id.login_id );
        login_password = findViewById( R.id.login_password );

        join_button = findViewById( R.id.join_button );
        login_button = findViewById( R.id.login_button );

        join_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( LoginActivity.this, RegisterActivity.class );
                startActivity( intent );
            }
        });

        login_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = login_id.getText().toString();
                String password = login_password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            if(success) {//로그인 성공시

                                Toast.makeText( getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT ).show();
                                Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                                startActivity( intent );

                            } else {//로그인 실패시
                                Toast.makeText( getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT ).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest( id, password, responseListener );
                RequestQueue queue = Volley.newRequestQueue( LoginActivity.this );
                queue.add( loginRequest );

                Response.Listener<String> responseListener2 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String accessToken=jsonObject.getString("accessToken");
                            String refreshToken=jsonObject.getString("refreshToken");
                            //getSharedPreferences("파일이름",'모드')
                            //모드 => 0 (읽기,쓰기가능)
                            //모드 => MODE_PRIVATE (이 앱에서만 사용가능)
                            preferences = getSharedPreferences("UserToken", MODE_PRIVATE);

                            //Editor를 preferences에 쓰겠다고 연결
                            SharedPreferences.Editor editor = preferences.edit();

                            //putString(KEY,VALUE)
                            editor.putString("accessToken", accessToken);
                            editor.putString("refreshToken", refreshToken);

                            //항상 commit & apply 를 해주어야 저장이 된다.
                            editor.commit();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //서버로 Volley를 이용해서 요청
                TokenIssueRequest tokenIssueRequest = new TokenIssueRequest( id, password, responseListener2);
                RequestQueue queue2 = Volley.newRequestQueue( LoginActivity.this );
                queue2.add( tokenIssueRequest );
            }
        });
    }
}