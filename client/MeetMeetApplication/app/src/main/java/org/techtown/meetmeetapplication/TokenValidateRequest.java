package org.techtown.meetmeetapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class TokenValidateRequest extends StringRequest {

    //서버 URL 설정
    //access token 재발급 요청 /token/access
    final static private String URL = "http://9bb4-182-222-218-49.ngrok.io/token/access";
    private final Map<String, String> headers;

    public TokenValidateRequest(Map<String, String> headers, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, URL, listener, errorListener);
        this.headers=headers;
    }

    @Override
    public Map<String, String>getHeaders(){//throws AuthFailureError
        return headers;
    }
}