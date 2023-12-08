package com.example.termproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SettingActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private GoogleSignInAccount gsa;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 상태 표시줄 색상 설정
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        // Firebase 인증 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        // GoogleSignInAccount 초기화
        gsa = GoogleSignIn.getLastSignedInAccount(SettingActivity.this);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // GoogleSignInClient 초기화
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // 뷰 요소 초기화
        ImageView btn = findViewById(R.id.citybtn);
        ImageView back = findViewById(R.id.back);
        TextView email_text = findViewById(R.id.email);
        TextView name_text = findViewById(R.id.name);
        TextView btn_text = findViewById(R.id.citybtntext);
        EditText city_text = findViewById(R.id.city);
        ImageView logout = findViewById(R.id.logoutbtn);

        // Intent로부터 데이터 받기
        String email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");
        String url = getIntent().getStringExtra("url");
        String city = getIntent().getStringExtra("city");

        // 이미지 로드 및 텍스트 설정
        loadImage(url);
        email_text.setText(email);
        name_text.setText(name);
        String hint = "현재 도시는" + city + "입니다.";
        city_text.setHint(hint);

        // 로그아웃 버튼 클릭 시
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();  // 로그아웃
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 액티비티 종료
                Toast.makeText(SettingActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();

            }
        });

        // 뒤로가기 버튼 클릭 시 현재 액티비티 종료
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // EditText 입력 감지를 통한 도시 설정 및 버튼 동작
        city_text.addTextChangedListener(new TextWatcher() {
            @Override
            //입력전 변경
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            //입력중 변경
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            //입력후 변경
            public void afterTextChanged(Editable editable) {
                String str = city_text.getText().toString(); // 입력된 도시명 저장
                if(str.equals("")){
                    // 입력값이 없을 경우
                    // 버튼 이미지, 텍스트 색상 변경
                    btn.setImageResource(R.drawable.box_button);
                    btn_text.setTextColor(Color.parseColor("#B5B6BD"));
                }
                else{
                    // 입력값이 있는 경우
                    // 버튼 이미지, 텍스트 색상 변경
                    btn.setImageResource(R.drawable.box_button_after);
                    btn_text.setTextColor(Color.parseColor("#ffffff"));

                    // 버튼 클릭 시 설정된 도시 정보를 이전 화면으로 전달하고 액티비티 종료
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("key", str); // 데이터 추가
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    });
                }
            }
        });
    }

    //url로 이미지를 가져와 변환 후 imageview에 적용
    private void loadImage(String imageUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imageUrl); // 받은 string을 이미지 URL 변환
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // 연결 설정
                    conn.setDoInput(true); // 입력 허용 설정
                    conn.connect(); // 연결

                    InputStream is = conn.getInputStream(); // InputStream으로 이미지 읽기
                    Bitmap bitmap = BitmapFactory.decodeStream(is); // InputStream에서 Bitmap으로 변환

                    // UI 업데이트는 runOnUiThread를 사용하여 메인 스레드에서 처리
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 이미지뷰에 비트맵 설정
                            ImageView image = findViewById(R.id.profile); // 날씨 이미지를 표시할 ImageView
                            image.setImageBitmap(bitmap); //이미지 설정
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace(); // URL 예외 처리
                } catch (IOException e) {
                    e.printStackTrace(); // IO 예외 처리
                }
            }
        }).start(); // 스레드 실행
    }

    // 사용자 로그아웃 메서드
    private void signOut() {
        mGoogleSignInClient.signOut() //GoogleSignInClient를 사용하여 사용자의 Google 계정에서 로그아웃을 시도
                .addOnCompleteListener(this, task -> { //로그아웃 작업이 완료되면, 완료 리스너(addOnCompleteListener)가 호출
                    mAuth.signOut(); //이 리스너에서는 Firebase의 인증 객체(mAuth)를 사용하여 Firebase에 대한 로그아웃을 시도
                });
        gsa = null; //GoogleSignInAccount 객체를 null로 설정하여 로그아웃된 사용자 정보를 초기화
    }
}
