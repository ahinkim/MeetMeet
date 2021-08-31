package org.techtown.meetmeetapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DiaryMonthReqeust extends StringRequest {

    //서버 URL 설정
    //다이어리 월별 리스트 요청 /diary
    static private String URL = "http://be97-182-222-218-49.ngrok.io/diary";
    private final Map<String, String> headers;

    public DiaryMonthReqeust(Map<String, String> headers,Integer year,Integer month, Response.Listener<String> listener, Response.ErrorListener errorListener ){
        super(Method.GET, URL, listener, errorListener);
        this.headers=headers;
        URL= URL+"?year="+year+"&month="+month;
    }

    @Override
    public Map<String, String>getHeaders() throws AuthFailureError {
        return headers!=null?headers:super.getHeaders();
    }

}