package org.techtown.meetmeetapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.meetmeetapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DiaryWriteActivity extends AppCompatActivity {

    private EditText write_text;
    private SharedPreferences preferences;
    private Button done_button;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_write );

        write_text=findViewById(R.id.write_text);
        done_button=findViewById(R.id.done_button);

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String diary = write_text.getText().toString();

                //헤더에 토큰 넣기
                preferences = getSharedPreferences("UserToken", MODE_PRIVATE);
                String accessToken=preferences.getString("accessToken","");
                String refreshToken=preferences.getString("refreshToken","");
                HashMap<String, String> headers = new HashMap<>();
                headers.put("access",accessToken);
                headers.put("refresh",refreshToken);

                    Response.Listener<String> responseListener= new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //유효하면 access token과 함께 다이어리 등록 요청
                            Response.Listener<String> responseListener= new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //메인 화면으로 돌아가기
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            };
                            Response.ErrorListener errorListener = new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            };
                            DiaryWriteRequest diaryWriteRequest = new DiaryWriteRequest(headers,diary,responseListener, errorListener);
                            RequestQueue queue = Volley.newRequestQueue( DiaryWriteActivity.this );
                            queue.add( diaryWriteRequest );
                        }
                    };
                    Response.ErrorListener errorListener=new Response.ErrorListener() {
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

                                        headers.put("access", newAccessToken);

                                        //유효하면 access token과 함께 다이어리 등록 요청
                                        Response.Listener<String> responseListener= new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                //메인 화면으로 돌아가기
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        };
                                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                            }
                                        };
                                        DiaryWriteRequest diaryWriteRequest = new DiaryWriteRequest(headers,diary,responseListener, errorListener);
                                        RequestQueue queue = Volley.newRequestQueue( DiaryWriteActivity.this );
                                        queue.add( diaryWriteRequest );

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Response.ErrorListener errorListener = new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //access token 재발급 실패하면 로그인으로 돌아감
                                    Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent2);
                                    finish();
                                }
                            };
                            //서버로 Volley를 이용해서 요청
                            TokenReissueRequest tokenReissueRequest = new TokenReissueRequest(headers, responseListener, errorListener);
                            RequestQueue queue1 = Volley.newRequestQueue(DiaryWriteActivity.this);
                            queue1.add(tokenReissueRequest);
                        }
                    };
                    TokenValidateRequest tokenValidateRequest = new TokenValidateRequest(headers,responseListener, errorListener);
                    RequestQueue queue = Volley.newRequestQueue( DiaryWriteActivity.this );
                    queue.add( tokenValidateRequest );
            }

        });

    }
}
