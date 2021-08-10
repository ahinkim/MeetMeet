package org.techtown.meetmeetapp;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest extends StringRequest {

    //서버 url 설정
    //아이디 중복 확인 요청
    final static  private String URL="http://7c6a2e853bcd.ngrok.io/process/validate";
    private Map<String, String> map;

    public ValidateRequest(String id, Response.Listener<String> listener){
        super(Method.POST, URL, listener,null);

        map = new HashMap<>();
        map.put("id", id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
