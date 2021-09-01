package org.techtown.meetmeetapplication;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DiaryMonthReqeust extends StringRequest {

    //서버 URL 설정
    //다이어리 월별 리스트 요청 /diary
    private final Map<String, String> headers;

    public DiaryMonthReqeust(String url, Map<String, String> headers, Response.Listener<String> listener, Response.ErrorListener errorListener ){
        super(Method.GET, url, listener, errorListener);
        this.headers=headers;
    }

    @Override
    public Map<String, String>getHeaders() throws AuthFailureError {
        return headers!=null?headers:super.getHeaders();
    }
}