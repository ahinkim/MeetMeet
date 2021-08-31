package org.techtown.meetmeetapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.meetmeetapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    Button month_button;
    Button year_button;
    Button mypage_button;
    ImageButton write_button;

    public Calendar cal = Calendar.getInstance();

    RecyclerView recyclerView;
    NoteAdapter adapter;

    Context context;

    ArrayList<NoteItem> mList=new ArrayList<NoteItem>();

    DatePickerDialog.OnDateSetListener monthlistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            //선택한 월로 버튼의 글씨 바뀜
            if(monthOfYear==1){month_button.setText("JANUARY");}
            else if(monthOfYear==2){month_button.setText("FEBRUARY");}
            else if(monthOfYear==3){month_button.setText("MARCH");}
            else if(monthOfYear==4){month_button.setText("APRIL");}
            else if(monthOfYear==5){month_button.setText("MAY");}
            else if(monthOfYear==6){month_button.setText("JUNE");}
            else if(monthOfYear==7){month_button.setText("JULY");}
            else if(monthOfYear==8){month_button.setText("AUGUST");}
            else if(monthOfYear==9){month_button.setText("SEPTEMBER");}
            else if(monthOfYear==10){month_button.setText("OCTOBER");}
            else if(monthOfYear==11){month_button.setText("NOVEMBER");}
            else if(monthOfYear==12){month_button.setText("DECEMBER");}
        }
    };
    DatePickerDialog.OnDateSetListener yearlistener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            //선택한 연도로 버튼 바뀜
            year_button.setText(""+year);
        }
    };
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
        setContentView(R.layout.activity_main);
        month_button = findViewById(R.id.month_button);
        year_button=findViewById(R.id.year_button);
        write_button=findViewById(R.id.write_button);
        mypage_button=findViewById(R.id.mypage_button);

        //현재 연도 세팅
        int year = cal.get(Calendar.YEAR);
        year_button.setText(""+year);

        //현재 달 세팅
        int month=cal.get(Calendar.MONTH)+1;
        if(month==1){month_button.setText("JANUARY");}
        else if(month==2){month_button.setText("FEBRUARY");}
        else if(month==3){month_button.setText("MARCH");}
        else if(month==4){month_button.setText("APRIL");}
        else if(month==5){month_button.setText("MAY");}
        else if(month==6){month_button.setText("JUNE");}
        else if(month==7){month_button.setText("JULY");}
        else if(month==8){month_button.setText("AUGUST");}
        else if(month==9){month_button.setText("SEPTEMBER");}
        else if(month==10){month_button.setText("OCTOBER");}
        else if(month==11){month_button.setText("NOVEMBER");}
        else if(month==12){month_button.setText("DECEMBER");}

        //Recycler view 세팅
        recyclerView=findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NoteAdapter() ;

        //일기 데이터 넣어놓기
        //adapter.addItem(new NoteItem(0,"", "첫번째 일기입니다.","8월 28일"));
        //adapter.addItem(new NoteItem(1,"", "두번째 일기입니다.","8월 29일"));

        /*
        //한달치 일기 받아오기
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //유효하면 access token과 함께 한달치 다이어리 요청
                Response.Listener<String> responseListener= new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //access token 유효하면 diaries jsonArray 받아오기
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray diariesArray=jsonObject.optJSONArray("diaries");
                            JSONObject element;
                            for(int i=0;i<diariesArray.length();i++){
                                element=(JSONObject) diariesArray.opt(i);
                                adapter.addItem(new NoteItem(element.optString("_id")
                                        ,element.optString("diary")
                                        ,element.optString("created_At")));
                            }

                        }catch (JSONException e){

                            e.printStackTrace();
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                };
                String yearString=year_button.getText().toString();
                Integer year=Integer.parseInt(yearString);
                String monthString=month_button.getText().toString();
                Integer month=0;
                if(monthString=="JANUARY"){month=1;}
                else if(monthString=="FEBRUARY"){month=2;}
                else if(monthString=="MARCH"){month=3;}
                else if(monthString=="APRIL"){month=4;}
                else if(monthString=="MAY"){month=5;}
                else if(monthString=="JUNE"){month=6;}
                else if(monthString=="JULY"){month=7;}
                else if(monthString=="AUGUST"){month=8;}
                else if(monthString=="SEPTEMBER"){month=9;}
                else if(monthString=="OCTOBER"){month=10;}
                else if(monthString=="NOVEMBER"){month=11;}
                else if(monthString=="DECEMBER"){month=12;}

                DiaryMonthReqeust diaryMonthReqeust = new DiaryMonthReqeust(headers,year,month,responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue( MainActivity.this );
                queue.add( diaryMonthReqeust );
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
                                    try {
                                        //access token 유효하면 diaries jsonArray 받아오기
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray diariesArray=jsonObject.optJSONArray("diaries");
                                        JSONObject element;
                                        for(int i=0;i<diariesArray.length();i++){
                                            element=(JSONObject) diariesArray.opt(i);
                                            adapter.addItem(new NoteItem(element.optString("_id")
                                                    ,element.optString("diary")
                                                    ,element.optString("created_At")));
                                            Toast.makeText(getApplicationContext(),element.optString("diary"),Toast.LENGTH_LONG).show();
                                        }

                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Response.ErrorListener errorListener = new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            };
                            String yearString=year_button.getText().toString();
                            Integer year=Integer.parseInt(yearString);
                            String monthString=month_button.getText().toString();
                            Integer month=0;
                            if(monthString=="JANUARY"){month=1;}
                            else if(monthString=="FEBRUARY"){month=2;}
                            else if(monthString=="MARCH"){month=3;}
                            else if(monthString=="APRIL"){month=4;}
                            else if(monthString=="MAY"){month=5;}
                            else if(monthString=="JUNE"){month=6;}
                            else if(monthString=="JULY"){month=7;}
                            else if(monthString=="AUGUST"){month=8;}
                            else if(monthString=="SEPTEMBER"){month=9;}
                            else if(monthString=="OCTOBER"){month=10;}
                            else if(monthString=="NOVEMBER"){month=11;}
                            else if(monthString=="DECEMBER"){month=12;}

                            DiaryMonthReqeust diaryMonthReqeust = new DiaryMonthReqeust(headers,year,month,responseListener, errorListener);
                            RequestQueue queue = Volley.newRequestQueue( MainActivity.this );
                            queue.add( diaryMonthReqeust );

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
                RequestQueue queue1 = Volley.newRequestQueue(MainActivity.this);
                queue1.add(tokenReissueRequest);
            }
        };
        TokenValidateRequest tokenValidateRequest = new TokenValidateRequest(headers,responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue( MainActivity.this );
        queue.add( tokenValidateRequest );
        */

        recyclerView.setAdapter(adapter) ;

        //일기 클릭했을 때
        adapter.setOnItemClickListener(new OnNoteItemClickListener() {
            @Override
            public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position) {
                NoteItem item=adapter.getItem(position);
                Toast.makeText(getApplicationContext(),"아이템 선택됨: "+item.getContents(),Toast.LENGTH_LONG).show();
            }
        });

        //월 선택 버튼
        month_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MonthPickerDialog monthPickerDialog = new MonthPickerDialog();
                monthPickerDialog.setListener(monthlistener);
                monthPickerDialog.show(getSupportFragmentManager(), "YearMonthPickerTest");
            }
        });
        //연도 선택 버튼
        year_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                YearPickerDialog yearPickerDialog = new YearPickerDialog();
                yearPickerDialog.setListener(yearlistener);
                yearPickerDialog.show(getSupportFragmentManager(), "YearMonthPickerTest");
            }
        });
        //일기 쓰기 버튼
        write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, DiaryWriteActivity.class );
                startActivity( intent );
                finish();
            }
        });
        mypage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, MypageActivity.class );
                startActivity( intent );
                finish();
            }
        });
    }
}