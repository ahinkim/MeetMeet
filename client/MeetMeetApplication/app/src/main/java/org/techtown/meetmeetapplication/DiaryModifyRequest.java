package org.techtown.meetmeetapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DiaryModifyRequest extends StringRequest {

    //서버 URL 설정
    //다이어리 수정 요청 /diary
    static private String URL = "http://be97-182-222-218-49.ngrok.io/diary";
    private final Map<String, String> headers;
    private Map<String, String> map;

    public DiaryModifyRequest(Map<String, String> headers,String id, String diary, Response.Listener<String> listener, Response.ErrorListener errorListener ){
        super(Method.PUT, URL, listener, errorListener);
        this.headers=headers;
        URL= URL+"?id="+id;
        map = new HashMap<>();
        map.put("diary", diary);
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