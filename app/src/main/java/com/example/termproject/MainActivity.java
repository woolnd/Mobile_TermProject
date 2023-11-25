package com.example.termproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // 기본 API 키 및 도시 설정
    String api = "097176059f7a6fe3934a296004c7abcf";
    String city = "Ulsan";
    TextView city_view, temp_view, max_view, wind_view, humidity_view, cloud_view, min_view, description_view;
    private static final int REQUEST_CODE = 1; //SettingActivity로 전환할 때 사용할 요청 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        //도시를 파라미터로 날씨 정보를 업데이트 해줌
        loadWeatherInfo(city);

        // 각종 TextView와 ImageView 초기화
        city_view = findViewById(R.id.title);
        temp_view = findViewById(R.id.temp);
        max_view = findViewById(R.id.max);
        wind_view = findViewById(R.id.wind);
        humidity_view = findViewById(R.id.humidity);
        cloud_view = findViewById(R.id.cloud);
        min_view = findViewById(R.id.min);
        description_view = findViewById(R.id.description);

        TextView date_view = findViewById(R.id.date);
        TextView week_view = findViewById(R.id.week);
        ConstraintLayout top = findViewById(R.id.top);

        ImageView setting = findViewById(R.id.setting);

        //최상단에 표시할 도시 작성
        String title = city + "의 날씨";
        city_view.setText(title);

        //getCurrentDate()함수 반환값으로 날짜 작성
        date_view.setText(getCurrentDate());

        //getCurrentWeek()함수 반환값은 1: 일요일 2:월요일..
        //숫자 값으로 요일 작성
        switch (getCurrentWeek()){
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
        if(time<18 && time >=9){
            top.setBackgroundResource(R.drawable.afternoon);
            description_view.setTextColor(Color.parseColor("#99ccff"));
            date_view.setTextColor(Color.parseColor("#99ccff"));
            week_view.setTextColor(Color.parseColor("#99ccff"));
            temp_view.setTextColor(Color.parseColor("#99ccff"));
            max_view.setTextColor(Color.parseColor("#99ccff"));
            min_view.setTextColor(Color.parseColor("#99ccff"));
            wind_view.setTextColor(Color.parseColor("#99ccff"));
            humidity_view.setTextColor(Color.parseColor("#99ccff"));
            cloud_view.setTextColor(Color.parseColor("#99ccff"));

        }
        //21시부터 06시전에는 밤에 대한 이미지와 남색 글자색 변경
        else if(time>=21 || time < 6){
            top.setBackgroundResource(R.drawable.night);
            description_view.setTextColor(Color.parseColor("#000080"));
            date_view.setTextColor(Color.parseColor("#000080"));
            week_view.setTextColor(Color.parseColor("#000080"));
            temp_view.setTextColor(Color.parseColor("#000080"));
            max_view.setTextColor(Color.parseColor("#000080"));
            min_view.setTextColor(Color.parseColor("#000080"));
            wind_view.setTextColor(Color.parseColor("#000080"));
            humidity_view.setTextColor(Color.parseColor("#000080"));
            cloud_view.setTextColor(Color.parseColor("#000080"));
        }
        //나머지 시간대는 새벽, 초저녁에 대한 이미지와 주황 글자색 변경
        else{
            top.setBackgroundResource(R.drawable.dawn);
            description_view.setTextColor(Color.parseColor("#FF9500"));
            date_view.setTextColor(Color.parseColor("#FF9500"));
            week_view.setTextColor(Color.parseColor("#FF9500"));
            temp_view.setTextColor(Color.parseColor("#FF9500"));
            max_view.setTextColor(Color.parseColor("#FF9500"));
            min_view.setTextColor(Color.parseColor("#FF9500"));
            wind_view.setTextColor(Color.parseColor("#FF9500"));
            humidity_view.setTextColor(Color.parseColor("#FF9500"));
            cloud_view.setTextColor(Color.parseColor("#FF9500"));
        }

        //setting 이미지 클릭 시 SettingActivity 화면 전환
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
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
        DateFormat dateFormat = new SimpleDateFormat("HH");	// 시간 형식을 지정 ("HH"는 24시간 형식)

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
                    String temp_format = "현재 "+String.format("%.2f", temp)+"°C"; // 두 번째 소수 자리까지 포맷팅
                    temp_view.setText(temp_format); // TextView에 출력

                    double temp_max = mainObject.get("temp_max").getAsDouble() - 275.15; // "temp_max"(최고기온)값을 받아서 변환한 후 계산
                    String temp_max_format = "최고 "+String.format("%.2f", temp_max)+"°C"; // 두 번째 소수 자리까지 포맷팅
                    max_view.setText(temp_max_format); // TextView에 출력

                    double temp_min = mainObject.get("temp_min").getAsDouble() - 275.15; // "temp_min"(최저기온)값을 받아서 변환한 후 계산
                    String temp_min_format = " / 최저 "+String.format("%.2f", temp_min)+"°C"; // 두 번째 소수 자리까지 포맷팅
                    min_view.setText(temp_min_format); // TextView에 출력

                    JsonObject windObject = response.body().getAsJsonObject("wind"); // 바람 관련 정보 처리
                    double wind = windObject.get("speed").getAsDouble(); //"speed"(속력)값을 받아서 저장
                    String wind_format = "바람 "+String.format("%.1f", wind)+"m/s"; //첫 번째 소수 자리와 바람의 단위 포맷팅
                    wind_view.setText(wind_format); // TextView에 출력

                    double humidity = mainObject.get("humidity").getAsDouble();  //"humidity"(습도)값을 받아서 저장
                    String humidity_format = " / 습도 "+String.format("%.0f", humidity)+"%"; //소수 자리 없이 습도의 단위 포맷팅
                    humidity_view.setText(humidity_format); // TextView에 출력

                    JsonObject cloudObject = response.body().getAsJsonObject("clouds"); // 구름 관련 정보 처리
                    double cloud = cloudObject.get("all").getAsDouble(); //"cloud"(구름)값을 받아서 저장
                    String cloud_format = " / 구름 "+String.format("%.0f", cloud)+"%"; //소수 자리 없이 구름의 단위 포맷팅
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
}