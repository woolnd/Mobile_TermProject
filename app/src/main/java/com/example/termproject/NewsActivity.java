package com.example.termproject;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class NewsActivity extends AppCompatActivity {
    String link;
    private WebView webView;
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 해당 액티비티에 레이아웃 설정
        setContentView(R.layout.activity_news);
        // 상태 바 색상 설정
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        // MainActivity로부터 전달된 링크 가져오기
        link = getIntent().getStringExtra("link");

        // 뒤로 가기 버튼 및 웹뷰 설정
        back = findViewById(R.id.back);
        webView = findViewById(R.id.news);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        // 전달된 링크 로드
        webView.loadUrl(link);

        // 뒤로 가기 버튼 클릭 이벤트 설정
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티 종료
            }
        });
    }
}
