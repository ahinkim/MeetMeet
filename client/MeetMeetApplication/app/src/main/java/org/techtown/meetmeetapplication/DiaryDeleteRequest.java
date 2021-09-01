package org.techtown.meetmeetapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DiaryDeleteRequest extends StringRequest {

    //서버 URL 설정
    //다이어리 삭제 요청 /diary
    //static private String URL = "http://9bb4-182-222-218-49.ngrok.io/diary";
    private final Map<String, String> headers;
    private Map<String, String> map;

    public DiaryDeleteRequest(String url, Map<String, String> headers, Response.Listener<String> listener, Response.ErrorListener errorListener ){
        super(Method.DELETE, url, listener, errorListener);
        this.headers=headers;
    }

    @Override
    public Map<String, String>getHeaders() throws AuthFailureError {
        return headers!=null?headers:super.getHeaders();
    }


}