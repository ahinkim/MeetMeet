package org.techtown.meetmeetapplication;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AutoLoginActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //단말에 저장된 token 받아오기
        preferences = getSharedPreferences("UserToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String accessToken = preferences.getString("accessToken", "");
        String refreshToken = preferences.getString("refreshToken", "");

        if (accessToken == "" && refreshToken == "") {
            Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent1);
            finish();
        } else {
            //헤더에 토큰 넣기
            HashMap<String, String> headers = new HashMap<>();
            headers.put("access", accessToken);
            headers.put("refresh", refreshToken);

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //access token이 유효하지 않을 때 Reissue
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                //access token 재발급 성공
                                String newAccessToken = jsonObject.getString("newAccessToken");
                                preferences = getSharedPreferences("UserToken", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("accessToken", newAccessToken);
                                editor.commit();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent2);
                            finish();
                        }
                    };
                    //서버로 Volley를 이용해서 요청
                    TokenReissueRequest tokenReissueRequest = new TokenReissueRequest(headers, responseListener, errorListener);
                    RequestQueue queue1 = Volley.newRequestQueue(AutoLoginActivity.this);
                    queue1.add(tokenReissueRequest);
                }
            };
            TokenValidateRequest tokenValidateRequest = new TokenValidateRequest(headers, responseListener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(AutoLoginActivity.this);
            queue.add(tokenValidateRequest);

            //자동 로그인 됐을 때
            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent1);
            finish();
        }
    }
}
