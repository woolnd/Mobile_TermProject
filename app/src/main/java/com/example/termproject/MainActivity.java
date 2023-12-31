package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // 기본 API 키 및 도시 설정
    String api = "097176059f7a6fe3934a296004c7abcf";
    String city = "Ulsan";
    TextView city_view, temp_view, feel_view, wind_view, humidity_view, cloud_view, description_view;
    private static final int REQUEST_CODE = 1; //SettingActivity로 전환할 때 사용할 요청 코드
    private TabLayout tabLayout;

    private FloatingActionButton fab_main, fab_mypage, fab_search, fab_refresh, fab_google, fab_naver, fab_daum;
    private Animation fab_open, fab_close, fab_stay, fab_clear;
    private boolean isFabOpen = false;
    private boolean isSearch = false;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        //플로팅 메세지 애니메이션
        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);
        fab_stay = AnimationUtils.loadAnimation(mContext, R.anim.fab_stay);
        fab_clear = AnimationUtils.loadAnimation(mContext, R.anim.fab_close_all);

        //플로팅 버튼 초기화
        fab_main = findViewById(R.id.fab_main);
        fab_mypage = findViewById(R.id.fab_mypage);
        fab_search = findViewById(R.id.fab_search);
        fab_refresh = findViewById(R.id.fab_refresh);
        fab_google = findViewById(R.id.fab_google);
        fab_naver = findViewById(R.id.fab_naver);
        fab_daum = findViewById(R.id.fab_daum);

        //플로팅 버튼 초기 설정
        fab_mypage.startAnimation(fab_close);
        fab_mypage.setVisibility(View.GONE);

        fab_search.startAnimation(fab_close);
        fab_search.setVisibility(View.GONE);

        fab_refresh.startAnimation(fab_close);
        fab_refresh.setVisibility(View.GONE);

        fab_google.startAnimation(fab_close);
        fab_google.setVisibility(View.GONE);

        fab_naver.startAnimation(fab_close);
        fab_naver.setVisibility(View.GONE);

        fab_daum.startAnimation(fab_close);
        fab_daum.setVisibility(View.GONE);


        //클릭 시 toggleFab()함수
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFab();
            }
        });

        //클릭 시 인텐트에 정보(이메일, 이름, 프로필 url, 도시)를 담아 액티비티 전환
        fab_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                String email = getIntent().getStringExtra("email");
                String name = getIntent().getStringExtra("name");
                String url = getIntent().getStringExtra("url");
                intent.putExtra("email", email);
                intent.putExtra("name", name);
                intent.putExtra("url", url);
                intent.putExtra("city", city);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //클릭 시 toggleSearch()함수 실행
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSearch();
            }
        });

        //클릭 시 구글페이지로 액티비티 전환
        fab_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GoogleActivity.class);
                startActivity(intent);
            }
        });

        //클릭 시 네이버페이지로 액티비티 전환
        fab_naver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NaverActivity.class);
                startActivity(intent);
            }
        });

        //클릭 시 다음페이지로 액티비티 전환
        fab_daum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DaumActivity.class);
                startActivity(intent);
            }
        });

        //클릭 시 새로고침을 함
        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent(); //새로운 인텐트로 현재 액티비티 재 이동
                startActivity(intent);
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Toast.makeText(MainActivity.this, "새로고침", Toast.LENGTH_SHORT).show();
            }
        });


        //ViewPager2와 TabLayout을 함께 사용하여 스와이프 가능한 탭 기능을 구현
        PagerAdapter viewPager2Adapter
                = new PagerAdapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 viewPager2 = findViewById(R.id.vp);
        viewPager2.setAdapter(viewPager2Adapter);

        //=== TabLayout기능 추가 부분 ============================================
        tabLayout = findViewById(R.id.tablayout);
        //TabLayoutMediator를 사용하여 TabLayout과 ViewPager2를 연결
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            // 탭의 위치(position)에 따라 각 탭에 표시될 텍스트를 지정
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("오늘의 뉴스");
                        break;
                    case 1:
                        tab.setText("어제의 뉴스");
                        break;
                    // 필요한 만큼 case 추가
                }
            }
        }).attach();

        //loadWeatherInfo함수 실행
        loadWeatherInfo(city);


        // 각종 TextView와 ImageView 초기화
        city_view = findViewById(R.id.title);
        temp_view = findViewById(R.id.temp);
        feel_view = findViewById(R.id.feel);
        wind_view = findViewById(R.id.wind);
        humidity_view = findViewById(R.id.humidity);
        cloud_view = findViewById(R.id.cloud);
        description_view = findViewById(R.id.description);

        TextView date_view = findViewById(R.id.date);
        TextView week_view = findViewById(R.id.week);
        ConstraintLayout top = findViewById(R.id.top);

        //최상단에 표시할 도시 작성
        String title = city + "의 날씨";
        city_view.setText(title);

        //getCurrentDate()함수 반환값으로 날짜 작성
        date_view.setText(getCurrentDate());

        //getCurrentWeek()함수 반환값은 1: 일요일 2:월요일..
        //숫자 값으로 요일 작성
        switch (getCurrentWeek()) {
            case 1:
                week_view.setText("일요일");
                break;
            case 2:
                week_view.setText("월요일");
                break;
            case 3:
                week_view.setText("화요일");
                break;
            case 4:
                week_view.setText("수요일");
                break;
            case 5:
                week_view.setText("목요일");
                break;
            case 6:
                week_view.setText("금요일");
                break;
            case 7:
                week_view.setText("토요일");
                break;
        }

        //getCurrentTime()함수로 현재 시간 반환
        String currentTime = getCurrentTime();
        //반환 값을 int로 변경
        int time = Integer.parseInt(currentTime);

        // 시간대에 따라 배경 이미지 및 글자 색상 변경
        //9시부터 18시전에는 낮에 대한 이미지와 파란 글자색으로 변경
        if (time < 18 && time >= 9) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.afternoon));
            top.setBackgroundResource(R.drawable.afternoon);
            description_view.setTextColor(Color.parseColor("#99ccff"));
            date_view.setTextColor(Color.parseColor("#99ccff"));
            week_view.setTextColor(Color.parseColor("#99ccff"));
            temp_view.setTextColor(Color.parseColor("#99ccff"));
            feel_view.setTextColor(Color.parseColor("#99ccff"));
            wind_view.setTextColor(Color.parseColor("#99ccff"));
            humidity_view.setTextColor(Color.parseColor("#99ccff"));
            cloud_view.setTextColor(Color.parseColor("#99ccff"));

            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#5AA6FF"));

        }
        //21시부터 06시전에는 밤에 대한 이미지와 남색 글자색 변경
        else if (time >= 21 || time < 6) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.night));
            top.setBackgroundResource(R.drawable.night);
            description_view.setTextColor(Color.parseColor("#000080"));
            date_view.setTextColor(Color.parseColor("#000080"));
            week_view.setTextColor(Color.parseColor("#000080"));
            temp_view.setTextColor(Color.parseColor("#000080"));
            feel_view.setTextColor(Color.parseColor("#000080"));
            wind_view.setTextColor(Color.parseColor("#000080"));
            humidity_view.setTextColor(Color.parseColor("#000080"));
            cloud_view.setTextColor(Color.parseColor("#000080"));

            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#6A6AFF"));
        }
        //나머지 시간대는 새벽, 초저녁에 대한 이미지와 주황 글자색 변경
        else {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dawn));
            top.setBackgroundResource(R.drawable.dawn);
            description_view.setTextColor(Color.parseColor("#FF9500"));
            date_view.setTextColor(Color.parseColor("#FF9500"));
            week_view.setTextColor(Color.parseColor("#FF9500"));
            temp_view.setTextColor(Color.parseColor("#FF9500"));
            feel_view.setTextColor(Color.parseColor("#FF9500"));
            wind_view.setTextColor(Color.parseColor("#FF9500"));
            humidity_view.setTextColor(Color.parseColor("#FF9500"));
            cloud_view.setTextColor(Color.parseColor("#FF9500"));

            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF9F5A"));
        }
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
                            ImageView image = findViewById(R.id.weather); // 날씨 이미지를 표시할 ImageView
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

    // 현재 날짜를 가져오는 메서드
    public static String getCurrentDate() {
        Date dateNow = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("M월 dd일", Locale.getDefault());  // 2월 15일
        // 현재 날짜를 M월 dd일 형식으로 반환
        return format.format(dateNow);
    }

    // 현재 요일을 가져오는 메서드
    public static int getCurrentWeek() {
        Date currentDate = new Date(); // 현재 날짜를 가져옴

        Calendar calendar = Calendar.getInstance(); // 캘린더 객체 생성
        calendar.setTime(currentDate); // 캘린더에 현재 날짜 설정

        int dayOfWeekNumber = calendar.get(Calendar.DAY_OF_WEEK); // 현재 요일을 가져옴 (일요일부터 토요일까지 1부터 7까지의 숫자)
        return dayOfWeekNumber; // 요일 숫자 반환
    }

    // 현재 시간을 가져오는 메서드
    public static String getCurrentTime() {
        long now = System.currentTimeMillis(); // 현재 시간을 milliseconds 단위로 가져옴
        Date currentTime = new Date(now); // milliseconds를 현재 시간으로 변환하여 Date 객체 생성
        DateFormat dateFormat = new SimpleDateFormat("HH");    // 시간 형식을 지정 ("HH"는 24시간 형식)

        return dateFormat.format(currentTime);  // 현재 시간을 지정한 형식으로 문자열로 반환
    }

    // SettingActivity에서 반환된 데이터 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String updatedCity = data.getStringExtra("key"); // 데이터 가져오기
                city = updatedCity; // MainActivity에서 받은 데이터로 city 변수 업데이트
                loadWeatherInfo(city); // 날씨 정보 업데이트를 위해 API 호출
                updateUI(city); // UI 업데이트
            }
        }
    }

    private void loadWeatherInfo(String city) {
        // Retrofit을 사용하여 API 연결 설정
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")    // baseUrl 등록
                .addConverterFactory(GsonConverterFactory.create())  // Gson 변환기 등록
                .build();

        // Retrofit에서 Service 인스턴스 생성
        Service service = retrofit.create(Service.class);

        // API 호출
        Call<JsonObject> call = service.currentWeather(city, api);
        call.enqueue(new Callback<JsonObject>() {
            AlertDialog.Builder menu = new AlertDialog.Builder(MainActivity.this);

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    // API 응답 처리
                    JsonObject mainObject = response.body().getAsJsonObject("main");
                    double temp = mainObject.get("temp").getAsDouble() - 275.15; // "temp"(현재기온)값을 받아서 변환한 후 계산
                    String temp_format = "현재 " + String.format("%.2f", temp) + "°C"; // 두 번째 소수 자리까지 포맷팅
                    temp_view.setText(temp_format); // TextView에 출력

                    double feel_like = mainObject.get("feels_like").getAsDouble() - 275.15; // "temp_max"(최고기온)값을 받아서 변환한 후 계산
                    String feel_like_format = "체감 " + String.format("%.2f", feel_like) + "°C"; // 두 번째 소수 자리까지 포맷팅
                    feel_view.setText(feel_like_format); // TextView에 출력

                    JsonObject windObject = response.body().getAsJsonObject("wind"); // 바람 관련 정보 처리
                    double wind = windObject.get("speed").getAsDouble(); //"speed"(속력)값을 받아서 저장
                    String wind_format = "바람 " + String.format("%.1f", wind) + "m/s"; //첫 번째 소수 자리와 바람의 단위 포맷팅
                    wind_view.setText(wind_format); // TextView에 출력

                    double humidity = mainObject.get("humidity").getAsDouble();  //"humidity"(습도)값을 받아서 저장
                    String humidity_format = " / 습도 " + String.format("%.0f", humidity) + "%"; //소수 자리 없이 습도의 단위 포맷팅
                    humidity_view.setText(humidity_format); // TextView에 출력

                    JsonObject cloudObject = response.body().getAsJsonObject("clouds"); // 구름 관련 정보 처리
                    double cloud = cloudObject.get("all").getAsDouble(); //"cloud"(구름)값을 받아서 저장
                    String cloud_format = " / 구름 " + String.format("%.0f", cloud) + "%"; //소수 자리 없이 구름의 단위 포맷팅
                    cloud_view.setText(cloud_format); // TextView에 출력


                    JsonArray weatherArray = response.body().getAsJsonArray("weather"); // "weather" 키에 해당하는 JsonArray 추출
                    JsonObject weatherObject = weatherArray.get(0).getAsJsonObject(); // 첫 번째 요소 추출
                    String description = weatherObject.get("description").getAsString(); //"description"(상세한 날씨)값을 받아서 저장
                    description_view.setText(description); // TextView에 출력
                    String icon = weatherObject.get("icon").getAsString(); // "icon" 키의 값을 가져옴

                    String imageUrl = "https://openweathermap.org/img/wn/" + icon + "@2x.png"; // 이미지 URL을 가져옴
                    loadImage(imageUrl); //url을 loadImage함수로 이미지로 변환
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                menu.setTitle("API 호출 실패"); // API 호출 실패 시 dialog출력
                AlertDialog alertDialog = menu.create();
                alertDialog.show();
            }
        });
    }

    private void updateUI(String city) {
        city_view = findViewById(R.id.title); // 도시 제목을 표시할 TextView
        String title = city + "의 날씨";
        city_view.setText(title); // TextView에 제목 다시 설정
    }

    private void toggleFab() {
        if (isFabOpen) {
            // 현재 FAB 메뉴가 열려있는 상태일 때
            fab_main.setImageResource(R.drawable.fab_main_1); // 메인 FAB 이미지 변경
            fab_mypage.setClickable(false); // 마이페이지 FAB 클릭 불가능
            fab_search.setClickable(false); // 검색 FAB 클릭 불가능
            fab_refresh.setClickable(false); // 새로고침 FAB 클릭 불가능

            // 각 FAB를 닫힌 상태로 애니메이션을 주면서 숨김
            fab_mypage.startAnimation(fab_close);
            fab_search.startAnimation(fab_close);
            fab_refresh.startAnimation(fab_close);

            if (isSearch) {
                // 검색이 열려있는 상태일 때
                fab_google.setClickable(false); // 구글 검색 FAB 클릭 불가능
                fab_naver.setClickable(false); // 네이버 검색 FAB 클릭 불가능
                fab_daum.setClickable(false); // 다음 검색 FAB 클릭 불가능

                // 각 검색 FAB를 닫힌 상태로 애니메이션을 주면서 숨김
                fab_google.startAnimation(fab_close);
                fab_naver.startAnimation(fab_close);
                fab_daum.startAnimation(fab_close);

                isSearch = false; // 검색 FAB들을 닫은 상태로 변경
            } else {
                // 검색이 닫혀있는 상태일 때
                fab_google.setClickable(false); // 구글 검색 FAB 클릭 불가능
                fab_naver.setClickable(false); // 네이버 검색 FAB 클릭 불가능
                fab_daum.setClickable(false); // 다음 검색 FAB 클릭 불가능

                // 각 검색 FAB를 닫힌 상태로 애니메이션을 주면서 숨김
                fab_google.startAnimation(fab_clear);
                fab_naver.startAnimation(fab_clear);
                fab_daum.startAnimation(fab_clear);
            }

            isFabOpen = false; // FAB 메뉴를 닫은 상태로 변경
        } else {
            // 현재 FAB 메뉴가 닫혀있는 상태일 때
            fab_main.setImageResource(R.drawable.fab_main_2); // 메인 FAB 이미지 변경

            // 각 FAB를 보이도록 설정하고, 애니메이션을 주면서 확장시킴
            fab_mypage.setVisibility(View.VISIBLE);
            fab_search.setVisibility(View.VISIBLE);
            fab_refresh.setVisibility(View.VISIBLE);
            fab_mypage.startAnimation(fab_open);
            fab_search.startAnimation(fab_open);
            fab_refresh.startAnimation(fab_open);
            fab_mypage.setClickable(true); // 마이페이지 FAB 클릭 가능
            fab_search.setClickable(true); // 검색 FAB 클릭 가능
            fab_refresh.setClickable(true); // 새로고침 FAB 클릭 가능

            isFabOpen = true; // FAB 메뉴를 열린 상태로 변경
        }
    }

    private void toggleSearch() {
        if (isSearch) {
            // 현재 검색 FAB 메뉴가 열려있는 상태일 때
            fab_google.setClickable(false); // 구글 검색 FAB 클릭 불가능
            fab_naver.setClickable(false); // 네이버 검색 FAB 클릭 불가능
            fab_daum.setClickable(false); // 다음 검색 FAB 클릭 불가능

            // 각 검색 FAB를 닫힌 상태로 애니메이션을 주면서 숨김
            fab_google.startAnimation(fab_close);
            fab_naver.startAnimation(fab_close);
            fab_daum.startAnimation(fab_close);

            isSearch = false; // 검색 FAB 메뉴를 닫은 상태로 변경
        } else {
            // 현재 검색 FAB 메뉴가 닫혀있는 상태일 때
            fab_google.setVisibility(View.VISIBLE);
            fab_naver.setVisibility(View.VISIBLE);
            fab_daum.setVisibility(View.VISIBLE);

            // 각 검색 FAB를 보이도록 설정하고, 애니메이션을 주면서 확장시킴
            fab_google.startAnimation(fab_open);
            fab_naver.startAnimation(fab_open);
            fab_daum.startAnimation(fab_open);
            fab_mypage.startAnimation(fab_stay);
            fab_search.startAnimation(fab_stay);
            fab_refresh.startAnimation(fab_stay);
            fab_google.setClickable(true); // 구글 검색 FAB 클릭 가능
            fab_naver.setClickable(true); // 네이버 검색 FAB 클릭 가능
            fab_daum.setClickable(true); // 다음 검색 FAB 클릭 가능

            isSearch = true; // 검색 FAB 메뉴를 열린 상태로 변경
        }
    }
}