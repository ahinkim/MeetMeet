package org.techtown.meetmeetapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class SecessionRequest extends StringRequest {

    //서버 URL 설정
    //닉네임 수정 요청 /mypage/nickname
    static private String URL = "http://be97-182-222-218-49.ngrok.io/process/secession";
    private final Map<String, String> headers;

    public SecessionRequest(Map<String, String> headers, Response.Listener<String> listener, Response.ErrorListener errorListener ){
        super(Method.DELETE, URL, listener, errorListener);
        this.headers=headers;
    }

    @Override
    public Map<String, String>getHeaders() throws AuthFailureError {
        return headers!=null?headers:super.getHeaders();
    }

}