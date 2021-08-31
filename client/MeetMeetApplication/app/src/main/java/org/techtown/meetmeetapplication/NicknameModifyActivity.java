package org.techtown.meetmeetapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.meetmeetapp.R;

import java.util.HashMap;

public class NicknameModifyActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    Button nickname_button;
    EditText new_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //단말에 저장된 token 받아오기
        preferences = getSharedPreferences("UserToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String accessToken = preferences.getString("accessToken", "");
        String refreshToken = preferences.getString("refreshToken", "");

        //헤더에 토큰 넣기
        HashMap<String, String> headers = new HashMap<>();
        headers.put("access", accessToken);
        headers.put("refresh", refreshToken);

        //버튼 세팅
        setContentView(R.layout.activity_nickname);
        nickname_button=findViewById(R.id.nickname_button);
        new_nickname=findViewById(R.id.new_nickname);

        //닉네임 변경 버튼
        nickname_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = new_nickname.getText().toString();

                Response.Listener<String> responseListener= new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //유효하면 access token과 함께 닉네임 변경 요청
                        Response.Listener<String> responseListener= new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Intent intent = new Intent( NicknameModifyActivity.this, MypageActivity.class );
                                startActivity( intent );
                                finish();
                            }
                        };
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        };
                        NicknameModifyRequest nicknameModifyRequest = new NicknameModifyRequest(headers,nickname,responseListener, errorListener);
                        RequestQueue queue = Volley.newRequestQueue( NicknameModifyActivity.this );
                        queue.add( nicknameModifyRequest );
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

                                    //유효하면 access token과 함께 한달치 다이어리 요청
                                    Response.Listener<String> responseListener= new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Intent intent = new Intent( NicknameModifyActivity.this, MypageActivity.class );
                                            startActivity( intent );
                                            finish();
                                        }
                                    };
                                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                        }
                                    };
                                    NicknameModifyRequest nicknameModifyRequest = new NicknameModifyRequest(headers,nickname,responseListener, errorListener);
                                    RequestQueue queue = Volley.newRequestQueue( NicknameModifyActivity.this );
                                    queue.add( nicknameModifyRequest );

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
                        RequestQueue queue1 = Volley.newRequestQueue(NicknameModifyActivity.this);
                        queue1.add(tokenReissueRequest);
                    }
                };
                TokenValidateRequest tokenValidateRequest = new TokenValidateRequest(headers,responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue( NicknameModifyActivity.this );
                queue.add( tokenValidateRequest );
            }
        });

    }
}