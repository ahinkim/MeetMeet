package org.techtown.meetmeetapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class TokenIssueRequest extends StringRequest {

    //서버 URL 설정
    //access,refresh token 발급 요청 /token/issue
    final static private String URL = "http://be97-182-222-218-49.ngrok.io/token/issue";
    private Map<String, String> map;

    public TokenIssueRequest(String id, String password, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("id", id);
        map.put("password", password);
    }

    @Override
    protected Map<String, String>getParams() {
        return map;
    }
}