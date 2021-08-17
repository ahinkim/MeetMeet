package org.techtown.meetmeetapplication;

import android.app.AuthenticationRequiredException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.meetmeetapp.R;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {
    private static final String Tag="SplashActivity";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

                //헤더에 토큰 넣기
                preferences = getSharedPreferences("UserToken", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                String accessToken=preferences.getString("accessToken","");
                String refreshToken=preferences.getString("refreshToken","");

                if(accessToken==""&&refreshToken==""){
                    Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent1);
                    finish();
                }
                else{
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("access",accessToken);
                    headers.put("refresh",refreshToken);

                    Response.Listener<String> responseListener= new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                //자동 로그인 됐을 때
                                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent1);
                                finish();

                                System.out.println();
                            }catch (JSONException e1){//419error 어떻게 처리할까

                            }
                        }
                    };
                    TokenValidateRequest tokenValidateRequest = new TokenValidateRequest(headers,responseListener);
                    RequestQueue queue = Volley.newRequestQueue( SplashActivity.this );
                    queue.add( tokenValidateRequest );

                    Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response2) {
                            try {
                                JSONObject jsonObject = new JSONObject(response2);

                                //자동 로그인 됐을 때
                                String newAccessToken=jsonObject.getString("newAccessToken");

                                preferences = getSharedPreferences("UserToken", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("accessToken", newAccessToken);
                                editor.commit();

                                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent1);
                                finish();

                            }catch (JSONException e2) {
                                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent2);
                                finish();
                                e2.printStackTrace();
                            }
                        }
                    };
                    //서버로 Volley를 이용해서 요청
                    TokenReissueRequest tokenReissueRequest = new TokenReissueRequest(headers,responseListener1);
                    RequestQueue queue1 = Volley.newRequestQueue( SplashActivity.this );
                    queue1.add( tokenReissueRequest );
                }

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(Tag,"Application Running");
            }
        },2000);
    }
}
