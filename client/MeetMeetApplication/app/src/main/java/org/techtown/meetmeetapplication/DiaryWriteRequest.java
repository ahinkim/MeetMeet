package org.techtown.meetmeetapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DiaryWriteRequest extends StringRequest {

    //서버 URL 설정
    //다이어리 작성 요청 /diary
    final static private String URL = "http://80de-182-222-218-49.ngrok.io/diary";
    private final Map<String, String> headers;
    private Map<String, String> map;

    public DiaryWriteRequest(Map<String, String> headers, String diary, Response.Listener<String> listener, Response.ErrorListener errorListener ){
        super(Method.POST, URL, listener, errorListener);

        this.headers=headers;

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
