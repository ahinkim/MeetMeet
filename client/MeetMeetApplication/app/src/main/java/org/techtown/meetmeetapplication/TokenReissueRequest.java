package org.techtown.meetmeetapplication;

import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class TokenReissueRequest extends StringRequest {

    //서버 URL 설정
    //access,refresh token 재발급 요청 /token/reissuance
    final static private String URL = "http://0f1c92958392.ngrok.io/token/reissuance";
    private final Map<String, String> headers;

    public TokenReissueRequest(Map<String, String> headers, Response.Listener<String> listener) {
        super(Method.GET, URL, listener, null);
        this.headers=headers;
    }

    @Override
    public Map<String, String>getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
}