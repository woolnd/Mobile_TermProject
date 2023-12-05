package com.example.termproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    // 구글 API 클라이언트
    private GoogleSignInClient mGoogleSignInClient;

    // 구글 계정
    private GoogleSignInAccount gsa;

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth mAuth;

    // 구글 로그인 버튼
    private SignInButton btnGoogleLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 상태 바 색상 설정
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        // Firebase 인증 객체 초기화
        mAuth = FirebaseAuth.getInstance();

        // Google 로그인을 앱에 통합
        // GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions); // 구글 로그인 클라이언트 설정
        btnGoogleLogin = findViewById(R.id.login);  // 로그인 버튼
        btnGoogleLogin.setOnClickListener(view -> {
            signIn(); // 로그인 메서드 호출
        });
    }

    // 구글 로그인 메서드
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // GoogleSignInClient.getSignInIntent(...)에서 반환된 결과 처리
        if (requestCode == RC_SIGN_IN) {
            // 이 호출로부터 반환된 Task는 항상 완료됨. 리스너를 추가할 필요 없음
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task); // 로그인 결과 처리
        }
    }

    // 사용자 정보 가져오기
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            String personName = acct.getDisplayName(); // 사용자 이름 가져오기
            String personEmail = acct.getEmail(); // 사용자 이메일 가져오기
            Uri personPhoto = acct.getPhotoUrl(); // 사용자 사진 URL 가져오기

            if (acct != null) {
                firebaseAuthWithGoogle(acct.getIdToken()); // Google 인증 토큰으로 Firebase 인증 진행

                Log.d(TAG, "handleSignInResult:personName " + personName); // 이름 로그 기록
                Log.d(TAG, "handleSignInResult:personEmail " + personEmail); // 이메일 로그 기록
                Log.d(TAG, "handleSignInResult:personPhoto " + personPhoto); // 사진 URL 로그 기록

                String photoUrlString = (personPhoto != null) ? personPhoto.toString() : null;

                // MainActivity로 정보 전송하여 화면 전환
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("name", personName); // 이름 전달
                intent.putExtra("email", personEmail); // 이메일 전달
                intent.putExtra("url", photoUrlString); // 프로필 사진 URL 전달
                startActivity(intent); // MainActivity 시작
            }
        } catch (ApiException e) {
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode()); // 로그인 실패 시 에러 코드 로그
        }
    }

    // GoogleAuthProvider를 사용하여 Firebase에 인증 요청
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 인증 성공 시 UI 업데이트
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        // 인증 실패 시 메시지 표시
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
