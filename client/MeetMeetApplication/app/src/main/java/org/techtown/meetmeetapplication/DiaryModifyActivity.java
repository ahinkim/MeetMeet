package org.techtown.meetmeetapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.meetmeetapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DiaryModifyActivity extends AppCompatActivity {

    private EditText write_text;
    private SharedPreferences preferences;
    private Button done_button;
    private Button delete_button;

    private TextView year_text;
    private TextView month_text;
    private TextView date_text;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_write );

        write_text=findViewById(R.id.write_text);
        done_button=findViewById(R.id.done_button);
        delete_button=findViewById(R.id.delete_button);

        year_text=findViewById(R.id.year_text);
        month_text=findViewById(R.id.month_text);
        date_text=findViewById(R.id.date_text);

        Intent intent=getIntent();
        String modify_id=intent.getStringExtra("modify_id");
        String modify_contents=intent.getStringExtra("modify_contents");
        String modify_date=intent.getStringExtra("modify_date");

        String url="http://0485-182-222-218-49.ngrok.io/diary?id="+modify_id;

        String year=modify_date.substring(0,4);
        String month=modify_date.substring(5,7);
        String date=modify_date.substring(8,10);

        year_text.setText(year);
        date_text.setText(date);

        if(month.equals("01")){month_text.setText("JANUARY");}
        else if(month.equals("02")){month_text.setText("FEBRUARY");}
        else if(month.equals("03")){month_text.setText("MARCH");}
        else if(month.equals("04")){month_text.setText("APRIL");}
        else if(month.equals("05")){month_text.setText("MAY");}
        else if(month.equals("06")){month_text.setText("JUNE");}
        else if(month.equals("07")){month_text.setText("JULY");}
        else if(month.equals("08")){month_text.setText("AUGUST");}
        else if(month.equals("09")){month_text.setText("SEPTEMBER");}
        else if(month.equals("10")){month_text.setText("OCTOBER");}
        else if(month.equals("11")){month_text.setText("NOVEMBER");}
        else if(month.equals("12")){month_text.setText("DECEMBER");}

        write_text.setText(modify_contents);

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
                        //유효하면 access token과 함께 다이어리 수정 요청
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
                        DiaryModifyRequest diaryModifyRequest = new DiaryModifyRequest(url, headers, diary,responseListener, errorListener);
                        RequestQueue queue = Volley.newRequestQueue( DiaryModifyActivity.this );
                        queue.add( diaryModifyRequest );
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

                                    //유효하면 access token과 함께 다이어리 수정 요청
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
                                    DiaryModifyRequest diaryModifyRequest = new DiaryModifyRequest(url, headers,diary,responseListener, errorListener);
                                    RequestQueue queue = Volley.newRequestQueue( DiaryModifyActivity.this );
                                    queue.add( diaryModifyRequest );
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
                        RequestQueue queue1 = Volley.newRequestQueue(DiaryModifyActivity.this);
                        queue1.add(tokenReissueRequest);
                    }
                };
                TokenValidateRequest tokenValidateRequest = new TokenValidateRequest(headers,responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue( DiaryModifyActivity.this );
                queue.add( tokenValidateRequest );
            }

        });

        delete_button.setOnClickListener(new View.OnClickListener() {
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
                        //유효하면 access token과 함께 다이어리 수정 요청
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
                        DiaryDeleteRequest diaryDeleteRequest = new DiaryDeleteRequest(url, headers,responseListener, errorListener);
                        RequestQueue queue = Volley.newRequestQueue( DiaryModifyActivity.this );
                        queue.add( diaryDeleteRequest );
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

                                    //유효하면 access token과 함께 다이어리 수정 요청
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
                                    DiaryDeleteRequest diaryDeleteRequest = new DiaryDeleteRequest(url, headers,responseListener, errorListener);
                                    RequestQueue queue = Volley.newRequestQueue( DiaryModifyActivity.this );
                                    queue.add( diaryDeleteRequest );
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
                        RequestQueue queue1 = Volley.newRequestQueue(DiaryModifyActivity.this);
                        queue1.add(tokenReissueRequest);
                    }
                };
                TokenValidateRequest tokenValidateRequest = new TokenValidateRequest(headers,responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue( DiaryModifyActivity.this );
                queue.add( tokenValidateRequest );
            }

        });
    }
}