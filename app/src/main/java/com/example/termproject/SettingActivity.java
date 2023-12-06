package com.example.termproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        mAuth = FirebaseAuth.getInstance();

        gsa = GoogleSignIn.getLastSignedInAccount(SettingActivity.this);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        ImageView btn = findViewById(R.id.citybtn);
        ImageView back = findViewById(R.id.back);
        TextView email_text = findViewById(R.id.email);
        TextView name_text = findViewById(R.id.name);
        EditText city_text = findViewById(R.id.city);
        ImageView logout = findViewById(R.id.logoutbtn);

        String email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");
        String url = getIntent().getStringExtra("url");
        String city = getIntent().getStringExtra("city");

        loadImage(url);
        email_text.setText(email);
        name_text.setText(name);
        String hint = "현재 도시는" + city + "입니다.";
        city_text.setHint(hint);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                //입력된 글자를 string으로 변환후 텍스트뷰에 추가
                String str = city_text.getText().toString();
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("key", str); // 데이터 추가
                        setResult(Activity.RESULT_OK, resultIntent);
                    }
                });
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

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    mAuth.signOut();
                    Toast.makeText(SettingActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
                    // ...
                });
        gsa = null;
    }

    /* 회원 삭제요청 */
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> {
                    // ...
                });
    }
}
