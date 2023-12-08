package com.example.termproject;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class DaumActivity extends AppCompatActivity {

    private WebView webView;
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum);

        // 상태 바 색상을 흰색으로 설정
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        back = findViewById(R.id.back); // '뒤로 가기' 이미지 뷰
        webView = findViewById(R.id.google); // 웹뷰 설정

        // '뒤로 가기' 이미지 버튼 클릭 시 현재 액티비티 종료
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 웹뷰 설정
        webView.setWebViewClient(new WebViewClient()); // 웹뷰에서 새 창이 뜨지 않도록 처리
        webView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 사용 가능하도록 설정
        webView.getSettings().setUseWideViewPort(true); // 화면 사이즈 맞춤
        webView.loadUrl("https://www.daum.net"); // 다음 홈페이지 로드
    }
}
