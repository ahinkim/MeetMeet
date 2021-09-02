package org.techtown.meetmeetapplication;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    //서버 URL 설정
    //회원가입 요청 /process/adduser
    final static private String URL = "http://0485-182-222-218-49.ngrok.io/process/adduser";
    private Map<String, String> map;

    public RegisterRequest(String id, String password,String nickname, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("id", id);
        map.put("password", password);
        map.put("nickname",nickname);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }
}