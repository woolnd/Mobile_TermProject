package com.example.termproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        EditText et = findViewById(R.id.et);
        Button btn = findViewById(R.id.button);

        et.addTextChangedListener(new TextWatcher() {
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
                String str = et.getText().toString();
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
        });
    }
}
