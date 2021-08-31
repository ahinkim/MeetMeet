package org.techtown.meetmeetapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DiaryDeleteRequest extends StringRequest {

    //서버 URL 설정
    //다이어리 삭제 요청 /diary
    static private String URL = "http://be97-182-222-218-49.ngrok.io/diary";
    private final Map<String, String> headers;
    private Map<String, String> map;

    public DiaryDeleteRequest(Map<String, String> headers, String id, Response.Listener<String> listener, Response.ErrorListener errorListener ){
        super(Method.DELETE, URL, listener, errorListener);
        this.headers=headers;
        URL= URL+"?id="+id;
        map = new HashMap<>();
    }

    @Override
    public Map<String, String>getHeaders() throws AuthFailureError {
        return headers!=null?headers:super.getHeaders();
    }
    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }

}