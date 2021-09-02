package org.techtown.meetmeetapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.meetmeetapp.R;

import java.util.HashMap;

public class MypageActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    Button logout_button;
    Button withdraw_button;
    Button nickname_button;
    ImageButton exit_button;

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
        setContentView(R.layout.activity_mypage);
        logout_button=findViewById(R.id.logout_button);
        withdraw_button=findViewById(R.id.withdraw_button);
        nickname_button=findViewById(R.id.nickname_button);
        exit_button=findViewById(R.id.exit_button);

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //나가면 메인 페이지만 있음
                finish();
            }
        });

        nickname_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, NicknameModifyActivity.class);
                startActivity(intent);
                //finish 안했기 때문에 닉네임에서 뒤로가기 누르면 메인페이지로 마이페이지로 돌아감
            }
        });

        //로그아웃 버튼
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener= new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //유효하면 access token과 함께 로그아웃 요청
                        Response.Listener<String> responseListener= new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        };
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        };
                        LogoutRequest logoutRequest = new LogoutRequest(headers,responseListener, errorListener);
                        RequestQueue queue = Volley.newRequestQueue( MypageActivity.this );
                        queue.add( logoutRequest );
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

                                    //유효하면 access token과 함께 로그아웃 요청
                                    Response.Listener<String> responseListener= new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                        }
                                    };
                                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                        }
                                    };
                                    LogoutRequest logoutRequest = new LogoutRequest(headers,responseListener, errorListener);
                                    RequestQueue queue = Volley.newRequestQueue( MypageActivity.this );
                                    queue.add( logoutRequest );
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
                        RequestQueue queue1 = Volley.newRequestQueue(MypageActivity.this);
                        queue1.add(tokenReissueRequest);
                    }
                };
                TokenValidateRequest tokenValidateRequest = new TokenValidateRequest(headers,responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue( MypageActivity.this );
                queue.add( tokenValidateRequest );

                Intent intent = new Intent( MypageActivity.this, LoginActivity.class );
                startActivity( intent );
                finish();
            }
        });

        //회원탈퇴 버튼
        withdraw_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener= new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Response.Listener<String> responseListener= new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        };
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        };
                        SecessionRequest secessionRequest = new SecessionRequest(headers,responseListener, errorListener);
                        RequestQueue queue = Volley.newRequestQueue( MypageActivity.this );
                        queue.add( secessionRequest );
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

                                    Response.Listener<String> responseListener= new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                        }
                                    };
                                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                        }
                                    };
                                    SecessionRequest secessionRequest = new SecessionRequest(headers,responseListener, errorListener);
                                    RequestQueue queue = Volley.newRequestQueue( MypageActivity.this );
                                    queue.add( secessionRequest );

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
                        RequestQueue queue1 = Volley.newRequestQueue(MypageActivity.this);
                        queue1.add(tokenReissueRequest);
                    }
                };
                TokenValidateRequest tokenValidateRequest = new TokenValidateRequest(headers,responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue( MypageActivity.this );
                queue.add( tokenValidateRequest );

                Intent intent = new Intent( MypageActivity.this, LoginActivity.class );
                startActivity( intent );
                finish();
            }
        });

    }
}
