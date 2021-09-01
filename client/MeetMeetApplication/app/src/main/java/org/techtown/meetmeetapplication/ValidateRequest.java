package org.techtown.meetmeetapplication;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest extends StringRequest {

    //서버 url 설정
    //아이디 중복 확인 요청 /process/validate
    final static  private String URL="http://9bb4-182-222-218-49.ngrok.io/process/validate";
    private Map<String, String> map;

    public ValidateRequest(String id, Response.Listener<String> listener){
        super(Method.POST, URL, listener,null);

        map = new HashMap<>();
        map.put("id", id);
    }

    @Override
    protected Map<String, String> getParams(){
        return map;
    }
}
