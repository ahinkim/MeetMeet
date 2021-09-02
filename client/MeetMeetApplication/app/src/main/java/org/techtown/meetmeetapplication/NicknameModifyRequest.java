package org.techtown.meetmeetapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class NicknameModifyRequest extends StringRequest {

    //서버 URL 설정
    //닉네임 수정 요청 /mypage/nickname
    static private String URL = "http://0485-182-222-218-49.ngrok.io/mypage/nickname";
    private final Map<String, String> headers;
    private Map<String, String> map;

    public NicknameModifyRequest(Map<String, String> headers, String nickname, Response.Listener<String> listener, Response.ErrorListener errorListener ){
        super(Method.PUT, URL, listener, errorListener);
        this.headers=headers;
        map = new HashMap<>();
        map.put("nickname", nickname);
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