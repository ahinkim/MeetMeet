package org.techtown.meetmeetapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText join_id, join_password, join_pwck;
    private Button join_button, check_button;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_join );

        //아이디값 찾아주기
        join_id = findViewById( R.id.join_id );
        join_password = findViewById( R.id.join_password );
        join_pwck = findViewById(R.id.join_pwck);


        //아이디 중복 체크
        check_button = findViewById(R.id.check_button);
        check_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String id = join_id.getText().toString();
                if (validate) {
                    return; //검증 완료
                }

                if (id.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디를 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                join_id.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(id, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });

        //회원가입 버튼 클릭 시 수행
        join_button = findViewById( R.id.join_button );
        join_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = join_id.getText().toString();
                final String password = join_password.getText().toString();
                final String PassCk = join_pwck.getText().toString();

                //아이디 중복체크 했는지 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("중복된 아이디가 있는지 확인하세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                //한 칸이라도 입력 안했을 경우
                if (id.equals("") || password.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                //비밀번호가 다르게 입력되었을 경우
                if(!password.equals(PassCk)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("비밀번호가 동일하지 않습니다.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }
                Toast.makeText(getApplicationContext(), String.format("%s님 가입을 환영합니다.", id), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {}
                };

                //서버로 Volley를 이용해서 요청
                RegisterRequest registerRequest = new RegisterRequest( id, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue( RegisterActivity.this );
                queue.add( registerRequest );
            }
        });
    }
}
